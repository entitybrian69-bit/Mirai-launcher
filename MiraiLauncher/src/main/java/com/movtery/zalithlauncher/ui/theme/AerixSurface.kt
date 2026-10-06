/*
 * Aerix Launcher visual tokens.
 *
 * Keep shared surface colors, spacing, and corner radii here so launcher screens
 * use the same obsidian-and-cyan glass language instead of inventing local values.
 */
package com.movtery.zalithlauncher.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AerixSurface {
    val canvas = Color(0xFF07121C)
    // Translucent, blue-shifted panes let the wallpaper remain a visible part of the material.
    val panel = Color(0xA6193042)
    val panelRaised = Color(0xB8234054)
    val panelTrack = Color(0x63395368)
    val panelGlassTint = Color(0x7F24465C)
    val border = Color(0x72E7FBFF)
    val borderSoft = Color(0x45EAFBFF)
    val borderHighlight = Color(0xE1E5FBFF)
    val glassTint = Color(0xFF31546B)
    val glassBlue = Color(0xFF8DEBFF)
    val glassViolet = Color(0xFFB6A2FF)
    val glassRose = Color(0xFFF4A8D8)
    val glassShadow = Color(0x70000612)
    val accent = Color(0xFF93F1E8)
    val accentSecondary = Color(0xFFBDAFFF)
    val accentGlow = Color(0x6693F1E8)
    val accentContainer = Color(0x5536CFC8)
    val onAccent = Color(0xFF071417)
    val onAccentContainer = Color(0xFFBDF7F7)
    val accentLight = Color(0xFF006D73)
    val accentLightContainer = Color(0xFFA8F0F1)
    val onAccentLightContainer = Color(0xFF002021)
    val lightCanvas = Color(0xFFF3F8FA)
    val lightPanel = Color(0xFFFCFEFF)
    val lightPanelRaised = Color(0xFFE5F1F3)
    val lightBorder = Color(0xFFBCCED1)
    val lightTextPrimary = Color(0xFF14212B)
    val lightTextSecondary = Color(0xFF4C626D)
    val errorDarkContainer = Color(0xFF7A2635)
    val errorLight = Color(0xFFB3203D)
    val errorLightContainer = Color(0xFFFFDAD9)
    val dangerContainer = Color(0xFF3B1A1E)
    val warningContainer = Color(0xFF3A2E16)
    val modrinthBrand = Color(0xFF1BD96A)
    val onModrinthBrand = Color(0xFF072314)
    val curseForgeBrand = Color(0xFFF16436)
    val logInfoSurface = Color(0xFF447152)
    val logDebugSurface = Color(0xFF43698D)
    val logWarningSurface = Color(0xFF656E76)
    val syntaxMuted = Color(0xFF6E7C83)
    val syntaxString = Color(0xFF6AAB73)
    val syntaxAccent = Color(0xFFC67CBA)
    val shadowSoft = Color(0x3A000000)
    val shadowSubtle = Color(0x1A000000)
    val scrimSoft = Color(0x33000000)
    val textPrimary = Color(0xFFF1F6FA)
    val textSecondary = Color(0xFFA8B5C2)
    val textMuted = Color(0xFF748392)
    val success = Color(0xFF56D99A)
    val warning = Color(0xFFFFC857)
    val danger = Color(0xFFFF7189)
    val glassShine = Color(0x48FFFFFF)

    /** Tint added above an existing surface when the user's blur preference is zero. */
    const val sharpGlassTintAlpha = 0.12f
}

object AerixSpacing {
    val zero: Dp = 0.dp
    val hairline: Dp = 1.dp
    val xxs: Dp = 2.dp
    val tiny: Dp = 3.dp
    val xs: Dp = 4.dp
    val xsPlus: Dp = 5.dp
    val smCompact: Dp = 6.dp
    val smTight: Dp = 7.dp
    val sm: Dp = 8.dp
    val smPlus: Dp = 10.dp
    val smNarrow: Dp = 9.dp
    val mdTight: Dp = 11.dp
    val md: Dp = 12.dp
    val mdPlus: Dp = 14.dp
    val lg: Dp = 16.dp
    val lgPlus: Dp = 18.dp
    val xl: Dp = 20.dp
    val xlPlus: Dp = 22.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp
    val section: Dp = 40.dp
}

object AerixRadii {
    val square: Dp = 0.dp
    val tiny: Dp = 4.dp
    val micro: Dp = 6.dp
    val compact: Dp = 8.dp
    val controlSmall: Dp = 10.dp
    val control: Dp = 12.dp
    val cardSmall: Dp = 16.dp
    val card: Dp = 18.dp
    val cardLarge: Dp = 20.dp
    val panelSmall: Dp = 22.dp
    val panel: Dp = 24.dp
    val dialog: Dp = 28.dp
    val hero: Dp = 32.dp
    val pill: Dp = 50.dp
}

object AerixMetrics {
    val pillTabHeight: Dp = 38.dp
    val pageHeaderHeight: Dp = 44.dp
    val shellHeaderHeight: Dp = 58.dp
    val shellActionHeight: Dp = 40.dp
    val navigationRailWidth: Dp = 70.dp
    val favoritesEmptyStateTopInset: Dp = 80.dp
    val exportTreeLabelStartInset: Dp = 46.dp
    val expandedKeyboardEndInset: Dp = 58.dp
    val assetSearchBottomInset: Dp = 58.dp
    val glassRimInset: Dp = 16.dp
    val glassSurfaceElevation: Dp = 3.dp
    val glassFloatingElevation: Dp = 8.dp
    val glassRailElevation: Dp = 8.dp
    val glassDialogElevation: Dp = 8.dp
    val glassSelectedElevation: Dp = 1.dp
    val glassSubtleElevation: Dp = 0.dp
}
