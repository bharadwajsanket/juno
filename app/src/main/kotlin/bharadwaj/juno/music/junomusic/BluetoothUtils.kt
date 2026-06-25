package bharadwaj.juno.music.junomusic

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi


fun isBluetoothHeadphoneConnected(context: Context): Boolean {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        audioDevices.any { device ->
            device.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                    device.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
        }
    } else {
        
        @Suppress("DEPRECATION")
        audioManager.isBluetoothA2dpOn || audioManager.isBluetoothScoOn
    }
}


fun getConnectedBluetoothDeviceName(context: Context): String? {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    
    val isBluetoothActive = audioManager.isBluetoothA2dpOn || audioManager.isBluetoothScoOn
    if (!isBluetoothActive) return null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        
        val activeBluetoothDevice = audioDevices.find { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP }
            ?: audioDevices.find { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }
            
        return activeBluetoothDevice?.productName?.toString()
    } else {
        return null
    }
}


fun isBuds(name: String?): Boolean {
    if (name == null) return false
    val lowerName = name.lowercase()
    return lowerName.contains("buds") || 
           lowerName.contains("airpods") || 
           lowerName.contains("earpods") || 
           lowerName.contains("earphone") ||
           lowerName.contains("freebuds") ||
           lowerName.contains("pods")
}


fun isSpeaker(name: String?): Boolean {
    if (name == null) return false
    val lowerName = name.lowercase()
    return lowerName.contains("speaker") || 
           lowerName.contains("soundbar") || 
           lowerName.contains("homepod") || 
           lowerName.contains("___juno_protect___") ||
           lowerName.contains("boombox") ||
           lowerName.contains("audio system") ||
           lowerName.contains("sound") ||
           lowerName.contains("audio") ||
           lowerName.contains("stereo") ||
           lowerName.contains("music") ||
           lowerName.contains("box") ||
           lowerName.contains("party") ||
           lowerName.contains("waves")
}

object BluetoothCodecDetector {
    private var bluetoothA2dp: android.bluetooth.BluetoothA2dp? = null
    
    private val profileListener = object : android.bluetooth.BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: android.bluetooth.BluetoothProfile?) {
            if (profile == android.bluetooth.BluetoothProfile.A2DP) {
                bluetoothA2dp = proxy as? android.bluetooth.BluetoothA2dp
            }
        }
        override fun onServiceDisconnected(profile: Int) {
            if (profile == android.bluetooth.BluetoothProfile.A2DP) {
                bluetoothA2dp = null
            }
        }
    }

    fun initialize(context: Context) {
        try {
            val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? android.bluetooth.BluetoothManager
            val adapter = bluetoothManager?.adapter ?: android.bluetooth.BluetoothAdapter.getDefaultAdapter()
            adapter?.getProfileProxy(context, profileListener, android.bluetooth.BluetoothProfile.A2DP)
        } catch (e: Exception) {
            // Ignore any issues during initialization
        }
    }

    fun getActiveDeviceCodecName(): String {
        val a2dp = bluetoothA2dp ?: return "Unknown"
        try {
            val activeDeviceMethod = a2dp.javaClass.getDeclaredMethod("getActiveDevice")
            val activeDevice = activeDeviceMethod.invoke(a2dp) as? android.bluetooth.BluetoothDevice
            val device = activeDevice ?: a2dp.connectedDevices.firstOrNull() ?: return "Unknown"

            val getCodecStatusMethod = a2dp.javaClass.getDeclaredMethod("getCodecStatus", android.bluetooth.BluetoothDevice::class.java)
            val codecStatus = getCodecStatusMethod.invoke(a2dp, device) ?: return "Unknown"

            val getCodecConfigMethod = codecStatus.javaClass.getDeclaredMethod("getCodecConfig")
            val codecConfig = getCodecConfigMethod.invoke(codecStatus) ?: return "Unknown"

            val getCodecTypeMethod = codecConfig.javaClass.getDeclaredMethod("getCodecType")
            val codecType = getCodecTypeMethod.invoke(codecConfig) as? Int ?: return "Unknown"

            return when (codecType) {
                0 -> "SBC"
                1 -> "AAC"
                2 -> "aptX"
                3 -> "aptX HD"
                4 -> "LDAC"
                5 -> "LC3"
                else -> "Unknown"
            }
        } catch (e: Exception) {
            return "Unknown"
        }
    }
}
