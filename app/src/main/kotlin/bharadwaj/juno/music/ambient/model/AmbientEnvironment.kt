package bharadwaj.juno.music.ambient.model

enum class AmbientEnvironment {
    Ocean,
    Forest,
    Mountains,
    Meadow,
    Desert
}

object AmbientEnvironmentResolver {
    fun resolve(scene: AmbientScene): AmbientEnvironment {
        return when (scene) {
            AmbientScene.SnowScene -> AmbientEnvironment.Mountains
            AmbientScene.HotAfternoon -> AmbientEnvironment.Desert
            AmbientScene.StormyAfternoon,
            AmbientScene.StormyNight,
            AmbientScene.RainyNight,
            AmbientScene.RainyAfternoon,
            AmbientScene.RainySunset,
            AmbientScene.RainyMorning,
            AmbientScene.RainyEvening -> AmbientEnvironment.Ocean
            
            AmbientScene.ClearNoon,
            AmbientScene.ClearAfternoon,
            AmbientScene.GoldenHour,
            AmbientScene.ClearMorning -> AmbientEnvironment.Meadow
            
            AmbientScene.CloudyNoon,
            AmbientScene.CloudyMorning,
            AmbientScene.CloudyGoldenHour,
            AmbientScene.CloudySunset,
            AmbientScene.CloudyEvening,
            AmbientScene.CloudyNight -> AmbientEnvironment.Forest
            
            AmbientScene.SunriseGlow,
            AmbientScene.SunsetBlaze,
            AmbientScene.ClearEvening,
            AmbientScene.StarryNight,
            AmbientScene.DeepMidnight,
            AmbientScene.WinterMorning -> AmbientEnvironment.Mountains
            
            else -> AmbientEnvironment.Meadow
        }
    }
}
