package com.music.innertube

import com.music.innertube.models.YouTubeClient
import kotlinx.coroutines.runBlocking
import org.junit.Test

class InnerTubeTest {

    @Test
    fun testVisitorData() {
        runBlocking {
            println("=== TEST VISITOR DATA ===")
            val result = YouTube.visitorData()
            println("VisitorData result: $result")
            if (result.isSuccess) {
                println("VisitorData value: ${result.getOrNull()}")
                YouTube.visitorData = result.getOrNull()
            } else {
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }

    @Test
    fun testPlayerPlayback() {
        runBlocking {
            println("=== TEST PLAYER PLAYBACK ===")
            // Set a default visitorData if not loaded
            val vdResult = YouTube.visitorData()
            if (vdResult.isSuccess) {
                YouTube.visitorData = vdResult.getOrNull()
                println("Initialized visitorData to: ${YouTube.visitorData}")
            }

            // Test with a sample song video ID (e.g., popular public song)
            val videoId = "dQw4w9WgXcQ" 
            val clients = listOf(
                YouTubeClient.WEB,
                YouTubeClient.WEB_REMIX,
                YouTubeClient.TVHTML5,
                YouTubeClient.TVHTML5_SIMPLY_EMBEDDED_PLAYER,
                YouTubeClient.IOS,
                YouTubeClient.MOBILE,
                YouTubeClient.ANDROID_VR_1_43_32,
                YouTubeClient.ANDROID_VR_1_61_48
            )

            for (client in clients) {
                println("\nTesting client: ${client.clientName} (version: ${client.clientVersion})")
                val playerRes = YouTube.player(
                    videoId = videoId,
                    client = client,
                    signatureTimestamp = 20000 // dummy timestamp
                )
                if (playerRes.isSuccess) {
                    val resp = playerRes.getOrNull()
                    println("  Status: ${resp?.playabilityStatus?.status}")
                    println("  Reason: ${resp?.playabilityStatus?.reason}")
                    println("  Is playable? ${resp?.playabilityStatus?.status == "OK"}")
                    println("  Has streamingData? ${resp?.streamingData != null}")
                    if (resp?.streamingData != null) {
                        println("  Adaptive Formats count: ${resp.streamingData?.adaptiveFormats?.size ?: 0}")
                    }
                } else {
                    println("  Failed to fetch: ${playerRes.exceptionOrNull()?.message}")
                    playerRes.exceptionOrNull()?.printStackTrace()
                }
            }
        }
    }
}
