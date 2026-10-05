/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 * Copyright (C) 2026 Aerix Launcher contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.ui.components.influencedByBackgroundColor

/**
 * Shared surface language for every Aerix screen, tab and page.
 *
 * Before this file the look was re-typed by hand in each screen: the same three greys were
 * written down as `Color(0xFF22252C)`, `Color(0xFF21242B)` and `Color(0xFF1E222B)` in different
 * files, borders were sometimes drawn and sometimes not, and radii drifted between 12dp and
 * 28dp. Nothing was wrong in isolation, but no two screens matched exactly.
 *
 * Now the vocabulary lives here once. A screen that needs a card, a pill tab, a section title or
 * a press animation asks for it by name and gets the same result as every other screen — which
 * is what makes the whole launcher feel like one product instead of a collection of pages.
 */
object AerixPalette {
    /** Deepest layer, behind everything. */
    val Obsidian = Color(0xFF0B0D12)
    val ObsidianDeep = Color(0xFF080A0E)
    val ObsidianRaised = Color(0xFF12151C)

    /** Translucent card fills. Keep them under 1f so the wallpaper shows through. */
    val Glass = Color(0xFF151A23)
    val GlassRaised = Color(0xFF1C222C)
    val GlassHigh = Color(0xFF232A35)

    /** Hairlines. White at low alpha reads as a lit glass edge on dark surfaces. */
    val Hairline = Color(0x1AFFFFFF)
    val HairlineStrong = Color(0x33FFFFFF)

    val TextPrimary = Color(0xFFF2F5F9)
    val TextSecondary = Color(0xFF9BA3B0)
    val TextTertiary = Color(0xFF6B7280)

    /** Chrome accent gradient — rail highlights, section titles, selected tabs. */
    val Cyan = Color(0xFF22D3EE)
    val Violet = Color(0xFFA855F7)
    val Magenta = Color(0xFFE879F9)

    /** Action accent — the PLAY button and other "this does the thing" surfaces. */
    val Green = Color(0xFF1BD96A)
    val GreenDeep = Color(0xFF06210F)

    val Amber = Color(0xFFFBBF24)
    val Red = Color(0xFFEF4444)

    val ChromeStops = listOf(Cyan, Violet, Magenta)

    /** The gradient used on rails, section titles and selected tabs. */
    fun chromeBrush(): Brush = Brush.linearGradient(ChromeStops)

    fun chromeBrushHorizontal(): Brush = Brush.horizontalGradient(ChromeStops)
}

object AerixRadius {
    val Chip = 14.dp
    val Rail = 14.dp
    val Card = 20.dp
    val Sheet = 28.dp
    val Pill = 100.dp
}

object AerixMotion {
    /** Colour swaps and small state changes. */
    const val Fast = 150
    /** Default for content transitions. */
    const val Base = 240
    /** Page-level transitions. */
    const val Slow = 380

    /**
     * Press feedback: slightly under-damped so a tap feels like it has weight, but still
     * settles fast enough that rapid tapping never looks laggy.
     */
    fun <T> press(damping: Float = 0.72f) = spring<T>(
        dampingRatio = damping,
        stiffness = Spring.StiffnessMediumLow
    )
}

/** The accent colour currently selected by the user, or the Aerix chrome accent. */
@Composable
fun aerixAccent(): Color = MiraiThemeManager.currentAccent()

/** A lit glass edge that works on both the dark and the light palette. */
@Composable
fun aerixHairline(alpha: Float = 0.10f): Color =
    MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)

/**
 * The fill of a glass surface.
 *
 * [influenced] keeps the existing behaviour: when a wallpaper is set, the fill drops in opacity
 * so the frosted background shows through instead of being covered up.
 */
@Composable
fun aerixGlassFill(
    elevated: Boolean = false,
    influenced: Boolean = true
): Color {
    val base = if (elevated) {
        MaterialTheme.colorScheme.surfaceContainerHighest
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    return influencedByBackgroundColor(color = base, enabled = influenced)
}

/** The fill for the chrome layer that hosts content: screen background, rails, top bars. */
@Composable
fun aerixChromeFill(alpha: Float = 0.86f): Color =
    MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = alpha)

/**
 * Draws the glass treatment on top of whatever fill the element already has:
 * a soft top sheen (light falling on the upper edge), an optional accent glow, and a hairline
 * border. One clipped, allocation-free draw pass — no per-frame blur, no layout tracking, so it
 * costs nothing while scrolling.
 *
 * Call it *after* `background(...)`/`clip(...)` so the border follows the element's own corners.
 */
@Composable
fun Modifier.aerixGlassSurface(
    cornerRadius: Dp = AerixRadius.Card,
    borderAlpha: Float = 0.10f,
    sheenAlpha: Float = 0.045f,
    glow: Color? = null,
    enabled: Boolean = true
): Modifier {
    val borderColor = aerixHairline(borderAlpha)
    return this.drawWithCache {
        val corner = CornerRadius(cornerRadius.toPx())
        val hairline = Stroke(width = 1.dp.toPx())
        val glowStroke = Stroke(width = 2.dp.toPx())
        onDrawBehind {
            if (!enabled) return@onDrawBehind

            // Light falling on the top edge. Reads as the curved face of a glass panel.
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = sheenAlpha), Color.Transparent),
                    startY = 0f,
                    endY = size.height
                ),
                cornerRadius = corner
            )

            glow?.let { accent ->
                drawRoundRect(
                    color = accent.copy(alpha = 0.22f),
                    cornerRadius = corner,
                    style = glowStroke
                )
            }

            drawRoundRect(
                color = borderColor,
                cornerRadius = corner,
                style = hairline
            )
        }
    }
}

/**
 * Smooth press feedback for anything tappable. Applied to cards, rail items and buttons so a tap
 * behaves identically everywhere.
 */
@Composable
fun Modifier.aerixPressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.965f
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = AerixMotion.press(),
        label = "aerixPressScale"
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * A glass panel. Use this instead of a bare [Surface] whenever the element should read as part of
 * the launcher chrome.
 */
@Composable
fun AerixGlassBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = AerixRadius.Card,
    elevated: Boolean = false,
    glow: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() }
    var modifier2 = modifier
        .clip(shape)
        .background(aerixGlassFill(elevated = elevated))
        .aerixGlassSurface(cornerRadius = cornerRadius, glow = glow)

    if (onClick != null) {
        modifier2 = modifier2
            .aerixPressScale(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
    }

    Box(modifier = modifier2, content = content)
}

/** One entry in an [AerixPillTabRow]. */
data class AerixTab(
    val label: String,
    val iconRes: Int? = null
)

/**
 * The pill tab strip used across the launcher: instance sub-tabs, Discover categories, settings
 * sections and renderer options all render through this one composable, so they animate and
 * highlight identically.
 *
 * The selected pill gets the accent fill, the accent border and a scale-up; the rest stay glass.
 */
@Composable
fun AerixPillTabRow(
    tabs: List<AerixTab>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = aerixAccent(),
    scrollable: Boolean = true
) {
    val scrollState = rememberScrollState()
    val rowModifier = if (scrollable) {
        modifier.fillMaxWidth().horizontalScroll(scrollState)
    } else {
        modifier.fillMaxWidth()
    }

    Row(
        modifier = rowModifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = index == selectedIndex
            val container by animateColorAsState(
                targetValue = if (selected) accent.copy(alpha = 0.16f) else aerixGlassFill(),
                animationSpec = tween(AerixMotion.Fast),
                label = "aerixTabContainer"
            )
            val content by animateColorAsState(
                targetValue = if (selected) accent else AerixPalette.TextSecondary,
                animationSpec = tween(AerixMotion.Fast),
                label = "aerixTabContent"
            )
            val border by animateColorAsState(
                targetValue = if (selected) accent.copy(alpha = 0.80f) else aerixHairline(),
                animationSpec = tween(AerixMotion.Fast),
                label = "aerixTabBorder"
            )

            Surface(
                shape = RoundedCornerShape(AerixRadius.Pill),
                color = container,
                contentColor = content,
                border = BorderStroke(1.dp, border),
                onClick = { onSelect(index) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tab.iconRes?.let { icon ->
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = null,
                            tint = content,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                        color = content
                    )
                }
            }
        }
    }
}

/**
 * Section heading with the accent bar. Used above every content group so headings line up the
 * same way on every page.
 */
@Composable
fun AerixSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    accent: Color = aerixAccent()
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 3.dp, height = 16.dp)
                    .clip(RoundedCornerShape(AerixRadius.Pill))
                    .background(Brush.verticalGradient(listOf(accent, accent.copy(alpha = 0.35f))))
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        subtitle?.let { text ->
            Text(
                text = text,
                modifier = Modifier.padding(start = 11.dp),
                style = MaterialTheme.typography.labelSmall,
                color = AerixPalette.TextSecondary
            )
        }
    }
}

/** Small status pill, e.g. "ACTIVE", "FABRIC", version chips. */
@Composable
fun AerixStatusPill(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = aerixAccent(),
    filled: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AerixRadius.Pill),
        color = if (filled) color else color.copy(alpha = 0.14f),
        contentColor = if (filled) AerixPalette.GreenDeep else color,
        border = BorderStroke(1.dp, color.copy(alpha = if (filled) 0f else 0.45f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Standard vertical rhythm between stacked groups on a page. */
val AerixSectionGap: Dp = 14.dp

/** Standard horizontal page padding. */
val AerixPagePadding: Dp = 14.dp

/** A spacer that keeps the vertical rhythm consistent. */
@Composable
fun AerixSectionSpacer() {
    Spacer(modifier = Modifier.height(AerixSectionGap))
}
