package bharadwaj.juno.music

import android.net.FakeConnectivityManager
import bharadwaj.juno.music.constants.AudioQuality
import bharadwaj.juno.music.utils.YTPlayerUtils
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.net.HttpURLConnection
import java.net.URL

class PlaybackDiagnosticsTest {

    @Test
    fun testPlayback() {
        runBlocking {
            println("=== START PLAYBACK DIAGNOSTICS UNIT TEST ===")
            
            try {
                println("Fetching visitorData...")
                val visitorDataResult = YTPlayerUtils.refreshVisitorData()
                println("visitorData result: $visitorDataResult")
            } catch (e: Exception) {
                println("Failed to fetch visitorData: ${e.message}")
            }

            val videoId = "dQw4w9WgXcQ"
            val result = YTPlayerUtils.playerResponseForPlayback(
                videoId = videoId,
                audioQuality = AudioQuality.OPUS,
                connectivityManager = FakeConnectivityManager()
            )
            println("Result: $result")
            if (result.isSuccess) {
                val playbackData = result.getOrNull()
                println("Success! PlaybackData obtained:")
                println("  Format: ${playbackData?.format?.mimeType}, bitrate: ${playbackData?.format?.bitrate}")
                println("  Stream URL: ${playbackData?.streamUrl}")
                println("  Expires: ${playbackData?.streamExpiresInSeconds}s")
                
                playbackData?.streamUrl?.let { urlString ->
                    try {
                        val url = URL(urlString)
                        val connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "GET"
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        connection.connectTimeout = 5000
                        connection.readTimeout = 5000
                        val responseCode = connection.responseCode
                        println("  GET URL response code: $responseCode")
                        if (responseCode >= 400) {
                            val errorStream = connection.errorStream?.bufferedReader()?.readText()
                            println("  Error stream: $errorStream")
                        }
                    } catch (e: Exception) {
                        println("  Failed to connect to stream URL: ${e.message}")
                        e.printStackTrace()
                    }
                }
            } else {
                val exception = result.exceptionOrNull()
                println("Playback resolution failed: ${exception?.message}")
                exception?.printStackTrace()
            }
        }
    }
}
