package bharadwaj.juno.music.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import bharadwaj.juno.music.constants.EnableHapticsKey

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

        try {
            when (type) {
                HapticType.LIGHT -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                    } else {
                        @Suppress("DEPRECATION")
                        v.vibrate(10)
                    }
                }
                HapticType.MEDIUM -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
                    } else {
                        @Suppress("DEPRECATION")
                        v.vibrate(25)
                    }
                }
                HapticType.HEAVY -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                    } else {
                        @Suppress("DEPRECATION")
                        v.vibrate(40)
                    }
                }
                HapticType.MICRO_TICK -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(5, 40))
                    } else {
                        @Suppress("DEPRECATION")
                        v.vibrate(5)
                    }
                }
                HapticType.SLIDER_TICK -> {
                    val now = System.currentTimeMillis()
                    if (now - lastSliderTickTime >= 45) {
                        lastSliderTickTime = now
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(3, 30))
                        } else {
                            @Suppress("DEPRECATION")
                            v.vibrate(3)
                        }
                    }
                }
                HapticType.BOUNDARY -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                    } else {
                        @Suppress("DEPRECATION")
                        v.vibrate(longArrayOf(0, 10, 40, 10), -1)
                    }
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
