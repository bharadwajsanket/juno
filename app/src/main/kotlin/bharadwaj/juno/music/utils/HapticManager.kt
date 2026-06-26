package bharadwaj.juno.music.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import bharadwaj.juno.music.constants.EnableHapticsKey
import bharadwaj.juno.music.constants.HapticIntensityKey

enum class HapticType {
    LIGHT,
    MEDIUM,
    HEAVY,
    MICRO_TICK,
    SLIDER_TICK,
    BOUNDARY
}

class HapticManager private constructor(context: Context) {

    private val appContext = context.applicationContext
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var lastSliderTickTime = 0L

    companion object {
        @Volatile
        private var instance: HapticManager? = null

        fun getInstance(context: Context): HapticManager {
            return instance ?: synchronized(this) {
                instance ?: HapticManager(context).also { instance = it }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun isHapticFeedbackEnabled(): Boolean {
        return try {
            Settings.System.getInt(
                appContext.contentResolver,
                Settings.System.HAPTIC_FEEDBACK_ENABLED,
                1
            ) != 0
        } catch (e: Exception) {
            true
        }
    }

    fun performHaptic(type: HapticType) {
        val systemEnabled = isHapticFeedbackEnabled()
        val prefEnabled = appContext.dataStore.get(EnableHapticsKey, true)
        val hasVibratorObj = vibrator != null && vibrator.hasVibrator()
        android.util.Log.d("HapticManager", "performHaptic: type=$type, systemEnabled=$systemEnabled, prefEnabled=$prefEnabled, hasVibrator=$hasVibratorObj")

        if (!systemEnabled) return
        if (!prefEnabled) return
        val v = vibrator ?: return
        if (!v.hasVibrator()) return

        val intensity = appContext.dataStore.get(HapticIntensityKey, 0.6f)
        val baseAmplitude = (intensity * 255).toInt().coerceIn(1, 255)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                when (type) {
                    HapticType.LIGHT -> {
                        val amp = (baseAmplitude * 0.4f).toInt().coerceIn(1, 255)
                        v.vibrate(VibrationEffect.createOneShot(10, amp))
                    }
                    HapticType.MEDIUM -> {
                        val amp = (baseAmplitude * 0.7f).toInt().coerceIn(1, 255)
                        v.vibrate(VibrationEffect.createOneShot(20, amp))
                    }
                    HapticType.HEAVY -> {
                        v.vibrate(VibrationEffect.createOneShot(35, baseAmplitude))
                    }
                    HapticType.MICRO_TICK -> {
                        val amp = (baseAmplitude * 0.25f).toInt().coerceIn(1, 255)
                        v.vibrate(VibrationEffect.createOneShot(5, amp))
                    }
                    HapticType.SLIDER_TICK -> {
                        val now = System.currentTimeMillis()
                        if (now - lastSliderTickTime >= 45) {
                            lastSliderTickTime = now
                            val amp = (baseAmplitude * 0.2f).toInt().coerceIn(1, 255)
                            v.vibrate(VibrationEffect.createOneShot(3, amp))
                        }
                    }
                    HapticType.BOUNDARY -> {
                        val amp1 = (baseAmplitude * 0.5f).toInt().coerceIn(1, 255)
                        v.vibrate(
                            VibrationEffect.createWaveform(
                                longArrayOf(0, 10, 40, 10),
                                intArrayOf(0, amp1, 0, baseAmplitude),
                                -1
                            )
                        )
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                when (type) {
                    HapticType.LIGHT -> v.vibrate(10)
                    HapticType.MEDIUM -> v.vibrate(25)
                    HapticType.HEAVY -> v.vibrate(40)
                    HapticType.MICRO_TICK -> v.vibrate(5)
                    HapticType.SLIDER_TICK -> {
                        val now = System.currentTimeMillis()
                        if (now - lastSliderTickTime >= 45) {
                            lastSliderTickTime = now
                            v.vibrate(3)
                        }
                    }
                    HapticType.BOUNDARY -> v.vibrate(longArrayOf(0, 10, 40, 10), -1)
                }
            }
        } catch (e: Exception) {
            // Ignore failure
        }
    }

    fun performTick() {
        performHaptic(HapticType.LIGHT)
    }

    fun performClick() {
        performHaptic(HapticType.LIGHT)
    }

    fun performMediumClick() {
        performHaptic(HapticType.MEDIUM)
    }

    fun performMicroTick() {
        performHaptic(HapticType.SLIDER_TICK)
    }

    fun performBoundaryFeedback() {
        performHaptic(HapticType.BOUNDARY)
    }
}
