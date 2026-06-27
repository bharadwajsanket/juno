package bharadwaj.juno.music.ambient.ui

import bharadwaj.juno.music.ambient.model.AmbientScene
import java.util.Calendar

/**
 * Resolves the greeting text and subtitle for a given [AmbientScene] and user name.
 * Incorporates time of day, weather conditions, seasons, and emotional mood.
 */
data class AmbientGreeting(
    val greeting: String,
    val subtitle: String,
)

enum class Season { Spring, Summer, Autumn, Winter }

object AmbientSceneGreeting {

    fun resolve(scene: AmbientScene, displayName: String): AmbientGreeting {
        val season = currentSeason()
        val (greeting, subtitles) = resolveGreetingAndSubtitles(scene, displayName, season)
        
        // Rotate hourly based on epoch time to ensure consistency but fresh variety
        val hourIndex = (System.currentTimeMillis() / 3_600_000L % subtitles.size).toInt()
        val subtitle = subtitles[hourIndex]
        
        return AmbientGreeting(greeting = greeting, subtitle = subtitle)
    }

    private fun currentSeason(): Season {
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1 // 1-indexed (1 = Jan, 12 = Dec)
        return when (month) {
            3, 4, 5 -> Season.Spring
            6, 7, 8 -> Season.Summer
            9, 10, 11 -> Season.Autumn
            else -> Season.Winter
        }
    }

    private fun resolveGreetingAndSubtitles(
        scene: AmbientScene,
        name: String,
        season: Season
    ): Pair<String, List<String>> {
        val greeting = when {
            scene == AmbientScene.DeepMidnight -> "Still up, $name?"
            scene in setOf(AmbientScene.StarryNight, AmbientScene.CloudyNight, AmbientScene.RainyNight, AmbientScene.StormyNight) -> "Good Night, $name"
            scene in setOf(AmbientScene.GoldenHour, AmbientScene.CloudyGoldenHour, AmbientScene.SunsetBlaze, AmbientScene.CloudySunset, AmbientScene.RainySunset, AmbientScene.ClearEvening, AmbientScene.CloudyEvening, AmbientScene.RainyEvening) -> "Good Evening, $name"
            scene in setOf(AmbientScene.ClearNoon, AmbientScene.CloudyNoon, AmbientScene.ClearAfternoon, AmbientScene.HotAfternoon, AmbientScene.RainyAfternoon, AmbientScene.StormyAfternoon) -> "Good Afternoon, $name"
            else -> "Good Morning, $name"
        }

        // Subtitles rotate under weather, time, and season
        val subtitles = when (scene) {
            AmbientScene.SunriseGlow -> when (season) {
                Season.Spring -> listOf("First light. Watch the spring bloom.", "Fresh morning. Feel the sunrise.")
                Season.Summer -> listOf("The day begins with warmth. Breathe.", "Sunrise, clear skies. Hope is here.")
                Season.Autumn -> listOf("A crisp morning rises. Sip your tea.", "Sunrise painting the quiet leaves.")
                Season.Winter -> listOf("Cold air, first light. Keep warm.", "The winter sun rises slowly.")
            }

            AmbientScene.ClearMorning, AmbientScene.WinterMorning -> when (season) {
                Season.Spring -> listOf("Morning sun, new growth. Good vibes.", "Spring morning. Clear skies, clear mind.")
                Season.Summer -> listOf("A bright day ahead. Rise and find your rhythm.", "Soak in the morning sun.")
                Season.Autumn -> listOf("A beautiful, crisp morning. Slow down.", "Unwind with the morning light.")
                Season.Winter -> listOf("Cold morning, warm music. Welcome.", "Peaceful winter light outside.")
            }

            AmbientScene.CloudyMorning, AmbientScene.CloudyNoon -> when (season) {
                Season.Spring -> listOf("Soft shadows today. Take a breather.", "Spring clouds. Let the music flow.")
                Season.Summer -> listOf("A cool, overcast sky. Find your focus.", "Cozy noon light under the clouds.")
                Season.Autumn -> listOf("Grey skies, warm playlist. Unwind.", "Autumn breeze under cloudy canopies.")
                Season.Winter -> listOf("A quiet, white cloud ceiling.", "Cozy up inside today.")
            }

            AmbientScene.RainyMorning, AmbientScene.RainyAfternoon, AmbientScene.RainySunset, AmbientScene.RainyEvening -> when (season) {
                Season.Spring -> listOf("Let the rain choose today's rhythm.", "Spring showers bringing soft melodies.")
                Season.Summer -> listOf("Cool monsoon rain. Slower songs match the vibe.", "A warm summer rain. Slow down.")
                Season.Autumn -> listOf("Rain falling on fallen leaves. Peaceful.", "Chilly rain outside. Time for quiet keys.")
                Season.Winter -> listOf("Cold drops against the glass. Stay warm.", "Rain and winter chill. Cozy up.")
            }

            AmbientScene.StormyAfternoon, AmbientScene.StormyNight -> listOf(
                "Stay warm. Let the music handle the weather.",
                "Let the storm pass outside. Find your focus.",
                "A dramatic sky today. Lean into the heavy beats."
            )

            AmbientScene.ClearNoon, AmbientScene.ClearAfternoon -> when (season) {
                Season.Spring -> listOf("Some afternoons deserve slower songs.", "A gentle spring afternoon vibe.")
                Season.Summer -> listOf("A sun-drenched afternoon. Stay cool.", "Midday warmth, relaxed beats.")
                Season.Autumn -> listOf("Golden leaves, quiet thoughts. Hello.", "A calm autumn afternoon.")
                Season.Winter -> listOf("Soak in the winter sun. Breathe.", "Cold breeze, warm sunshine.")
            }

            AmbientScene.HotAfternoon -> listOf(
                "Find your shade and your groove.",
                "Warm afternoon vibes. Stay cool.",
                "Let the beats cool down the day."
            )

            AmbientScene.GoldenHour, AmbientScene.CloudyGoldenHour -> when (season) {
                Season.Spring -> listOf("The golden hour is here. Breathe.", "Soaking in the spring glow.")
                Season.Summer -> listOf("A long, warm evening begins.", "Everything looks golden right now.")
                Season.Autumn -> listOf("Golden sunset. Unwind from the day.", "Autumn leaves caught in golden light.")
                Season.Winter -> listOf("A short, beautiful golden sunset.", "The winter sun sets early.")
            }

            AmbientScene.SunsetBlaze, AmbientScene.CloudySunset -> when (season) {
                Season.Spring -> listOf("Sunset glow. Unwind slowly.", "Watching the spring day fade.")
                Season.Summer -> listOf("Let the blazing sunset wind down.", "A warm summer dusk is here.")
                Season.Autumn -> listOf("Red sky, quiet evening. decompress.", "A gorgeous autumn dusk.")
                Season.Winter -> listOf("A clean winter sunset. Stay cozy.", "Dusk sets in early tonight.")
            }

            AmbientScene.ClearEvening, AmbientScene.CloudyEvening -> when (season) {
                Season.Spring -> listOf("Quiet spring evening ease.", "Let the day fade gently.")
                Season.Summer -> listOf("A warm evening. Relax with the breeze.", "Cooling down after a bright day.")
                Season.Autumn -> listOf("Crisp evening air. Sip something warm.", "Quiet hours begin.")
                Season.Winter -> listOf("Stay warm inside. The night is near.", "Peaceful winter evening.")
            }

            AmbientScene.StarryNight -> listOf(
                "The world is quiet now.",
                "Just you, the stars, and the melody.",
                "Night mode on. Rest well.",
                "Clear night sky, peaceful mind."
            )

            AmbientScene.CloudyNight -> listOf(
                "Cozy night under a soft cloud cover.",
                "A quiet night to gather your thoughts.",
                "Let the music play softly in the dark."
            )

            AmbientScene.RainyNight -> listOf(
                "Rain falling outside. Let it wash the day away.",
                "Cozy storm outside. Sleep peacefully.",
                "Slick streets, quiet music."
            )

            AmbientScene.DeepMidnight -> listOf(
                "The world is asleep.",
                "Just you and the music.",
                "Deep night, deep thoughts.",
                "Midnight hours."
            )

            AmbientScene.SnowScene -> listOf(
                "A silent snow day. Cozy up.",
                "Let the snowflakes settle outside.",
                "Peaceful white landscape.",
                "Winter magic outside the window."
            )
        }

        return Pair(greeting, subtitles)
    }
}
