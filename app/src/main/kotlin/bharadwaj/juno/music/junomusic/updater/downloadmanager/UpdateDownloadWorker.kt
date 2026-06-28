package bharadwaj.juno.music.junomusic.updater.downloadmanager

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import bharadwaj.juno.music.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream

class UpdateDownloadWorker(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val apkUrl = inputData.getString("apk_url") ?: return@withContext Result.failure()
        val version = inputData.getString("version") ?: "unknown"
        val fileSize = inputData.getString("file_size") ?: ""

        if (runAttemptCount > 5) {
            timber.log.Timber.e("Update download failed: maximum retry limit (5) reached")
            DownloadNotificationManager.showDownloadFailed(version, "Max retry limit reached")
            return@withContext Result.failure()
        }

        DownloadNotificationManager.showDownloadStarting(version, fileSize)

        val downloadDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "juno_updates"
        )
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }

        val isZip = apkUrl.contains("nightly.link") || apkUrl.endsWith(".zip")
        val downloadFile = if (isZip) File(downloadDir, "juno_temp.zip") else File(downloadDir, "junomusic.apk")

        fun openConnection(urlStr: String, startRange: Long): HttpURLConnection {
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 15000
            conn.readTimeout = 15000
            if (startRange > 0) {
                conn.setRequestProperty("Range", "bytes=$startRange-")
            }
            return conn
        }

        try {
            var existingLength = if (downloadFile.exists()) downloadFile.length() else 0L
            timber.log.Timber.d("Starting update download from URL: $apkUrl (existing file size: $existingLength bytes)")

            var connection = openConnection(apkUrl, existingLength)
            var responseCode = connection.responseCode

            // If range request was not acceptable or file is stale/invalid, start from scratch
            if (responseCode == 416 || (existingLength > 0 && responseCode != HttpURLConnection.HTTP_PARTIAL)) {
                timber.log.Timber.d("Server returned response $responseCode. Resetting download from scratch.")
                connection.disconnect()
                if (downloadFile.exists()) {
                    downloadFile.delete()
                }
                existingLength = 0L
                connection = openConnection(apkUrl, existingLength)
                responseCode = connection.responseCode
            }

            if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_PARTIAL) {
                connection.disconnect()
                timber.log.Timber.e("Server returned HTTP error response: $responseCode")
                DownloadNotificationManager.showDownloadFailed(
                    version,
                    context.getString(R.string.server_error, responseCode.toString())
                )
                return@withContext Result.retry()
            }

            val append = responseCode == HttpURLConnection.HTTP_PARTIAL
            val fileLength = if (append) {
                connection.contentLengthLong + existingLength
            } else {
                connection.contentLengthLong
            }

            val inputStream = connection.inputStream
            val outputStream = FileOutputStream(downloadFile, append)

            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalBytesRead: Long = if (append) existingLength else 0L

            timber.log.Timber.d("Downloading APK payload. Append mode = $append, Expected total size = $fileLength bytes")

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                if (isStopped) {
                    outputStream.close()
                    inputStream.close()
                    connection.disconnect()
                    timber.log.Timber.d("Download paused or stopped by WorkManager. Local cached bytes: ${downloadFile.length()}")
                    return@withContext Result.retry()
                }

                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead

                if (fileLength > 0) {
                    val progress = (totalBytesRead.toFloat() / fileLength.toFloat() * 100).toInt()
                    DownloadNotificationManager.updateDownloadProgress(progress, version)
                    setProgress(workDataOf("progress" to progress.toFloat() / 100f))
                }
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
            connection.disconnect()

            val finalFile = if (isZip) {
                val targetApkFile = File(downloadDir, "junomusic.apk")
                var extracted = false
                try {
                    ZipInputStream(downloadFile.inputStream()).use { zis ->
                        var entry = zis.nextEntry
                        while (entry != null) {
                            if (!entry.isDirectory && entry.name.endsWith(".apk")) {
                                FileOutputStream(targetApkFile).use { fos ->
                                    zis.copyTo(fos)
                                }
                                extracted = true
                                break
                            }
                            entry = zis.nextEntry
                        }
                    }
                } catch (e: Exception) {
                    timber.log.Timber.e(e, "ZIP extraction error")
                    if (downloadFile.exists()) downloadFile.delete()
                    DownloadNotificationManager.showDownloadFailed(
                        version,
                        e.message ?: "Failed to extract zip file"
                    )
                    return@withContext Result.failure()
                } finally {
                    if (downloadFile.exists()) {
                        downloadFile.delete()
                    }
                }
                if (!extracted) {
                    DownloadNotificationManager.showDownloadFailed(
                        version,
                        "Could not find APK in zip"
                    )
                    return@withContext Result.failure()
                }
                targetApkFile
            } else {
                downloadFile
            }

            if (version.startsWith("nightly-r")) {
                val runNumberString = version.removePrefix("nightly-r")
                val runNumber = runNumberString.toIntOrNull()
                if (runNumber != null) {
                    val sharedPreferences = context.getSharedPreferences("update_settings", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putInt("last_installed_nightly_run", runNumber).apply()
                }
            }

            timber.log.Timber.i("Update download successfully completed: ${finalFile.absolutePath}")
            DownloadNotificationManager.showDownloadComplete(version, finalFile.absolutePath)

            Result.success(workDataOf("file_path" to finalFile.absolutePath))
        } catch (e: java.io.IOException) {
            timber.log.Timber.w("Transient network error during update download: ${e.message}. Retrying...")
            Result.retry()
        } catch (e: Exception) {
            timber.log.Timber.e(e, "Fatal exception during update download")
            DownloadNotificationManager.showDownloadFailed(
                version,
                e.message ?: context.getString(R.string.download_failed)
            )
            Result.failure()
        }
    }
}
