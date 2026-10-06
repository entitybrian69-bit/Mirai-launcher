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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.content.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.coroutine.TaskSystem
import com.movtery.zalithlauncher.game.optimization.JvmGcAutoTunerDialog
import com.movtery.zalithlauncher.game.optimization.MobileFpsBoosterDialog
import com.movtery.zalithlauncher.game.optimization.SmartCrashDoctorDialog
import com.movtery.zalithlauncher.game.renderer.RendererPicker
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage
import com.movtery.zalithlauncher.ui.components.AerixSectionHeader
import com.movtery.zalithlauncher.ui.androidText
import com.movtery.zalithlauncher.ui.theme.MiraiThemeManager

private val ModrinthCardColor = AerixSurface.panel
private val ModrinthCardBorder = AerixSurface.border
private val ModrinthEmerald = AerixSurface.modrinthBrand
private val ModrinthOnEmerald = AerixSurface.onModrinthBrand
private val ModrinthAmberBg = AerixSurface.warningContainer
private val ModrinthAmberFg = AerixSurface.textPrimary

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

    var showFpsBooster by remember { mutableStateOf(false) }
    var showJreGcTuner by remember { mutableStateOf(false) }
    var showCrashDoctor by remember { mutableStateOf(false) }

    if (showFpsBooster) {
        MobileFpsBoosterDialog(
            version = currentVersion ?: versions.firstOrNull(),
            onDismiss = { showFpsBooster = false }
        )
    }

    if (showJreGcTuner) {
        JvmGcAutoTunerDialog(
            version = currentVersion ?: versions.firstOrNull(),
            onDismiss = { showJreGcTuner = false }
        )
    }

    if (showCrashDoctor) {
        SmartCrashDoctorDialog(
            version = currentVersion ?: versions.firstOrNull(),
            onDismiss = { showCrashDoctor = false }
        )
    }

    val jumpBackInVersions = remember(versions, currentVersion) {
        buildList {
            currentVersion?.let { add(it) }
            versions.filter { it != currentVersion }.take(3).forEach { add(it) }
        }.take(2)
    }

    val recentListVersions = remember(versions, jumpBackInVersions) {
        val rest = versions.filter { it !in jumpBackInVersions }
        rest.ifEmpty { versions }.take(8)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(start = AerixSpacing.mdPlus, end = AerixSpacing.smCompact, top = AerixSpacing.smPlus)
    ) {
        val useLandscapeDashboard = maxWidth >= 720.dp && maxWidth > maxHeight
        if (useLandscapeDashboard) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
            ) {
                DashboardHeader(
                    activeAccent = activeAccent,
                    activeTaskCount = tasks.size,
                    onBoostFps = { showFpsBooster = true },
                    onTuneGc = { showJreGcTuner = true },
                    onCrashDoctor = { showCrashDoctor = true },
                    onOpenFiles = onOpenFileManager,
                    onOpenTasks = {
                        AllSettings.launcherTaskMenuExpanded.save(
                            !AllSettings.launcherTaskMenuExpanded.state
                        )
                    }
                )

                if (versions.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(AerixRadii.card),
                        color = ModrinthCardColor,
                        border = BorderStroke(AerixSpacing.hairline, ModrinthCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(AerixSpacing.xl),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AerixSectionHeader(title = stringResource(R.string.versions_manage_no_versions))
                            Row(horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)) {
                                Button(
                                    onClick = onCreateInstance,
                                    shape = RoundedCornerShape(AerixRadii.control),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = activeAccent,
                                        contentColor = ModrinthOnEmerald
                                    )
                                ) {
                                    Text(text = "+ New Instance", fontWeight = FontWeight.ExtraBold)
                                }
                                Button(
                                    onClick = onExploreContent,
                                    shape = RoundedCornerShape(AerixRadii.control),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AerixSurface.panelRaised,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(text = "Browse Modpacks", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(AerixSpacing.md)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(0.48f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                        ) {
                            AerixSectionHeader(title = "Jump Back In")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
                            ) {
                                jumpBackInVersions.forEach { version ->
                                    JumpBackInMobileCard(
                                        version = version,
                                        isSelected = version == currentVersion,
                                        onSelect = { VersionsManager.saveVersion(version) },
                                        onPlay = {
                                            VersionsManager.saveVersion(version)
                                            onLaunchVersion(version)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (jumpBackInVersions.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(0.52f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AerixSectionHeader(
                                    title = "Recent Instances",
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "View All",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = activeAccent,
                                    modifier = Modifier.clickable(onClick = onManageVersions)
                                )
                            }
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                verticalArrangement = Arrangement.spacedBy(AerixSpacing.smCompact),
                                contentPadding = PaddingValues(bottom = AerixSpacing.md)
                            ) {
                                items(
                                    items = recentListVersions,
                                    key = { it.getVersionName() }
                                ) { version ->
                                    RecentInstanceMobileRow(
                                        version = version,
                                        isSelected = version == currentVersion,
                                        onSelect = { VersionsManager.saveVersion(version) },
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
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = AerixSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.md)
            ) {
                item {
                    DashboardHeader(
                        activeAccent = activeAccent,
                        activeTaskCount = tasks.size,
                        onBoostFps = { showFpsBooster = true },
                        onTuneGc = { showJreGcTuner = true },
                        onCrashDoctor = { showCrashDoctor = true },
                        onOpenFiles = onOpenFileManager,
                        onOpenTasks = {
                            AllSettings.launcherTaskMenuExpanded.save(
                                !AllSettings.launcherTaskMenuExpanded.state
                            )
                        }
                    )
                }

                if (versions.isEmpty()) {
                    // Empty state when no instances are installed yet
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(AerixRadii.card),
                            color = ModrinthCardColor,
                            border = BorderStroke(AerixSpacing.hairline, ModrinthCardBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(AerixSpacing.xl),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
                            ) {
                                Text(
                                    text = stringResource(R.string.versions_manage_no_versions),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)) {
                                    Button(
                                        onClick = onCreateInstance,
                                        shape = RoundedCornerShape(AerixRadii.control),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = activeAccent,
                                            contentColor = ModrinthOnEmerald
                                        )
                                    ) {
                                        Text(
                                            text = "+ New Instance",
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                    Button(
                                        onClick = onExploreContent,
                                        shape = RoundedCornerShape(AerixRadii.control),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = AerixSurface.panelRaised,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text(
                                            text = "Browse Modpacks",
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // 2. 'Jump Back In' — 2 Wide Cards Side-by-Side (Mockup #1)
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)) {
                            AerixSectionHeader(title = "Jump Back In")

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
                            ) {
                                jumpBackInVersions.forEach { version ->
                                    JumpBackInMobileCard(
                                        version = version,
                                        isSelected = version == currentVersion,
                                        onSelect = { VersionsManager.saveVersion(version) },
                                        onPlay = {
                                            VersionsManager.saveVersion(version)
                                            onLaunchVersion(version)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (jumpBackInVersions.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    // 3. 'Recent Instances' — Clean Horizontal Rows (Mockup #1)
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = AerixSpacing.xxs),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AerixSectionHeader(
                                title = "Recent Instances",
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "View All",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = activeAccent,
                                modifier = Modifier.clickable(onClick = onManageVersions)
                            )
                        }
                    }

                    items(
                        items = recentListVersions,
                        key = { it.getVersionName() }
                    ) { version ->
                        RecentInstanceMobileRow(
                            version = version,
                            isSelected = version == currentVersion,
                            onSelect = { VersionsManager.saveVersion(version) },
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
private fun DashboardHeader(
    activeAccent: Color,
    activeTaskCount: Int,
    onBoostFps: () -> Unit,
    onTuneGc: () -> Unit,
    onCrashDoctor: () -> Unit,
    onOpenFiles: () -> Unit,
    onOpenTasks: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AerixRadii.card),
        color = AerixSurface.canvas.copy(alpha = 0.94f),
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AerixSpacing.md, vertical = AerixSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smCompact),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DashboardActionPill(
                    label = "Boost FPS",
                    iconRes = R.drawable.ic_rocket_launch_filled,
                    activeAccent = activeAccent,
                    selected = true,
                    onClick = onBoostFps
                )
                DashboardActionPill(
                    label = "☕ JRE & GC",
                    onClick = onTuneGc
                )
                DashboardActionPill(
                    label = "🩺 Crash Doctor",
                    onClick = onCrashDoctor
                )
                DashboardActionPill(
                    label = "Files",
                    iconRes = R.drawable.ic_folder_outlined,
                    onClick = onOpenFiles
                )
                DashboardActionPill(
                    label = if (activeTaskCount == 1) "1 Task" else "$activeTaskCount Tasks",
                    iconRes = R.drawable.ic_download_2_filled,
                    activeAccent = activeAccent,
                    selected = activeTaskCount > 0,
                    onClick = onOpenTasks
                )
            }

            AerixSectionHeader(
                title = androidText("Aerix Launcher"),
                subtitle = "Your Minecraft library",
                titleStyle = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
private fun DashboardActionPill(
    label: String,
    iconRes: Int? = null,
    activeAccent: Color = MiraiThemeManager.currentAccent(),
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(AerixRadii.cardLarge))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(AerixRadii.cardLarge),
        color = if (selected) activeAccent.copy(alpha = 0.16f) else ModrinthCardColor,
        border = BorderStroke(
            AerixSpacing.hairline,
            if (selected) activeAccent.copy(alpha = 0.72f) else ModrinthCardBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AerixSpacing.smPlus, vertical = AerixSpacing.smCompact),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
        ) {
            iconRes?.let { resource ->
                Icon(
                    painter = painterResource(resource),
                    contentDescription = null,
                    tint = if (selected) activeAccent else AerixSurface.textSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = if (selected) activeAccent else AerixSurface.textPrimary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun JumpBackInMobileCard(
    version: Version,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeAccent = MiraiThemeManager.currentAccent()
    val info = version.getVersionInfo()
    val mcVer = info?.minecraftVersion ?: "Unknown"
    val loaderName = info?.loaderInfo?.loader?.displayName ?: "Vanilla"

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(AerixRadii.card))
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(AerixRadii.card),
        color = ModrinthCardColor,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else AerixSpacing.hairline,
            color = if (isSelected) activeAccent.copy(alpha = 0.7f) else ModrinthCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AerixSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(AerixRadii.controlSmall))
                    .background(AerixSurface.panelRaised)
                    .padding(AerixSpacing.xs),
                contentAlignment = Alignment.Center
            ) {
                VersionIconImage(
                    version = version,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
            ) {
                Text(
                    text = version.getVersionName(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "$loaderName $mcVer",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = AerixSurface.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Circular Dynamic Accent Play Button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(activeAccent)
                    .clickable(onClick = onPlay),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_play_arrow_filled),
                    contentDescription = stringResource(R.string.main_launch_game),
                    tint = ModrinthOnEmerald,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun RecentInstanceMobileRow(
    version: Version,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onSettings: () -> Unit
) {
    val activeAccent = MiraiThemeManager.currentAccent()
    val info = version.getVersionInfo()
    val mcVer = info?.minecraftVersion ?: "Unknown"
    val loaderName = info?.loaderInfo?.loader?.displayName ?: "Vanilla"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AerixRadii.cardSmall))
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(AerixRadii.control),
        color = ModrinthCardColor,
        border = BorderStroke(
            width = AerixSpacing.hairline,
            color = if (isSelected) activeAccent.copy(alpha = 0.55f) else ModrinthCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AerixSpacing.mdPlus, vertical = AerixSpacing.smNarrow),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
        ) {
            VersionIconImage(
                version = version,
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(AerixRadii.micro))
            )

            Text(
                text = "${version.getVersionName()} • $loaderName $mcVer",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AerixSurface.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onSettings,
                modifier = Modifier.size(26.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings_filled),
                    contentDescription = "Instance Settings",
                    tint = AerixSurface.textSecondary,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
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
    Row(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(AerixRadii.cardLarge))
            .background(AerixSurface.panelRaised)
            .border(AerixSpacing.hairline, AerixSurface.borderSoft, RoundedCornerShape(AerixRadii.cardLarge))
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
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
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
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                ),
                cursorBrush = SolidColor(ModrinthEmerald),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (value.isNotEmpty()) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.generic_clear),
                tint = AerixSurface.textSecondary,
                modifier = Modifier
                    .size(15.dp)
                    .clickable { onValueChange("") }
            )
        }
    }
}
