package bharadwaj.juno.music.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.sin

enum class HeaderLayoutType {
    LEFT_ALIGN,
    CENTER_ALIGN,
    RIGHT_ALIGN,
    SPLIT_LAYOUT
}

enum class TimePeriod {
    MORNING,
    AFTERNOON,
    EVENING,
    NIGHT
}

data class HomeHeaderTheme(
    val id: Int,
    val timePeriod: TimePeriod,
    val greetingTemplate: String, // supports "{name}"
    val subtitle: String,
    val backgroundBrush: Brush,
    val accentColor: Color,
    val textColor: Color,
    val subtitleColor: Color,
    val layoutType: HeaderLayoutType,
    val illustration: @Composable (modifier: Modifier, accentColor: Color) -> Unit
)

object HomeHeaderThemes {
    val themes = listOf(
        // ================= MORNING (05:00 - 11:59) =================
        // 1. Morning Sunrise
        HomeHeaderTheme(
            id = 1,
            timePeriod = TimePeriod.MORNING,
            greetingTemplate = "Good Morning, {name}",
            subtitle = "Let’s make today beautiful.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFFFFF9E6), Color(0xFFFFE0B2))
            ),
            accentColor = Color(0xFFF57C00),
            textColor = Color(0xFF5D4037),
            subtitleColor = Color(0xFF8D6E63),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    // Draw soft sun
                    drawCircle(
                        color = accent.copy(alpha = 0.15f),
                        radius = h * 0.4f,
                        center = Offset(w * 0.8f, h * 0.6f)
                    )
                    drawCircle(
                        color = accent.copy(alpha = 0.25f),
                        radius = h * 0.4f,
                        center = Offset(w * 0.8f, h * 0.6f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawLine(
                        color = accent.copy(alpha = 0.3f),
                        start = Offset(w * 0.5f, h * 0.6f),
                        end = Offset(w * 0.95f, h * 0.6f),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        ),

        // 2. Coffee Morning
        HomeHeaderTheme(
            id = 2,
            timePeriod = TimePeriod.MORNING,
            greetingTemplate = "Good Morning, {name}",
            subtitle = "A fresh day deserves a fresh playlist.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFFEFEBE9), Color(0xFFD7CCC8))
            ),
            accentColor = Color(0xFF6D4C41),
            textColor = Color(0xFF3E2723),
            subtitleColor = Color(0xFF5D4037),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    val cx = w * 0.82f
                    val cy = h * 0.55f
                    val mw = 18.dp.toPx()
                    val mh = 20.dp.toPx()
                    // Mug
                    drawRoundRect(
                        color = accent.copy(alpha = 0.22f),
                        topLeft = Offset(cx - mw / 2, cy - mh / 2),
                        size = Size(mw, mh),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                    // Handle
                    drawArc(
                        color = accent.copy(alpha = 0.22f),
                        startAngle = -90f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(cx + mw / 2 - 4.dp.toPx(), cy - mh / 3),
                        size = Size(8.dp.toPx(), mh * 0.6f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    // Steam
                    drawLine(
                        color = accent.copy(alpha = 0.25f),
                        start = Offset(cx - 3.dp.toPx(), cy - mh / 2 - 2.dp.toPx()),
                        end = Offset(cx - 5.dp.toPx(), cy - mh / 2 - 10.dp.toPx()),
                        strokeWidth = 1.5f.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = accent.copy(alpha = 0.25f),
                        start = Offset(cx + 3.dp.toPx(), cy - mh / 2 - 2.dp.toPx()),
                        end = Offset(cx + 1.dp.toPx(), cy - mh / 2 - 10.dp.toPx()),
                        strokeWidth = 1.5f.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        ),

        // 3. Morning Birds
        HomeHeaderTheme(
            id = 3,
            timePeriod = TimePeriod.MORNING,
            greetingTemplate = "Good Morning, {name}",
            subtitle = "Start your morning with a warm melody.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFFFFF0F5), Color(0xFFFFD1A9))
            ),
            accentColor = Color(0xFFE65100),
            textColor = Color(0xFF4E342E),
            subtitleColor = Color(0xFF8D6E63),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    // Flying birds paths
                    val bird1 = Path().apply {
                        moveTo(w * 0.72f, h * 0.4f)
                        quadraticTo(w * 0.75f, h * 0.35f, w * 0.78f, h * 0.4f)
                        quadraticTo(w * 0.81f, h * 0.35f, w * 0.84f, h * 0.4f)
                        quadraticTo(w * 0.81f, h * 0.38f, w * 0.78f, h * 0.42f)
                        quadraticTo(w * 0.75f, h * 0.38f, w * 0.72f, h * 0.4f)
                        close()
                    }
                    val bird2 = Path().apply {
                        moveTo(w * 0.82f, h * 0.52f)
                        quadraticTo(w * 0.84f, h * 0.48f, w * 0.86f, h * 0.52f)
                        quadraticTo(w * 0.88f, h * 0.48f, w * 0.9f, h * 0.52f)
                        quadraticTo(w * 0.88f, h * 0.5f, w * 0.86f, h * 0.54f)
                        quadraticTo(w * 0.84f, h * 0.5f, w * 0.82f, h * 0.52f)
                        close()
                    }
                    drawPath(bird1, accent.copy(alpha = 0.25f))
                    drawPath(bird2, accent.copy(alpha = 0.25f))
                }
            }
        ),

        // 4. Fresh Start
        HomeHeaderTheme(
            id = 4,
            timePeriod = TimePeriod.MORNING,
            greetingTemplate = "Good Morning, {name}",
            subtitle = "Rise and shine, your soundtrack is ready.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFFF9FBE7), Color(0xFFF0F4C3))
            ),
            accentColor = Color(0xFF827717),
            textColor = Color(0xFF33691E),
            subtitleColor = Color(0xFF558B2F),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    val px = w * 0.82f
                    val py = h * 0.7f
                    // Draw fresh growing leaf sprout
                    val leaf1 = Path().apply {
                        moveTo(px, py)
                        quadraticTo(px - 16.dp.toPx(), py - 18.dp.toPx(), px - 20.dp.toPx(), py - 16.dp.toPx())
                        quadraticTo(px - 12.dp.toPx(), py - 10.dp.toPx(), px, py)
                    }
                    val leaf2 = Path().apply {
                        moveTo(px, py)
                        quadraticTo(px + 16.dp.toPx(), py - 22.dp.toPx(), px + 22.dp.toPx(), py - 20.dp.toPx())
                        quadraticTo(px + 12.dp.toPx(), py - 12.dp.toPx(), px, py)
                    }
                    drawPath(leaf1, accent.copy(alpha = 0.22f))
                    drawPath(leaf2, accent.copy(alpha = 0.22f))
                    drawLine(
                        color = accent.copy(alpha = 0.3f),
                        start = Offset(px, py + 8.dp.toPx()),
                        end = Offset(px, py - 4.dp.toPx()),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        ),

        // ================= AFTERNOON (12:00 - 16:59) =================
        // 5. Bright Sky
        HomeHeaderTheme(
            id = 5,
            timePeriod = TimePeriod.AFTERNOON,
            greetingTemplate = "Good Afternoon, {name}",
            subtitle = "Hope your day is going well.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFFE0F7FA), Color(0xFF80DEEA))
            ),
            accentColor = Color(0xFF00838F),
            textColor = Color(0xFF004D40),
            subtitleColor = Color(0xFF006064),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    // Calm Clouds
                    drawCircle(
                        color = Color.White.copy(alpha = 0.35f),
                        radius = h * 0.32f,
                        center = Offset(w * 0.76f, h * 0.45f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.25f),
                        radius = h * 0.26f,
                        center = Offset(w * 0.88f, h * 0.52f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.2f),
                        radius = h * 0.22f,
                        center = Offset(w * 0.68f, h * 0.58f)
                    )
                }
            }
        ),

        // 6. Afternoon Focus
        HomeHeaderTheme(
            id = 6,
            timePeriod = TimePeriod.AFTERNOON,
            greetingTemplate = "Good Afternoon, {name}",
            subtitle = "Take a small music break.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFFECEFF1), Color(0xFFB0BEC5))
            ),
            accentColor = Color(0xFF37474F),
            textColor = Color(0xFF263238),
            subtitleColor = Color(0xFF455A64),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    val cx = w * 0.8f
                    val cy = h * 0.5f
                    val r = h * 0.26f
                    // Headband
                    drawArc(
                        color = accent.copy(alpha = 0.2f),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(cx - r, cy - r),
                        size = Size(r * 2, r * 2),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Earcups
                    drawRoundRect(
                        color = accent.copy(alpha = 0.25f),
                        topLeft = Offset(cx - r - 3.dp.toPx(), cy - 8.dp.toPx()),
                        size = Size(8.dp.toPx(), 20.dp.toPx()),
                        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                    )
                    drawRoundRect(
                        color = accent.copy(alpha = 0.25f),
                        topLeft = Offset(cx + r - 5.dp.toPx(), cy - 8.dp.toPx()),
                        size = Size(8.dp.toPx(), 20.dp.toPx()),
                        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                    )
                }
            }
        ),

        // 7. Calm Productivity
        HomeHeaderTheme(
            id = 7,
            timePeriod = TimePeriod.AFTERNOON,
            greetingTemplate = "Good Afternoon, {name}",
            subtitle = "Calm tunes for productive hours.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFFE8F5E9), Color(0xFFA5D6A7))
            ),
            accentColor = Color(0xFF2E7D32),
            textColor = Color(0xFF1B5E20),
            subtitleColor = Color(0xFF388E3C),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    val potWidth = 22.dp.toPx()
                    val potHeight = 16.dp.toPx()
                    val px = w * 0.82f
                    val py = h * 0.68f
                    // Pot
                    val potPath = Path().apply {
                        moveTo(px - potWidth / 2, py)
                        lineTo(px + potWidth / 2, py)
                        lineTo(px + potWidth * 0.4f, py + potHeight)
                        lineTo(px - potWidth * 0.4f, py + potHeight)
                        close()
                    }
                    drawPath(path = potPath, color = accent.copy(alpha = 0.22f))
                    // Plant leaves
                    val stem1 = Path().apply {
                        moveTo(px, py)
                        quadraticTo(px - 14.dp.toPx(), py - 20.dp.toPx(), px - 18.dp.toPx(), py - 18.dp.toPx())
                        quadraticTo(px - 10.dp.toPx(), py - 12.dp.toPx(), px, py)
                    }
                    val stem2 = Path().apply {
                        moveTo(px, py)
                        quadraticTo(px + 14.dp.toPx(), py - 24.dp.toPx(), px + 20.dp.toPx(), py - 22.dp.toPx())
                        quadraticTo(px + 10.dp.toPx(), py - 14.dp.toPx(), px, py)
                    }
                    drawPath(path = stem1, color = accent.copy(alpha = 0.25f))
                    drawPath(path = stem2, color = accent.copy(alpha = 0.25f))
                }
            }
        ),

        // 8. Equalizer Bloom
        HomeHeaderTheme(
            id = 8,
            timePeriod = TimePeriod.AFTERNOON,
            greetingTemplate = "Good Afternoon, {name}",
            subtitle = "A little harmony for your afternoon.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFFE8EAF6), Color(0xFFC5CAE9))
            ),
            accentColor = Color(0xFF283593),
            textColor = Color(0xFF1A237E),
            subtitleColor = Color(0xFF303F9F),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    val startX = w * 0.72f
                    val baseLine = h * 0.7f
                    val barWidth = 4.dp.toPx()
                    val gap = 6.dp.toPx()
                    val heights = listOf(14.dp, 28.dp, 20.dp, 36.dp, 10.dp)
                    heights.forEachIndexed { i, height ->
                        val hPx = height.toPx()
                        drawRoundRect(
                            color = accent.copy(alpha = 0.2f),
                            topLeft = Offset(startX + i * (barWidth + gap), baseLine - hPx),
                            size = Size(barWidth, hPx),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )
                    }
                }
            }
        ),

        // ================= EVENING (17:00 - 20:59) =================
        // 9. Sunset Chill
        HomeHeaderTheme(
            id = 9,
            timePeriod = TimePeriod.EVENING,
            greetingTemplate = "Good Evening, {name}",
            subtitle = "Slow down and unwind.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFFFFF3E0), Color(0xFFFFD180), Color(0xFFEA80FC))
            ),
            accentColor = Color(0xFFE65100),
            textColor = Color(0xFF3E2723),
            subtitleColor = Color(0xFF4A148C),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    val sunRadius = h * 0.28f
                    drawArc(
                        color = accent.copy(alpha = 0.2f),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(w * 0.8f - sunRadius, h * 0.65f - sunRadius),
                        size = Size(sunRadius * 2, sunRadius * 2)
                    )
                    drawLine(
                        color = accent.copy(alpha = 0.25f),
                        start = Offset(w * 0.62f, h * 0.65f),
                        end = Offset(w * 0.94f, h * 0.65f),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = accent.copy(alpha = 0.15f),
                        start = Offset(w * 0.68f, h * 0.72f),
                        end = Offset(w * 0.88f, h * 0.72f),
                        strokeWidth = 1.5f.dp.toPx()
                    )
                }
            }
        ),

        // 10. City Lights
        HomeHeaderTheme(
            id = 10,
            timePeriod = TimePeriod.EVENING,
            greetingTemplate = "Good Evening, {name}",
            subtitle = "Your evening soundtrack is waiting.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFF2C2A4A), Color(0xFF4F3B78))
            ),
            accentColor = Color(0xFFFFD54F),
            textColor = Color(0xFFECEFF1),
            subtitleColor = Color(0xFFD1C4E9),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    drawRect(
                        color = accent.copy(alpha = 0.15f),
                        topLeft = Offset(w * 0.72f, h * 0.5f),
                        size = Size(20.dp.toPx(), h * 0.5f)
                    )
                    drawRect(
                        color = accent.copy(alpha = 0.2f),
                        topLeft = Offset(w * 0.78f, h * 0.4f),
                        size = Size(24.dp.toPx(), h * 0.6f)
                    )
                    drawRect(
                        color = accent.copy(alpha = 0.12f),
                        topLeft = Offset(w * 0.86f, h * 0.55f),
                        size = Size(22.dp.toPx(), h * 0.45f)
                    )
                    drawCircle(color = Color.White.copy(alpha = 0.35f), radius = 2.dp.toPx(), center = Offset(w * 0.75f, h * 0.6f))
                    drawCircle(color = Color.White.copy(alpha = 0.35f), radius = 2.dp.toPx(), center = Offset(w * 0.8f, h * 0.5f))
                    drawCircle(color = Color.White.copy(alpha = 0.35f), radius = 2.dp.toPx(), center = Offset(w * 0.82f, h * 0.62f))
                }
            }
        ),

        // 11. Relaxed Atmosphere
        HomeHeaderTheme(
            id = 11,
            timePeriod = TimePeriod.EVENING,
            greetingTemplate = "Good Evening, {name}",
            subtitle = "Unwind with the setting sun.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFFFBE9E7), Color(0xFFFFCCBC), Color(0xFFD1C4E9))
            ),
            accentColor = Color(0xFFD84315),
            textColor = Color(0xFF3E2723),
            subtitleColor = Color(0xFF5E35B1),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    drawCircle(
                        color = Color(0xFFE91E63).copy(alpha = 0.08f),
                        radius = h * 0.42f,
                        center = Offset(w * 0.74f, h * 0.45f)
                    )
                    drawCircle(
                        color = accent.copy(alpha = 0.08f),
                        radius = h * 0.35f,
                        center = Offset(w * 0.86f, h * 0.55f)
                    )
                    drawCircle(
                        color = Color(0xFFFFEB3B).copy(alpha = 0.08f),
                        radius = h * 0.25f,
                        center = Offset(w * 0.8f, h * 0.3f)
                    )
                }
            }
        ),

        // 12. Rainy Evening
        HomeHeaderTheme(
            id = 12,
            timePeriod = TimePeriod.EVENING,
            greetingTemplate = "Good Evening, {name}",
            subtitle = "Ease into the evening hours.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFFCFD8DC), Color(0xFF90A4AE))
            ),
            accentColor = Color(0xFF455A64),
            textColor = Color(0xFF263238),
            subtitleColor = Color(0xFF37474F),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    val cloudPath = Path().apply {
                        moveTo(w * 0.7f, h * 0.5f)
                        quadraticTo(w * 0.73f, h * 0.35f, w * 0.8f, h * 0.4f)
                        quadraticTo(w * 0.87f, h * 0.38f, w * 0.9f, h * 0.5f)
                        quadraticTo(w * 0.95f, h * 0.55f, w * 0.9f, h * 0.62f)
                        lineTo(w * 0.7f, h * 0.62f)
                        close()
                    }
                    drawPath(path = cloudPath, color = accent.copy(alpha = 0.15f))
                    drawLine(
                        color = accent.copy(alpha = 0.3f),
                        start = Offset(w * 0.75f, h * 0.68f),
                        end = Offset(w * 0.72f, h * 0.78f),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = accent.copy(alpha = 0.3f),
                        start = Offset(w * 0.82f, h * 0.68f),
                        end = Offset(w * 0.79f, h * 0.78f),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = accent.copy(alpha = 0.3f),
                        start = Offset(w * 0.88f, h * 0.68f),
                        end = Offset(w * 0.85f, h * 0.78f),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        ),

        // ================= NIGHT (21:00 - 04:59) =================
        // 13. Night Moon & Stars
        HomeHeaderTheme(
            id = 13,
            timePeriod = TimePeriod.NIGHT,
            greetingTemplate = "Good Night, {name}",
            subtitle = "One last playlist before sleep.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFF1E2640), Color(0xFF0F1423))
            ),
            accentColor = Color(0xFFFFD54F),
            textColor = Color(0xFFECEFF1),
            subtitleColor = Color(0xFFB0BEC5),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    val moonPath = Path().apply {
                        moveTo(w * 0.8f, h * 0.3f)
                        quadraticTo(w * 0.72f, h * 0.5f, w * 0.8f, h * 0.7f)
                        quadraticTo(w * 0.86f, h * 0.5f, w * 0.8f, h * 0.3f)
                    }
                    drawPath(path = moonPath, color = accent.copy(alpha = 0.25f))
                    drawCircle(color = Color.White.copy(alpha = 0.4f), radius = 2.dp.toPx(), center = Offset(w * 0.65f, h * 0.35f))
                    drawCircle(color = Color.White.copy(alpha = 0.4f), radius = 3.dp.toPx(), center = Offset(w * 0.72f, h * 0.25f))
                    drawCircle(color = Color.White.copy(alpha = 0.4f), radius = 1.5f.dp.toPx(), center = Offset(w * 0.6f, h * 0.55f))
                }
            }
        ),

        // 14. Starry Constellation
        HomeHeaderTheme(
            id = 14,
            timePeriod = TimePeriod.NIGHT,
            greetingTemplate = "Good Night, {name}",
            subtitle = "Let music end the day softly.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFF110D2C), Color(0xFF221A4B))
            ),
            accentColor = Color(0xFFFFD54F),
            textColor = Color(0xFFECEFF1),
            subtitleColor = Color(0xFFB0BEC5),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    val p1 = Offset(w * 0.72f, h * 0.32f)
                    val p2 = Offset(w * 0.78f, h * 0.5f)
                    val p3 = Offset(w * 0.88f, h * 0.4f)
                    drawLine(
                        color = Color.White.copy(alpha = 0.12f),
                        start = p1,
                        end = p2,
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.12f),
                        start = p2,
                        end = p3,
                        strokeWidth = 1.dp.toPx()
                    )
                    drawCircle(color = accent.copy(alpha = 0.35f), radius = 4.dp.toPx(), center = p1)
                    drawCircle(color = accent.copy(alpha = 0.35f), radius = 4.dp.toPx(), center = p2)
                    drawCircle(color = accent.copy(alpha = 0.35f), radius = 4.dp.toPx(), center = p3)
                }
            }
        ),

        // 15. Cozy Midnight Glow
        HomeHeaderTheme(
            id = 15,
            timePeriod = TimePeriod.NIGHT,
            greetingTemplate = "Good Night, {name}",
            subtitle = "Cozy up with some late-night tunes.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFF0F0C1B), Color(0xFF2C1E3D))
            ),
            accentColor = Color(0xFFFFD54F),
            textColor = Color(0xFFECEFF1),
            subtitleColor = Color(0xFFB0BEC5),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    val cx = w * 0.8f
                    val cy = h * 0.55f
                    drawCircle(
                        color = accent.copy(alpha = 0.08f),
                        radius = h * 0.45f,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = accent.copy(alpha = 0.15f),
                        radius = h * 0.25f,
                        center = Offset(cx, cy)
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.2f),
                        start = Offset(cx, cy + 10.dp.toPx()),
                        end = Offset(cx, cy + 24.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.2f),
                        start = Offset(cx - 8.dp.toPx(), cy + 24.dp.toPx()),
                        end = Offset(cx + 8.dp.toPx(), cy + 24.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                    val flamePath = Path().apply {
                        moveTo(cx, cy - 8.dp.toPx())
                        quadraticTo(cx - 5.dp.toPx(), cy + 4.dp.toPx(), cx, cy + 8.dp.toPx())
                        quadraticTo(cx + 5.dp.toPx(), cy + 4.dp.toPx(), cx, cy - 8.dp.toPx())
                        close()
                    }
                    drawPath(flamePath, accent.copy(alpha = 0.35f))
                }
            }
        ),

        // 16. Sweet Dreams
        HomeHeaderTheme(
            id = 16,
            timePeriod = TimePeriod.NIGHT,
            greetingTemplate = "Good Night, {name}",
            subtitle = "Let music end the day softly.",
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFF0F1423), Color(0xFF1E2640))
            ),
            accentColor = Color(0xFF80DEEA),
            textColor = Color(0xFFECEFF1),
            subtitleColor = Color(0xFFB0BEC5),
            layoutType = HeaderLayoutType.LEFT_ALIGN,
            illustration = { modifier, accent ->
                Canvas(modifier = modifier) {
                    val w = size.width
                    val h = size.height
                    val cx = w * 0.8f
                    val cy = h * 0.5f
                    val cw = h * 0.65f
                    val ch = h * 0.42f
                    drawRoundRect(
                        color = accent.copy(alpha = 0.15f),
                        topLeft = Offset(cx - cw / 2, cy - ch / 2),
                        size = Size(cw, ch),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawRoundRect(
                        color = accent.copy(alpha = 0.12f),
                        topLeft = Offset(cx - cw / 4, cy - ch / 5),
                        size = Size(cw / 2, ch * 0.4f),
                        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                    )
                    drawCircle(
                        color = accent.copy(alpha = 0.25f),
                        radius = 4.dp.toPx(),
                        center = Offset(cx - cw / 8, cy)
                    )
                    drawCircle(
                        color = accent.copy(alpha = 0.25f),
                        radius = 4.dp.toPx(),
                        center = Offset(cx + cw / 8, cy)
                    )
                }
            }
        )
    )
}
