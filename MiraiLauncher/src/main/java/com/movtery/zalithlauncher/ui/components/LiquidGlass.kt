/*
 * Aerix Liquid Glass material layer.
 *
 * A lightweight Compose treatment for translucent surfaces: tinted depth, soft
 * spectral reflections, a bright upper rim, and a cool lower edge. It deliberately
 * does not blur the whole screen, so the material remains usable with backgroundBlur=0.
 */
package com.movtery.zalithlauncher.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.movtery.zalithlauncher.ui.theme.AerixMetrics
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface

/**
 * Adds the layered sheen and spectral rim that make a translucent surface read as
 * glass rather than as a flat semi-transparent card.
 *
 * Apply this to a transparent [androidx.compose.material3.Surface], or to content
 * inside a card whose own container is already translucent. The backdrop remains
 * sharp when the user's blur preference is zero.
 */
fun Modifier.liquidGlass(
    shape: Shape,
    tint: Color = AerixSurface.glassTint,
    strength: Float = 1f,
    elevation: Dp = AerixMetrics.glassSurfaceElevation
): Modifier {
    val intensity = strength.coerceIn(0f, 1f)
    return this
        .shadow(
            elevation = elevation * intensity,
            shape = shape,
            clip = false,
            ambientColor = AerixSurface.glassShadow,
            spotColor = AerixSurface.glassShadow
        )
        .clip(shape)
        .drawWithCache {
            val width = size.width.coerceAtLeast(1f)
            val height = size.height.coerceAtLeast(1f)
            val longEdge = maxOf(width, height)
            val prismFill = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.10f * intensity),
                    tint.copy(alpha = 0.11f * intensity),
                    AerixSurface.glassBlue.copy(alpha = 0.085f * intensity),
                    AerixSurface.glassViolet.copy(alpha = 0.065f * intensity),
                    AerixSurface.glassRose.copy(alpha = 0.035f * intensity),
                    Color.Transparent
                ),
                start = Offset.Zero,
                end = Offset(width, height)
            )
            val topReflection = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.27f * intensity),
                    AerixSurface.glassBlue.copy(alpha = 0.10f * intensity),
                    Color.Transparent,
                    AerixSurface.glassRose.copy(alpha = 0.025f * intensity)
                ),
                startY = 0f,
                endY = height
            )
            val upperLens = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.21f * intensity),
                    AerixSurface.glassBlue.copy(alpha = 0.075f * intensity),
                    Color.Transparent
                ),
                center = Offset(width * 0.19f, height * 0.025f),
                radius = longEdge * 0.82f
            )
            val lowerPrism = Brush.radialGradient(
                colors = listOf(
                    AerixSurface.glassViolet.copy(alpha = 0.09f * intensity),
                    AerixSurface.glassBlue.copy(alpha = 0.045f * intensity),
                    Color.Transparent
                ),
                center = Offset(width * 0.92f, height * 0.92f),
                radius = longEdge * 0.72f
            )
            val rim = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.68f * intensity),
                    Color.White.copy(alpha = 0.20f * intensity),
                    AerixSurface.glassBlue.copy(alpha = 0.34f * intensity),
                    AerixSurface.glassViolet.copy(alpha = 0.24f * intensity),
                    AerixSurface.glassRose.copy(alpha = 0.17f * intensity),
                    Color.White.copy(alpha = 0.48f * intensity)
                ),
                start = Offset.Zero,
                end = Offset(width, height)
            )
            val outline = shape.createOutline(size, layoutDirection, this)
            val rimWidth = AerixSpacing.hairline.toPx() * intensity

            onDrawWithContent {
                // The stacked low-alpha washes keep the image behind the pane visible.
                drawRect(brush = prismFill)
                drawRect(brush = upperLens, blendMode = BlendMode.Screen)
                drawRect(brush = topReflection, blendMode = BlendMode.Softlight)
                drawRect(brush = lowerPrism, blendMode = BlendMode.Screen)
                drawContent()

                if (rimWidth > 0f) {
                    drawOutline(
                        outline = outline,
                        brush = rim,
                        style = Stroke(width = rimWidth)
                    )
                    val inset = AerixMetrics.glassRimInset.toPx()
                    drawLine(
                        color = Color.White.copy(alpha = 0.46f * intensity),
                        start = Offset(inset, AerixSpacing.zero.toPx()),
                        end = Offset((width - inset).coerceAtLeast(inset), AerixSpacing.hairline.toPx() * 0.5f),
                        strokeWidth = AerixSpacing.hairline.toPx() * intensity
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.14f * intensity),
                        start = Offset(AerixSpacing.hairline.toPx(), height * 0.22f),
                        end = Offset(AerixSpacing.hairline.toPx(), height * 0.72f),
                        strokeWidth = AerixSpacing.hairline.toPx() * intensity
                    )
                    drawLine(
                        color = AerixSurface.glassBlue.copy(alpha = 0.13f * intensity),
                        start = Offset(inset, height - AerixSpacing.hairline.toPx()),
                        end = Offset((width - inset).coerceAtLeast(inset), height - AerixSpacing.hairline.toPx()),
                        strokeWidth = AerixSpacing.hairline.toPx() * intensity
                    )
                }
            }
        }
}
