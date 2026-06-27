package bharadwaj.juno.music.ambient.ui.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.ui.AmbientSceneColors
import kotlin.math.sin

/**
 * Procedural forest environment with layered pine tree sway, silhouette depth, and haze.
 */
@Composable
fun AmbientForest(
    colors: AmbientSceneColors,
    atmosphere: AmbientAtmosphere,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "forest_motion")

    // Camera drift for horizontal parallax
    val parallaxDrift by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15_000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "parallax",
    )

    // Base wind oscillator cycle
    val windCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * kotlin.math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wind_cycle",
    )

    // Gust calculation
    val windSway = sin(windCycle) * (atmosphere.windSpeedKmh / 15f).coerceIn(0.1f, 2.5f)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val fill = colors.mountainFill

        // Horizon sits at 70% of height
        val horizonY = h * 0.70f

        // Draw ground base matching mountainFill color
        drawRect(
            color = fill,
            topLeft = Offset(0f, horizonY),
            size = Size(w, h - horizonY),
        )

        // ── 1. Far Range Trees (tall, desaturated, high opacity offset) ─────────
        val farTreeColor = lerp(fill, colors.skyBottom, 0.45f)
        val farDriftX = parallaxDrift * 0.25f
        withTransform({
            translate(left = farDriftX)
        }) {
            val farTrees = listOf(
                FarTree(w * 0.08f, h * 0.16f, w * 0.08f),
                FarTree(w * 0.20f, h * 0.18f, w * 0.09f),
                FarTree(w * 0.35f, h * 0.15f, w * 0.07f),
                FarTree(w * 0.52f, h * 0.20f, w * 0.10f),
                FarTree(w * 0.68f, h * 0.17f, w * 0.08f),
                FarTree(w * 0.82f, h * 0.19f, w * 0.09f),
                FarTree(w * 0.94f, h * 0.15f, w * 0.07f),
            )
            farTrees.forEach { tree ->
                val treeBaseY = horizonY + h * 0.05f
                val sway = windSway * tree.height * 0.04f
                drawPineTree(tree.cx, treeBaseY, tree.height, tree.width, farTreeColor, sway)
            }
        }

        // ── 2. Distant Haze Overlay (squeezed between ranges) ──────────────────
        val hazeColor = colors.skyBottom.copy(alpha = 0.25f)
        drawRect(
            color = hazeColor,
            topLeft = Offset(0f, horizonY),
            size = Size(w, h * 0.10f),
        )

        // ── 3. Mid Range Trees (medium size, slightly darker) ─────────────────
        val midTreeColor = lerp(fill, colors.skyBottom, 0.20f)
        val midDriftX = parallaxDrift * 0.60f
        withTransform({
            translate(left = midDriftX)
        }) {
            val midTrees = listOf(
                FarTree(w * 0.14f, h * 0.22f, w * 0.11f),
                FarTree(w * 0.28f, h * 0.25f, w * 0.12f),
                FarTree(w * 0.44f, h * 0.21f, w * 0.10f),
                FarTree(w * 0.60f, h * 0.26f, w * 0.13f),
                FarTree(w * 0.76f, h * 0.23f, w * 0.11f),
                FarTree(w * 0.90f, h * 0.24f, w * 0.12f),
            )
            midTrees.forEach { tree ->
                val treeBaseY = horizonY + h * 0.12f
                val sway = windSway * tree.height * 0.06f
                drawPineTree(tree.cx, treeBaseY, tree.height, tree.width, midTreeColor, sway)
            }
        }

        // ── 4. Near Range Trees (large size, foreground details) ──────────────
        val nearTreeColor = fill
        val nearDriftX = parallaxDrift * 1.0f
        withTransform({
            translate(left = nearDriftX)
        }) {
            val nearTrees = listOf(
                FarTree(w * 0.04f, h * 0.31f, w * 0.15f),
                FarTree(w * 0.22f, h * 0.33f, w * 0.16f),
                FarTree(w * 0.50f, h * 0.29f, w * 0.14f),
                FarTree(w * 0.70f, h * 0.35f, w * 0.18f),
                FarTree(w * 0.88f, h * 0.30f, w * 0.15f),
            )
            nearTrees.forEach { tree ->
                val treeBaseY = horizonY + h * 0.32f
                val sway = windSway * tree.height * 0.08f
                drawPineTree(tree.cx, treeBaseY, tree.height, tree.width, nearTreeColor, sway)
            }
        }
    }
}

private data class FarTree(val cx: Float, val height: Float, val width: Float)

private fun DrawScope.drawPineTree(
    cx: Float,
    cy: Float,
    height: Float,
    width: Float,
    fill: Color,
    swayOffset: Float,
) {
    // 1. Draw Trunk
    val trunkW = width * 0.16f
    val trunkH = height * 0.22f
    drawRect(
        color = fill.copy(alpha = fill.alpha * 0.85f),
        topLeft = Offset(cx - trunkW / 2f, cy - trunkH),
        size = Size(trunkW, trunkH),
    )

    // 2. Draw Stacked Triangles
    val leavesBaseY = cy - trunkH
    val leavesH = height * 0.78f

    // Base triangle
    val baseT = Path().apply {
        moveTo(cx - width / 2f, leavesBaseY)
        quadraticTo(cx + swayOffset * 0.3f, cy - height * 0.4f, cx + swayOffset, cy - height)
        lineTo(cx + width / 2f, leavesBaseY)
        close()
    }
    // Mid triangle
    val midBaseY = leavesBaseY - leavesH * 0.28f
    val midW = width * 0.78f
    val midT = Path().apply {
        moveTo(cx - midW / 2f, midBaseY)
        lineTo(cx + swayOffset, cy - height)
        lineTo(cx + midW / 2f, midBaseY)
        close()
    }
    // Top triangle
    val topBaseY = leavesBaseY - leavesH * 0.58f
    val topW = width * 0.52f
    val topT = Path().apply {
        moveTo(cx - topW / 2f, topBaseY)
        lineTo(cx + swayOffset, cy - height)
        lineTo(cx + topW / 2f, topBaseY)
        close()
    }

    drawPath(baseT, fill)
    drawPath(midT, fill)
    drawPath(topT, fill)
}
