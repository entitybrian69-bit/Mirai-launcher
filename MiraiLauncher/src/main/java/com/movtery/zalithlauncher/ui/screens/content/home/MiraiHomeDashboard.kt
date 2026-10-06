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

package com.movtery.zalithlauncher.ui.screens.content.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.coroutine.TaskSystem
import com.movtery.zalithlauncher.game.optimization.JvmGcAutoTunerDialog
import com.movtery.zalithlauncher.game.optimization.MobileFpsBoosterDialog
import com.movtery.zalithlauncher.game.optimization.SmartCrashDoctorDialog
import com.movtery.zalithlauncher.game.renderer.RendererPicker
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.components.liquidGlass
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage
import com.movtery.zalithlauncher.ui.theme.AerixMetrics
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.ui.theme.MiraiThemeManager

private val ModrinthEmerald = AerixSurface.modrinthBrand

private val DefaultAvailableRenderers = setOf(
    RendererPicker.LTW,
    RendererPicker.LTW_LEGACY,
    RendererPicker.VGPU,
    RendererPicker.VGPU_1368,
    RendererPicker.ZINK,
    RendererPicker.VIRGL,
    RendererPicker.GL4ES
)

fun resolveRendererShortLabel(version: Version?): String {
    val mcVer = version?.getVersionInfo()?.minecraftVersion.orEmpty()
    val manual = version?.getRenderer().orEmpty()
    val choice = RendererPicker.pick(mcVer, manual, DefaultAvailableRenderers)
    return when {
        choice.identifier == RendererPicker.VGPU -> "vgpu"
        choice.identifier == RendererPicker.VGPU_1368 -> "VGPU 1.3.6β"
        choice.identifier == RendererPicker.LTW_LEGACY || choice.identifier.contains("Legacy", ignoreCase = true) -> "LTW Legacy"
        choice.identifier == RendererPicker.LTW || choice.identifier.contains("LTW", ignoreCase = true) -> "LTW"
        choice.identifier == RendererPicker.ZINK || choice.identifier.contains("Zink", ignoreCase = true) -> "Kopper Zink"
        choice.identifier == RendererPicker.VIRGL || choice.identifier.contains("VirGL", ignoreCase = true) -> "VirGL"
        choice.identifier == RendererPicker.GL4ES || choice.identifier.contains("GL4ES", ignoreCase = true) -> "GL4ES"
        else -> "LTW"
    }
}

fun resolveRendererBadgeDetail(version: Version?): String {
    val mcVer = version?.getVersionInfo()?.minecraftVersion.orEmpty()
    val manual = version?.getRenderer().orEmpty()
    val choice = RendererPicker.pick(mcVer, manual, DefaultAvailableRenderers)
    return when {
        choice.identifier == RendererPicker.VGPU -> "vgpu - (up to 1.16.5, fast)"
        choice.identifier == RendererPicker.VGPU_1368 -> "VGPU 1.3.6β"
        choice.identifier == RendererPicker.LTW_LEGACY || choice.identifier.contains("Legacy", ignoreCase = true) -> "LTW Legacy 1.8–1.16.5"
        choice.identifier == RendererPicker.LTW || choice.identifier.contains("LTW", ignoreCase = true) -> "LTW 1.17+"
        choice.identifier == RendererPicker.ZINK || choice.identifier.contains("Zink", ignoreCase = true) -> "Kopper Zink"
        choice.identifier == RendererPicker.VIRGL || choice.identifier.contains("VirGL", ignoreCase = true) -> "VirGL"
        choice.identifier == RendererPicker.GL4ES || choice.identifier.contains("GL4ES", ignoreCase = true) -> "GL4ES"
        else -> "LTW 1.17+"
    }
}

@Composable
fun MiraiHomeDashboard(
    onLaunchVersion: (Version) -> Unit,
    onOpenVersionSettings: (Version) -> Unit,
    onExploreContent: () -> Unit,
    onCreateInstance: () -> Unit,
    onManageVersions: () -> Unit,
    onOpenFileManager: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val versions by VersionsManager.versions.collectAsStateWithLifecycle()
    val currentVersion by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val tasks by TaskSystem.tasksFlow.collectAsStateWithLifecycle()
    val activeAccent = MiraiThemeManager.currentAccent()
    val selectedVersion = currentVersion ?: versions.firstOrNull()
    val recentVersions = remember(versions, selectedVersion) {
        buildList {
            if (selectedVersion != null) add(selectedVersion)
            versions.filterNot { it == selectedVersion }.forEach { add(it) }
        }.take(8)
    }

    var showFpsBooster by remember { mutableStateOf(false) }
    var showJreGcTuner by remember { mutableStateOf(false) }
    var showCrashDoctor by remember { mutableStateOf(false) }

    if (showFpsBooster) {
        MobileFpsBoosterDialog(
            version = selectedVersion,
            onDismiss = { showFpsBooster = false }
        )
    }
    if (showJreGcTuner) {
        JvmGcAutoTunerDialog(
            version = selectedVersion,
            onDismiss = { showJreGcTuner = false }
        )
    }
    if (showCrashDoctor) {
        SmartCrashDoctorDialog(
            version = selectedVersion,
            onDismiss = { showCrashDoctor = false }
        )
    }

    val onPlaySelected: () -> Unit = {
        selectedVersion?.let { version ->
            VersionsManager.saveVersion(version)
            onLaunchVersion(version)
        } ?: onCreateInstance()
    }
    val onOpenTasks: () -> Unit = {
        AllSettings.launcherTaskMenuExpanded.save(!AllSettings.launcherTaskMenuExpanded.state)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = AerixSpacing.mdPlus,
                end = AerixSpacing.smCompact,
                top = AerixSpacing.smPlus,
                bottom = AerixSpacing.sm
            )
    ) {
        val wide = maxWidth >= 760.dp && maxWidth > maxHeight
        if (wide) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.md)
            ) {
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
                ) {
                    LiquidWelcomeHero(
                        selectedVersion = selectedVersion,
                        versionCount = versions.size,
                        accent = activeAccent,
                        onPlay = onPlaySelected,
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().height(76.dp),
                        horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                    ) {
                        DashboardActionCard(
                            title = "New instance",
                            subtitle = "Build a world",
                            iconRes = R.drawable.ic_add,
                            accent = activeAccent,
                            modifier = Modifier.weight(1f),
                            onClick = onCreateInstance
                        )
                        DashboardActionCard(
                            title = "Discover",
                            subtitle = "Mods & packs",
                            iconRes = R.drawable.ic_public,
                            accent = AerixSurface.glassBlue,
                            modifier = Modifier.weight(1f),
                            onClick = onExploreContent
                        )
                        DashboardActionCard(
                            title = "Your library",
                            subtitle = "All instances",
                            iconRes = R.drawable.ic_dashboard_filled,
                            accent = AerixSurface.glassViolet,
                            modifier = Modifier.weight(1f),
                            onClick = onManageVersions
                        )
                    }
                    RecentWorldsSection(
                        versions = recentVersions,
                        selectedVersion = selectedVersion,
                        onSelect = VersionsManager::saveVersion,
                        onPlay = { version ->
                            VersionsManager.saveVersion(version)
                            onLaunchVersion(version)
                        },
                        onOpenSettings = onOpenVersionSettings,
                        onManageVersions = onManageVersions,
                        onCreateInstance = onCreateInstance,
                        onExploreContent = onExploreContent,
                        modifier = Modifier.weight(0.72f).fillMaxWidth()
                    )
                }
                Column(
                    modifier = Modifier.widthIn(min = 270.dp, max = 332.dp).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
                ) {
                    SelectedWorldPanel(
                        version = selectedVersion,
                        accent = activeAccent,
                        onPlay = onPlaySelected,
                        onCreateInstance = onCreateInstance,
                        onOpenSettings = { version ->
                            VersionsManager.saveVersion(version)
                            onOpenVersionSettings(version)
                        },
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    )
                    QuickToolsPanel(
                        accent = activeAccent,
                        activeTaskCount = tasks.size,
                        onBoostFps = { showFpsBooster = true },
                        onTuneGc = { showJreGcTuner = true },
                        onCrashDoctor = { showCrashDoctor = true },
                        onOpenFiles = onOpenFileManager,
                        onOpenTasks = onOpenTasks,
                        modifier = Modifier.height(190.dp).fillMaxWidth()
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = AerixSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.md)
            ) {
                item(key = "welcome") {
                    LiquidWelcomeHero(
                        selectedVersion = selectedVersion,
                        versionCount = versions.size,
                        accent = activeAccent,
                        onPlay = onPlaySelected,
                        modifier = Modifier.fillMaxWidth().height(286.dp)
                    )
                }
                item(key = "actions") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                    ) {
                        DashboardActionCard(
                            title = "New instance",
                            subtitle = "Build a world",
                            iconRes = R.drawable.ic_add,
                            accent = activeAccent,
                            modifier = Modifier.width(190.dp).height(76.dp),
                            onClick = onCreateInstance
                        )
                        DashboardActionCard(
                            title = "Discover",
                            subtitle = "Mods & packs",
                            iconRes = R.drawable.ic_public,
                            accent = AerixSurface.glassBlue,
                            modifier = Modifier.width(190.dp).height(76.dp),
                            onClick = onExploreContent
                        )
                        DashboardActionCard(
                            title = "Your library",
                            subtitle = "All instances",
                            iconRes = R.drawable.ic_dashboard_filled,
                            accent = AerixSurface.glassViolet,
                            modifier = Modifier.width(190.dp).height(76.dp),
                            onClick = onManageVersions
                        )
                        DashboardActionCard(
                            title = "Files",
                            subtitle = "Game data",
                            iconRes = R.drawable.ic_folder_filled,
                            accent = AerixSurface.glassRose,
                            modifier = Modifier.width(190.dp).height(76.dp),
                            onClick = onOpenFileManager
                        )
                    }
                }
                item(key = "quick-tools") {
                    QuickToolsPanel(
                        accent = activeAccent,
                        activeTaskCount = tasks.size,
                        onBoostFps = { showFpsBooster = true },
                        onTuneGc = { showJreGcTuner = true },
                        onCrashDoctor = { showCrashDoctor = true },
                        onOpenFiles = onOpenFileManager,
                        onOpenTasks = onOpenTasks,
                        compact = true,
                        modifier = Modifier.fillMaxWidth().height(98.dp)
                    )
                }
                item(key = "recent-heading") {
                    RecentWorldsHeading(
                        count = recentVersions.size,
                        onManageVersions = onManageVersions
                    )
                }
                if (recentVersions.isEmpty()) {
                    item(key = "empty-worlds") {
                        EmptyRecentWorldsCard(
                            accent = activeAccent,
                            onCreateInstance = onCreateInstance,
                            onExploreContent = onExploreContent
                        )
                    }
                } else {
                    items(
                        items = recentVersions,
                        key = { "recent-${it.getVersionName()}" }
                    ) { version ->
                        RecentWorldRow(
                            version = version,
                            selected = version == selectedVersion,
                            accent = activeAccent,
                            onSelect = { VersionsManager.saveVersion(version) },
                            onPlay = {
                                VersionsManager.saveVersion(version)
                                onLaunchVersion(version)
                            },
                            onSettings = {
                                VersionsManager.saveVersion(version)
                                onOpenVersionSettings(version)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LiquidWelcomeHero(
    selectedVersion: Version?,
    versionCount: Int,
    accent: Color,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AerixRadii.hero)
    BoxWithConstraints(
        modifier = modifier
            .clip(shape)
            .liquidGlass(
                shape = shape,
                tint = accent,
                strength = 1f,
                elevation = AerixMetrics.glassFloatingElevation
            )
            .border(AerixSpacing.hairline, AerixSurface.borderHighlight.copy(alpha = 0.68f), shape)
    ) {
        val compact = maxWidth < 540.dp
        Image(
            painter = painterResource(R.drawable.mirai_hero_bg),
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize().alpha(0.62f)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0f to Color(0xF0071721),
                            0.46f to Color(0xCC0A1A28),
                            0.76f to Color(0x6422394C),
                            1f to Color(0x24384C70)
                        )
                    )
                )
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension * 0.86f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.20f), accent.copy(alpha = 0.09f), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.08f),
                    radius = radius
                ),
                radius = radius,
                center = Offset(size.width * 0.85f, size.height * 0.08f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AerixSurface.glassViolet.copy(alpha = 0.10f), Color.Transparent),
                    center = Offset(size.width * 0.99f, size.height * 0.96f),
                    radius = radius * 0.82f
                ),
                radius = radius * 0.82f,
                center = Offset(size.width * 0.99f, size.height * 0.96f)
            )
            drawLine(
                color = Color.White.copy(alpha = 0.38f),
                start = Offset(28.dp.toPx(), 1.dp.toPx()),
                end = Offset((size.width * 0.60f).coerceAtLeast(28.dp.toPx()), 1.dp.toPx()),
                strokeWidth = 1.dp.toPx()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (compact) AerixSpacing.lg else AerixSpacing.xxl),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(AerixSpacing.hairline, Color.White.copy(alpha = 0.30f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_mirai_mark),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Text(
                        text = "AERIX  /  YOUR WORLD",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp,
                        color = Color.White.copy(alpha = 0.88f)
                    )
                }
                Text(
                    text = if (selectedVersion == null) "Your next world\nstarts here." else "A new world\nawaits you.",
                    style = if (compact) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = if (compact) 34.sp else 46.sp,
                    maxLines = 2
                )
                Text(
                    text = when {
                        selectedVersion != null -> "One calm place to shape, tune and launch your Minecraft worlds."
                        versionCount == 0 -> "Create an instance, make it yours, then step straight into the game."
                        else -> "Your library is ready. Pick a world and make the next session yours."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.78f),
                    maxLines = if (compact) 2 else 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SelectedVersionChip(
                    version = selectedVersion,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = onPlay,
                    shape = RoundedCornerShape(AerixRadii.pill),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent,
                        contentColor = AerixSurface.onAccent
                    ),
                    contentPadding = PaddingValues(horizontal = AerixSpacing.lg, vertical = AerixSpacing.smPlus)
                ) {
                    Icon(
                        painter = painterResource(if (selectedVersion == null) R.drawable.ic_add else R.drawable.ic_play_arrow_filled),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(AerixSpacing.xs))
                    Text(
                        text = if (selectedVersion == null) "Create" else "Play now",
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectedVersionChip(
    version: Version?,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AerixRadii.pill)
    Surface(
        modifier = modifier
            .height(48.dp)
            .liquidGlass(
                shape = shape,
                tint = AerixSurface.glassBlue,
                strength = 0.72f,
                elevation = AerixMetrics.glassSubtleElevation
            ),
        shape = shape,
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(AerixSpacing.hairline, Color.White.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AerixSpacing.smPlus),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (version != null) {
                VersionIconImage(
                    version = version,
                    modifier = Modifier.size(28.dp).clip(RoundedCornerShape(AerixRadii.micro))
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_dashboard_filled),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.88f),
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = version?.getVersionName() ?: "No instance selected",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = version?.getVersionInfo()?.minecraftVersion ?: "Choose a world to begin",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.70f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DashboardActionCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(AerixRadii.cardLarge)
    Surface(
        modifier = modifier
            .clip(shape)
            .liquidGlass(
                shape = shape,
                tint = accent,
                strength = 0.86f,
                elevation = AerixMetrics.glassSelectedElevation
            ),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AerixSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(AerixRadii.control))
                    .background(accent.copy(alpha = 0.15f))
                    .border(AerixSpacing.hairline, accent.copy(alpha = 0.25f), RoundedCornerShape(AerixRadii.control)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(19.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = AerixSurface.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = AerixSurface.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SelectedWorldPanel(
    version: Version?,
    accent: Color,
    onPlay: () -> Unit,
    onCreateInstance: () -> Unit,
    onOpenSettings: (Version) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AerixRadii.panel)
    Surface(
        modifier = modifier
            .clip(shape)
            .liquidGlass(
                shape = shape,
                tint = accent,
                strength = 0.94f,
                elevation = AerixMetrics.glassFloatingElevation
            ),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.border),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(AerixSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AerixSpacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)) {
                    Text(
                        text = "ACTIVE WORLD",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.3.sp,
                        color = accent
                    )
                    Text(
                        text = if (version == null) "Ready to create" else "Your selected instance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AerixSurface.textPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_dashboard_filled),
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (version == null) {
                EmptyWorldBody(
                    modifier = Modifier.weight(1f),
                    accent = accent,
                    onCreateInstance = onCreateInstance
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VersionIconImage(
                        version = version,
                        modifier = Modifier
                            .size(58.dp)
                            .clip(RoundedCornerShape(AerixRadii.cardSmall))
                            .border(AerixSpacing.hairline, AerixSurface.borderSoft, RoundedCornerShape(AerixRadii.cardSmall))
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)
                    ) {
                        Text(
                            text = version.getVersionName(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = AerixSurface.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val info = version.getVersionInfo()
                        Text(
                            text = "${info?.loaderInfo?.loader?.displayName ?: "Vanilla"}  /  ${info?.minecraftVersion ?: "Unknown"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AerixSurface.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                GlassDivider()
                WorldMetricRow(
                    label = "RENDERER",
                    value = resolveRendererShortLabel(version),
                    accent = accent
                )
                WorldMetricRow(
                    label = "MINECRAFT",
                    value = version.getVersionInfo()?.minecraftVersion ?: "Unknown",
                    accent = accent
                )
                WorldMetricRow(
                    label = "PROFILE",
                    value = version.getVersionName().take(18),
                    accent = accent
                )
                Spacer(Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)) {
                    Button(
                        onClick = onPlay,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(AerixRadii.pill),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accent,
                            contentColor = AerixSurface.onAccent
                        ),
                        contentPadding = PaddingValues(horizontal = AerixSpacing.sm, vertical = AerixSpacing.smPlus)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_play_arrow_filled),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(AerixSpacing.xs))
                        Text("Launch", fontWeight = FontWeight.ExtraBold)
                    }
                    Button(
                        onClick = { onOpenSettings(version) },
                        shape = RoundedCornerShape(AerixRadii.pill),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AerixSurface.panelRaised.copy(alpha = 0.54f),
                            contentColor = AerixSurface.textPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = AerixSpacing.md, vertical = AerixSpacing.smPlus)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings_filled),
                            contentDescription = "Instance settings",
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyWorldBody(
    modifier: Modifier = Modifier,
    accent: Color,
    onCreateInstance: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AerixSpacing.md),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Your library is an open canvas. Add a version or curated pack to get started.",
            style = MaterialTheme.typography.bodyMedium,
            color = AerixSurface.textSecondary
        )
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onCreateInstance,
            shape = RoundedCornerShape(AerixRadii.pill),
            colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = AerixSurface.onAccent)
        ) {
            Icon(painterResource(R.drawable.ic_add), contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(AerixSpacing.xs))
            Text("Create an instance", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun WorldMetricRow(
    label: String,
    value: String,
    accent: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp,
            color = AerixSurface.textMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = AerixSurface.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun QuickToolsPanel(
    accent: Color,
    activeTaskCount: Int,
    onBoostFps: () -> Unit,
    onTuneGc: () -> Unit,
    onCrashDoctor: () -> Unit,
    onOpenFiles: () -> Unit,
    onOpenTasks: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val shape = RoundedCornerShape(AerixRadii.panelSmall)
    Surface(
        modifier = modifier
            .clip(shape)
            .liquidGlass(
                shape = shape,
                tint = accent,
                strength = 0.78f,
                elevation = AerixMetrics.glassSubtleElevation
            ),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(AerixSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)) {
                    Text(
                        text = "TOOLS & STATUS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp,
                        color = accent
                    )
                    if (!compact) {
                        Text(
                            text = "Small refinements, one tap away",
                            style = MaterialTheme.typography.labelSmall,
                            color = AerixSurface.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (activeTaskCount > 0) {
                    Text(
                        text = "$activeTaskCount active",
                        style = MaterialTheme.typography.labelSmall,
                        color = AerixSurface.success,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (compact) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                ) {
                    ToolPill("FPS", R.drawable.ic_rocket_launch_filled, accent, onBoostFps)
                    ToolPill("JRE / GC", R.drawable.ic_settings_filled, AerixSurface.glassBlue, onTuneGc)
                    ToolPill("Crash Doctor", R.drawable.ic_warning_filled, AerixSurface.warning, onCrashDoctor)
                    ToolPill("Files", R.drawable.ic_folder_outlined, AerixSurface.glassViolet, onOpenFiles)
                    ToolPill(
                        if (activeTaskCount == 1) "1 task" else "$activeTaskCount tasks",
                        R.drawable.ic_download_2_filled,
                        AerixSurface.glassRose,
                        onOpenTasks
                    )
                }
            } else {
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                ) {
                    ToolTile("FPS", "Performance", R.drawable.ic_rocket_launch_filled, accent, Modifier.weight(1f), onBoostFps)
                    ToolTile("JRE / GC", "Memory tuning", R.drawable.ic_settings_filled, AerixSurface.glassBlue, Modifier.weight(1f), onTuneGc)
                }
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                ) {
                    ToolTile("Crash Doctor", "Find a fix", R.drawable.ic_warning_filled, AerixSurface.warning, Modifier.weight(1f), onCrashDoctor)
                    ToolTile(
                        if (activeTaskCount == 1) "1 active task" else "$activeTaskCount tasks",
                        "Downloads & files",
                        R.drawable.ic_download_2_filled,
                        AerixSurface.glassRose,
                        Modifier.weight(1f),
                        onOpenTasks
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolTile(
    title: String,
    subtitle: String,
    iconRes: Int,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(AerixRadii.cardSmall)
    Surface(
        modifier = modifier
            .clip(shape)
            .liquidGlass(shape = shape, tint = accent, strength = 0.62f, elevation = AerixMetrics.glassSubtleElevation),
        shape = shape,
        color = Color.White.copy(alpha = 0.035f),
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = AerixSpacing.smPlus),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(iconRes), contentDescription = null, tint = accent, modifier = Modifier.size(19.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)) {
                Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = AerixSurface.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = AerixSurface.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun ToolPill(
    label: String,
    iconRes: Int,
    accent: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(AerixRadii.pill)
    Surface(
        modifier = Modifier
            .height(40.dp)
            .liquidGlass(shape, tint = accent, strength = 0.68f, elevation = AerixMetrics.glassSubtleElevation),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AerixSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(iconRes), contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = AerixSurface.textPrimary, maxLines = 1)
        }
    }
}

@Composable
private fun RecentWorldsSection(
    versions: List<Version>,
    selectedVersion: Version?,
    onSelect: (Version) -> Unit,
    onPlay: (Version) -> Unit,
    onOpenSettings: (Version) -> Unit,
    onManageVersions: () -> Unit,
    onCreateInstance: () -> Unit,
    onExploreContent: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)) {
        RecentWorldsHeading(count = versions.size, onManageVersions = onManageVersions)
        if (versions.isEmpty()) {
            EmptyRecentWorldsCard(
                accent = MiraiThemeManager.currentAccent(),
                onCreateInstance = onCreateInstance,
                onExploreContent = onExploreContent
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
            ) {
                versions.forEach { version ->
                    RecentWorldTile(
                        version = version,
                        selected = version == selectedVersion,
                        onSelect = { onSelect(version) },
                        onPlay = { onPlay(version) },
                        onSettings = { onOpenSettings(version) },
                        modifier = Modifier.width(224.dp).fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentWorldsHeading(
    count: Int,
    onManageVersions: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)) {
            Text(
                text = "Recently played",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = AerixSurface.textPrimary
            )
            Text(
                text = if (count == 0) "Your worlds will appear here" else "$count saved ${if (count == 1) "world" else "worlds"}",
                style = MaterialTheme.typography.labelSmall,
                color = AerixSurface.textSecondary
            )
        }
        TextButton(onClick = onManageVersions) {
            Text("Open library", color = MiraiThemeManager.currentAccent(), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RecentWorldTile(
    version: Version,
    selected: Boolean,
    onSelect: () -> Unit,
    onPlay: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = MiraiThemeManager.currentAccent()
    val shape = RoundedCornerShape(AerixRadii.cardLarge)
    Surface(
        modifier = modifier
            .clip(shape)
            .liquidGlass(shape, tint = if (selected) accent else AerixSurface.glassBlue, strength = if (selected) 0.88f else 0.56f, elevation = AerixMetrics.glassSubtleElevation)
            .clickable(onClick = onSelect),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(
            if (selected) 1.5.dp else AerixSpacing.hairline,
            if (selected) accent.copy(alpha = 0.76f) else AerixSurface.borderSoft
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(AerixSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus),
            verticalAlignment = Alignment.CenterVertically
        ) {
            VersionIconImage(
                version = version,
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(AerixRadii.controlSmall))
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)) {
                Text(version.getVersionName(), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = AerixSurface.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    text = version.getVersionInfo()?.minecraftVersion ?: "Minecraft",
                    style = MaterialTheme.typography.labelSmall,
                    color = AerixSurface.textSecondary,
                    maxLines = 1
                )
                Text(
                    text = version.getVersionInfo()?.loaderInfo?.loader?.displayName ?: "Vanilla",
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                    maxLines = 1
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onPlay, modifier = Modifier.size(34.dp)) {
                    Icon(painterResource(R.drawable.ic_play_arrow_filled), contentDescription = stringResource(R.string.main_launch_game), tint = accent, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onSettings, modifier = Modifier.size(30.dp)) {
                    Icon(painterResource(R.drawable.ic_settings_filled), contentDescription = "Instance settings", tint = AerixSurface.textSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun RecentWorldRow(
    version: Version,
    selected: Boolean,
    accent: Color,
    onSelect: () -> Unit,
    onPlay: () -> Unit,
    onSettings: () -> Unit
) {
    val shape = RoundedCornerShape(AerixRadii.cardLarge)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .liquidGlass(shape, tint = if (selected) accent else AerixSurface.glassBlue, strength = if (selected) 0.85f else 0.50f, elevation = AerixMetrics.glassSubtleElevation)
            .clickable(onClick = onSelect),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(
            if (selected) 1.5.dp else AerixSpacing.hairline,
            if (selected) accent.copy(alpha = 0.75f) else AerixSurface.borderSoft
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = AerixSpacing.md, vertical = AerixSpacing.smPlus),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            VersionIconImage(version, modifier = Modifier.size(42.dp).clip(RoundedCornerShape(AerixRadii.controlSmall)))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)) {
                Text(version.getVersionName(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AerixSurface.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    text = "${version.getVersionInfo()?.loaderInfo?.loader?.displayName ?: "Vanilla"}  ·  ${version.getVersionInfo()?.minecraftVersion ?: "Unknown"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AerixSurface.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onPlay) {
                Icon(painterResource(R.drawable.ic_play_arrow_filled), contentDescription = stringResource(R.string.main_launch_game), tint = accent)
            }
            IconButton(onClick = onSettings) {
                Icon(painterResource(R.drawable.ic_settings_filled), contentDescription = "Instance settings", tint = AerixSurface.textSecondary)
            }
        }
    }
}

@Composable
private fun EmptyRecentWorldsCard(
    accent: Color,
    onCreateInstance: () -> Unit,
    onExploreContent: () -> Unit
) {
    val shape = RoundedCornerShape(AerixRadii.cardLarge)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(shape, tint = accent, strength = 0.7f, elevation = AerixMetrics.glassSubtleElevation),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(AerixSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)) {
                Text("Your library is waiting", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AerixSurface.textPrimary)
                Text("Create a fresh instance or explore a curated pack.", style = MaterialTheme.typography.bodySmall, color = AerixSurface.textSecondary)
            }
            Button(
                onClick = onCreateInstance,
                shape = RoundedCornerShape(AerixRadii.pill),
                colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = AerixSurface.onAccent)
            ) { Text("Create", fontWeight = FontWeight.Bold) }
            TextButton(onClick = onExploreContent) { Text("Explore", color = accent) }
        }
    }
}

@Composable
private fun GlassDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(AerixSpacing.hairline)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, AerixSurface.border.copy(alpha = 0.74f), Color.Transparent)
                )
            )
    )
}

@Composable
fun ModrinthMetaPill(
    text: String,
    highlighted: Boolean = false,
    backgroundColor: Color = if (highlighted) AerixSurface.accentContainer else AerixSurface.panelRaised,
    textColor: Color = if (highlighted) ModrinthEmerald else AerixSurface.textSecondary,
    borderColor: Color? = if (highlighted) ModrinthEmerald.copy(alpha = 0.45f) else null
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AerixRadii.compact))
            .background(backgroundColor)
            .then(
                if (borderColor != null) {
                    Modifier.border(AerixSpacing.hairline, borderColor, RoundedCornerShape(AerixRadii.compact))
                } else Modifier
            )
            .padding(horizontal = AerixSpacing.smTight, vertical = AerixSpacing.xxs),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            maxLines = 1
        )
    }
}

@Composable
fun ModrinthCompactSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AerixRadii.pill)
    Row(
        modifier = modifier
            .height(40.dp)
            .clip(shape)
            .liquidGlass(shape, tint = AerixSurface.glassBlue, strength = 0.62f, elevation = AerixMetrics.glassSubtleElevation)
            .background(AerixSurface.panelRaised.copy(alpha = 0.42f))
            .border(AerixSpacing.hairline, AerixSurface.borderSoft, shape)
            .padding(horizontal = AerixSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = null,
            tint = AerixSurface.textSecondary,
            modifier = Modifier.size(16.dp)
        )
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodySmall,
                    color = AerixSurface.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium),
                cursorBrush = SolidColor(ModrinthEmerald),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (value.isNotEmpty()) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.generic_clear),
                tint = AerixSurface.textSecondary,
                modifier = Modifier.size(15.dp).clickable { onValueChange("") }
            )
        }
    }
}
