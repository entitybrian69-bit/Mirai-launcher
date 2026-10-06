/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.main

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.BuildKeys
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.coroutine.Task
import com.movtery.zalithlauncher.coroutine.TaskSystem
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.path.PathManager
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.AndroidStringText
import com.movtery.zalithlauncher.ui.androidText
import com.movtery.zalithlauncher.ui.components.AerixSectionHeader
import com.movtery.zalithlauncher.ui.components.liquidGlass
import com.movtery.zalithlauncher.ui.components.BackgroundCard
import com.movtery.zalithlauncher.ui.components.CardTitleLayout
import com.movtery.zalithlauncher.ui.guide.sendStartGuideOnce
import com.movtery.zalithlauncher.ui.screens.BackStackNavKey
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.AccountManageScreen
import com.movtery.zalithlauncher.ui.screens.content.FirstLoginMenu
import com.movtery.zalithlauncher.ui.screens.content.DownloadScreen
import com.movtery.zalithlauncher.ui.screens.content.FileSelectorScreen
import com.movtery.zalithlauncher.ui.screens.content.LauncherScreen
import com.movtery.zalithlauncher.ui.screens.content.LicenseScreen
import com.movtery.zalithlauncher.ui.screens.content.LogViewScreen
import com.movtery.zalithlauncher.ui.screens.content.MultiplayerScreen
import com.movtery.zalithlauncher.ui.screens.content.SettingsScreen
import com.movtery.zalithlauncher.ui.screens.content.VersionExportScreen
import com.movtery.zalithlauncher.ui.screens.content.VersionSettingsScreen
import com.movtery.zalithlauncher.ui.screens.content.VersionsManageScreen
import com.movtery.zalithlauncher.ui.screens.content.WebViewScreen
import com.movtery.zalithlauncher.ui.screens.content.assetinfo.AssetInfoScreen
import com.movtery.zalithlauncher.ui.screens.content.navigateToFileSelector
import com.movtery.zalithlauncher.ui.screens.navigateTo
import com.movtery.zalithlauncher.ui.screens.onBack
import com.movtery.zalithlauncher.ui.screens.rememberTransitionSpec
import com.movtery.zalithlauncher.ui.theme.backgroundColor
import com.movtery.zalithlauncher.ui.theme.festivals.FestivalTitleText
import com.movtery.zalithlauncher.ui.theme.onBackgroundColor
import com.movtery.zalithlauncher.ui.theme.AerixMetrics
import com.movtery.zalithlauncher.utils.animation.getAnimateTween
import com.movtery.zalithlauncher.utils.festival.LocalFestivals
import com.movtery.zalithlauncher.utils.file.formatFileSize
import com.movtery.zalithlauncher.viewmodel.ErrorViewModel
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.LocalBackgroundViewModel
import com.movtery.zalithlauncher.viewmodel.ModpackImportViewModel
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel
import com.movtery.zalithlauncher.viewmodel.sendKeepScreen

@Composable
fun MainScreen(
    screenBackStackModel: ScreenBackStackViewModel,
    eventViewModel: EventViewModel,
    modpackImportViewModel: ModpackImportViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val tasks by TaskSystem.tasksFlow.collectAsStateWithLifecycle()

    //监控当前是否有任务正在进行
    LaunchedEffect(tasks) {
        if (tasks.isEmpty()) {
            eventViewModel.sendKeepScreen(false)
        } else {
            //有任务正在进行，避免熄屏
            eventViewModel.sendKeepScreen(true)
        }
    }

    val isTaskMenuExpanded = AllSettings.launcherTaskMenuExpanded.state

    fun changeTasksExpandedState() {
        AllSettings.launcherTaskMenuExpanded.save(!isTaskMenuExpanded)
    }

    /** 回到主页面通用函数 */
    val toMainScreen: () -> Unit = {
        screenBackStackModel.mainScreen.clearWith(NormalNavKey.LauncherMain)
    }

    val mainScreenKey = screenBackStackModel.mainScreen.currentKey
    val inLauncherScreen = mainScreenKey == null || mainScreenKey is NormalNavKey.LauncherMain

    val isBackgroundValid = LocalBackgroundViewModel.current?.isValid == true
    val launcherBackgroundOpacity = AllSettings.launcherBackgroundOpacity.state.toFloat() / 100f

    // Keep the wallpaper present as the actual backplate of the glass system.
    // Use the preference as a soft nonlinear dimmer: the wallpaper stays luminous
    // through glass at its default value, while 100% still fully hides it.
    val wallpaperScrim = launcherBackgroundOpacity.coerceIn(0f, 1f).let { it * it * it }
    val backgroundColor = if (isBackgroundValid) {
        MaterialTheme.colorScheme.background.copy(alpha = wallpaperScrim)
    } else backgroundColor()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor,
        contentColor = onBackgroundColor()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val width = size.width.coerceAtLeast(1f)
                    val height = size.height.coerceAtLeast(1f)
                    val aurora = Brush.linearGradient(
                        colors = listOf(
                            AerixSurface.auroraCyan.copy(alpha = 0.10f),
                            Color.Transparent,
                            AerixSurface.auroraViolet.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        start = Offset.Zero,
                        end = Offset(width, height)
                    )
                    val cyanLens = Brush.radialGradient(
                        colors = listOf(
                            AerixSurface.auroraCyan.copy(alpha = 0.15f),
                            AerixSurface.auroraCyan.copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.04f, height * 0.06f),
                        radius = maxOf(width, height) * 0.74f
                    )
                    val violetLens = Brush.radialGradient(
                        colors = listOf(
                            AerixSurface.auroraViolet.copy(alpha = 0.12f),
                            AerixSurface.auroraViolet.copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.96f, height * 0.92f),
                        radius = maxOf(width, height) * 0.7f
                    )
                    onDrawBehind {
                        drawRect(brush = aurora)
                        drawRect(brush = cyanLens)
                        drawRect(brush = violetLens)
                    }
                }
        ) {
        Row(modifier = Modifier.fillMaxSize()) {
            MiraiNavigationRail(
                modifier = Modifier.fillMaxHeight(),
                selectedSection = mainScreenKey.toLauncherSection(),
                onNavigate = { section ->
                    when (section) {
                        LauncherSection.HOME -> screenBackStackModel.mainScreen.clearWith(NormalNavKey.LauncherMain)
                        LauncherSection.DISCOVER -> {
                            screenBackStackModel.downloadScreen.clearWith(screenBackStackModel.downloadModScreen)
                            screenBackStackModel.mainScreen.clearWith(screenBackStackModel.downloadScreen)
                        }
                        LauncherSection.LIBRARY -> screenBackStackModel.mainScreen.clearWith(NormalNavKey.VersionsManager)
                        LauncherSection.WALLPAPERS -> {
                            screenBackStackModel.settingsScreen.clearWith(NormalNavKey.Settings.Wallpapers)
                            screenBackStackModel.mainScreen.clearWith(screenBackStackModel.settingsScreen)
                        }
                        LauncherSection.MULTIPLAYER -> screenBackStackModel.mainScreen.clearWith(NormalNavKey.Multiplayer)
                        LauncherSection.SETTINGS -> {
                            if (screenBackStackModel.settingsScreen.currentKey === NormalNavKey.Settings.Wallpapers) {
                                screenBackStackModel.settingsScreen.clearWith(NormalNavKey.Settings.Renderer)
                            }
                            screenBackStackModel.mainScreen.clearWith(screenBackStackModel.settingsScreen)
                        }
                        LauncherSection.ACCOUNTS -> screenBackStackModel.mainScreen.clearWith(
                            NormalNavKey.AccountManager(FirstLoginMenu.NONE)
                        )
                    }
                },
                onCreateInstance = {
                    screenBackStackModel.downloadScreen.clearWith(screenBackStackModel.downloadGameScreen)
                    screenBackStackModel.mainScreen.clearWith(screenBackStackModel.downloadScreen)
                },
                onAccountClick = {
                    screenBackStackModel.mainScreen.clearWith(
                        NormalNavKey.AccountManager(FirstLoginMenu.NONE)
                    )
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                MainShellHeader(
                    mainScreenKey = mainScreenKey,
                    inLauncherScreen = inLauncherScreen,
                    activeTasksCount = tasks.size,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AerixSpacing.smPlus, vertical = AerixSpacing.sm),
                    onBack = { onBack(screenBackStackModel.mainScreen.backStack) },
                    onHome = toMainScreen,
                    onTasks = {
                        if (tasks.isNotEmpty()) changeTasksExpandedState() else {
                            screenBackStackModel.downloadScreen.clearWith(screenBackStackModel.downloadModScreen)
                            screenBackStackModel.mainScreen.clearWith(screenBackStackModel.downloadScreen)
                        }
                    },
                    onDiscover = {
                        screenBackStackModel.downloadScreen.clearWith(screenBackStackModel.downloadModScreen)
                        screenBackStackModel.mainScreen.clearWith(screenBackStackModel.downloadScreen)
                    },
                    onAccountClick = {
                        screenBackStackModel.mainScreen.clearWith(
                            NormalNavKey.AccountManager(FirstLoginMenu.NONE)
                        )
                    },
                    onOpenFileManager = {
                        screenBackStackModel.mainScreen.backStack.navigateToFileSelector(
                            startPath = PathManager.DIR_FILES_EXTERNAL.absolutePath,
                            selectFile = false,
                            saveKey = mainScreenKey ?: NormalNavKey.LauncherMain
                        ) {}
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    NavigationUI(
                        modifier = Modifier.fillMaxSize(),
                        screenBackStackModel = screenBackStackModel,
                        toMainScreen = toMainScreen,
                        eventViewModel = eventViewModel,
                        modpackImportViewModel = modpackImportViewModel,
                        submitError = submitError
                    )

                    TaskMenu(
                        tasks = tasks,
                        isExpanded = isTaskMenuExpanded,
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.36f)
                            .align(Alignment.CenterEnd)
                            .padding(all = AerixSpacing.sm)
                    ) {
                        changeTasksExpandedState()
                    }
                }
            }
        }
        }
    }
}

private fun TitledNavKey?.toLauncherSection(): LauncherSection? = when (this) {
    null, NormalNavKey.LauncherMain -> LauncherSection.HOME
    is NestedNavKey.Download,
    is NestedNavKey.DownloadGame,
    is NestedNavKey.DownloadModPack,
    is NestedNavKey.DownloadMod,
    is NestedNavKey.DownloadResourcePack,
    is NestedNavKey.DownloadSaves,
    is NestedNavKey.DownloadShaders,
    is NestedNavKey.DownloadFavorites,
    is NestedNavKey.AssetInfo -> LauncherSection.DISCOVER
    NormalNavKey.VersionsManager,
    is NestedNavKey.VersionSettings,
    is NestedNavKey.VersionExport -> LauncherSection.LIBRARY
    NormalNavKey.Multiplayer -> LauncherSection.MULTIPLAYER
    is NestedNavKey.Settings -> if (this.currentKey === NormalNavKey.Settings.Wallpapers) {
        LauncherSection.WALLPAPERS
    } else {
        LauncherSection.SETTINGS
    }
    is NormalNavKey.AccountManager -> LauncherSection.ACCOUNTS
    else -> null
}

@Composable
private fun <E : TitledNavKey> MainShellHeader(
    mainScreenKey: E?,
    inLauncherScreen: Boolean,
    activeTasksCount: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onTasks: () -> Unit,
    onDiscover: () -> Unit,
    onAccountClick: () -> Unit,
    onOpenFileManager: () -> Unit
) {
    val festivals = LocalFestivals.current
    val parentTitle = mainScreenKey?.title
    val childTitle = (mainScreenKey as? BackStackNavKey<*>)?.currentKey?.title
    val routeTitle = when {
        inLauncherScreen -> androidText(BuildKeys.LAUNCHER_NAME)
        parentTitle == null -> androidText(BuildKeys.LAUNCHER_IDENTIFIER)
        childTitle != null -> androidText(parentTitle, androidText(" / "), childTitle)
        else -> parentTitle
    }
    val showFestivalTitle = festivals.isNotEmpty() && (inLauncherScreen || parentTitle == null)
    val headerShape = RoundedCornerShape(AerixRadii.pill)

    Surface(
        modifier = modifier.liquidGlass(
            shape = headerShape,
            tint = AerixSurface.glassBlue,
            strength = 0.82f,
            elevation = AerixMetrics.glassFloatingElevation
        ),
        shape = headerShape,
        color = Color.Transparent,
        contentColor = AerixSurface.textPrimary,
        border = BorderStroke(1.dp, AerixSurface.borderSoft)
    ) {
        BoxWithConstraints {
            val compactActions = maxWidth < 610.dp
            val showAccountAction = maxWidth >= 460.dp
            val showSearch = maxWidth >= 650.dp
            val routeWidth = when {
                maxWidth < 430.dp -> 76.dp
                compactActions -> 126.dp
                else -> 210.dp
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AerixMetrics.shellHeaderHeight)
                    .padding(horizontal = AerixSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
            ) {
                if (!inLauncherScreen) {
                    HeaderIconButton(
                        iconRes = R.drawable.ic_arrow_back,
                        description = stringResource(R.string.generic_back),
                        onClick = onBack
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(AerixRadii.control))
                        .background(AerixSurface.accent.copy(alpha = 0.16f))
                        .liquidGlass(
                            shape = RoundedCornerShape(AerixRadii.control),
                            tint = AerixSurface.glassBlue,
                            strength = 0.8f,
                            elevation = AerixMetrics.glassSubtleElevation
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_aerix_mark),
                        contentDescription = "Aerix",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(
                    modifier = Modifier.widthIn(max = routeWidth),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "AERIX",
                        color = AerixSurface.accent,
                        fontSize = 9.sp,
                        letterSpacing = 1.8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                    Crossfade(
                        targetState = routeTitle.takeUnless { showFestivalTitle },
                        label = "aerixRouteTitle"
                    ) { title ->
                        if (showFestivalTitle) {
                            AerixSectionHeader(
                                titleContent = { style ->
                                    FestivalTitleText(festivals = festivals, style = style, maxLines = 1)
                                },
                                maxLines = 1,
                                titleStyle = MaterialTheme.typography.titleSmall.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        } else {
                            AerixSectionHeader(
                                title = title,
                                maxLines = 1,
                                titleStyle = MaterialTheme.typography.titleSmall.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AerixSurface.textPrimary
                                )
                            )
                        }
                    }
                }

                if (!inLauncherScreen && !compactActions) {
                    HeaderIconButton(
                        iconRes = R.drawable.ic_home_filled,
                        description = stringResource(R.string.generic_main_menu),
                        tint = AerixSurface.accent,
                        onClick = onHome
                    )
                }

                if (showSearch) {
                    Surface(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .widthIn(min = 150.dp, max = 340.dp)
                            .height(AerixMetrics.shellActionHeight)
                            .liquidGlass(
                                shape = CircleShape,
                                tint = AerixSurface.glassTint,
                                strength = 0.48f,
                                elevation = AerixMetrics.glassSubtleElevation
                            ),
                        shape = CircleShape,
                        color = Color.Transparent,
                        contentColor = AerixSurface.textSecondary,
                        border = BorderStroke(1.dp, AerixSurface.borderSoft),
                        onClick = onDiscover
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = AerixSpacing.md),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_search),
                                contentDescription = null,
                                tint = AerixSurface.glassBlue,
                                modifier = Modifier.size(17.dp)
                            )
                            Text(
                                text = "Explore mods, versions, worlds",
                                color = AerixSurface.textSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "⌕",
                                color = AerixSurface.textMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                HeaderActionPill(
                    iconRes = R.drawable.ic_download,
                    label = if (activeTasksCount > 0) "$activeTasksCount Tasks" else "Tasks",
                    compact = compactActions,
                    selected = activeTasksCount > 0,
                    onClick = onTasks
                )
                HeaderActionPill(
                    iconRes = R.drawable.ic_folder_filled,
                    label = "Files",
                    compact = compactActions,
                    onClick = onOpenFileManager
                )
                if (showAccountAction) {
                    HeaderActionPill(
                        iconRes = R.drawable.ic_person_outlined,
                        label = "Account",
                        compact = true,
                        selected = false,
                        onClick = onAccountClick
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderIconButton(
    iconRes: Int,
    description: String,
    tint: Color = AerixSurface.textPrimary,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(38.dp)
            .liquidGlass(
                shape = CircleShape,
                tint = AerixSurface.glassTint,
                strength = 0.42f,
                elevation = AerixMetrics.glassSubtleElevation
            ),
        shape = CircleShape,
        color = Color.Transparent,
        contentColor = tint,
        border = BorderStroke(1.dp, AerixSurface.borderSoft),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = description,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun HeaderActionPill(
    iconRes: Int,
    label: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val actionShape = RoundedCornerShape(AerixRadii.control)
    Surface(
        modifier = modifier
            .height(AerixMetrics.shellActionHeight)
            .liquidGlass(
                shape = actionShape,
                tint = if (selected) AerixSurface.accent else AerixSurface.glassTint,
                strength = if (selected) 0.92f else 0.56f,
                elevation = if (selected) AerixMetrics.glassSelectedElevation else AerixMetrics.glassSubtleElevation
            ),
        shape = actionShape,
        color = Color.Transparent,
        contentColor = if (selected) AerixSurface.accent else AerixSurface.textPrimary,
        border = BorderStroke(
            AerixSpacing.hairline,
            if (selected) AerixSurface.borderHighlight else AerixSurface.borderSoft
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (compact) AerixSpacing.sm else AerixSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = if (compact) label else null,
                modifier = Modifier.size(16.dp)
            )
            if (!compact) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun NavigationUI(
    modifier: Modifier = Modifier,
    screenBackStackModel: ScreenBackStackViewModel,
    toMainScreen: () -> Unit,
    eventViewModel: EventViewModel,
    modpackImportViewModel: ModpackImportViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val backStack = screenBackStackModel.mainScreen.backStack
    val currentKey = backStack.lastOrNull()

    LaunchedEffect(currentKey) {
        screenBackStackModel.mainScreen.currentKey = currentKey
    }

    if (backStack.isNotEmpty()) {
        /** 导航至版本详细信息屏幕 */
        val navigateToVersions: (Version) -> Unit = { version ->
            screenBackStackModel.mainScreen.navigateTo(
                screenKey = NestedNavKey.VersionSettings(version),
                useClassEquality = true
            )
        }
        /** 导航至整合包导出屏幕 */
        val navigateToExport: (Version) -> Unit = { version ->
            screenBackStackModel.mainScreen.removeAndNavigateTo(
                remove = NestedNavKey.VersionSettings::class,
                screenKey = NestedNavKey.VersionExport(version),
                useClassEquality = true
            )
        }

        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            onBack = {
                onBack(backStack)
            },
            transitionSpec = rememberTransitionSpec(),
            popTransitionSpec = rememberTransitionSpec(),
            entryProvider = entryProvider {
                entry<NormalNavKey.LauncherMain> {
                    LauncherScreen(
                        backStackViewModel = screenBackStackModel,
                        navigateToVersions = navigateToVersions,
                        onLaunchGame = { version ->
                            eventViewModel.sendEvent(
                                EventViewModel.Event.Launch.Game(version)
                            )
                        },
                        onOpenLink = {
                            eventViewModel.sendEvent(EventViewModel.Event.OpenLink(it))
                        },
                        startGuideOnce = { keys ->
                            eventViewModel.sendStartGuideOnce(keys)
                        }
                    )
                }
                entry<NestedNavKey.Settings> { key ->
                    SettingsScreen(
                        key = key,
                        backStackViewModel = screenBackStackModel,
                        openLicenseScreen = { raw ->
                            backStack.navigateTo(NormalNavKey.License(raw))
                        },
                        eventViewModel = eventViewModel,
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.License> { key ->
                    LicenseScreen(
                        key = key,
                        backStackViewModel = screenBackStackModel
                    )
                }
                entry<NormalNavKey.AccountManager> { key ->
                    AccountManageScreen(
                        key = key,
                        backStackViewModel = screenBackStackModel,
                        backToMainScreen = toMainScreen,
                        openLink = { url ->
                            eventViewModel.sendEvent(EventViewModel.Event.OpenLink(url))
                        },
                        eventViewModel = eventViewModel,
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.WebScreen> { key ->
                    WebViewScreen(
                        key = key,
                        backStackViewModel = screenBackStackModel,
                        eventViewModel = eventViewModel
                    )
                }
                entry<NormalNavKey.VersionsManager> {
                    VersionsManageScreen(
                        backScreenViewModel = screenBackStackModel,
                        navigateToVersions = navigateToVersions,
                        navigateToExport = navigateToExport,
                        eventViewModel = eventViewModel,
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.FileSelector> { key ->
                    FileSelectorScreen(
                        key = key,
                        backScreenViewModel = screenBackStackModel
                    ) {
                        backStack.removeLastOrNull()
                    }
                }
                entry<NestedNavKey.VersionSettings> { key ->
                    VersionSettingsScreen(
                        key = key,
                        backScreenViewModel = screenBackStackModel,
                        backToMainScreen = toMainScreen,
                        onExportModpack = {
                            navigateToExport(key.version)
                        },
                        eventViewModel = eventViewModel,
                        submitError = submitError
                    )
                }
                entry<NestedNavKey.VersionExport> { key ->
                    VersionExportScreen(
                        key = key,
                        backScreenViewModel = screenBackStackModel,
                        eventViewModel = eventViewModel,
                        backToMainScreen = toMainScreen
                    )
                }
                entry<NestedNavKey.Download> { key ->
                    DownloadScreen(
                        key = key,
                        backScreenViewModel = screenBackStackModel,
                        eventViewModel = eventViewModel,
                        modpackImportViewModel = modpackImportViewModel,
                        submitError = submitError
                    )
                }
                entry<NestedNavKey.AssetInfo> { key ->
                    AssetInfoScreen(
                        key = key,
                        mainScreenKey = screenBackStackModel.mainScreen.currentKey,
                        assetInfoScreenKey = key.currentKey,
                        eventViewModel = eventViewModel,
                        submitError = submitError,
                    )
                }
                entry<NormalNavKey.Multiplayer> {
                    MultiplayerScreen(
                        backScreenViewModel = screenBackStackModel,
                        eventViewModel = eventViewModel
                    )
                }
                entry<NormalNavKey.LogView> { key ->
                    LogViewScreen(
                        key = key,
                        backStackViewModel = screenBackStackModel,
                    )
                }
            }
        )
    } else {
        Box(modifier)
    }
}

@Composable
private fun TaskMenu(
    tasks: List<Task>,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    changeExpandedState: () -> Unit = {}
) {
    val show = isExpanded && tasks.isNotEmpty()

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    AnimatedVisibility(
        modifier = modifier,
        enter = slideInHorizontally(
            initialOffsetX = { if (isRtl) -it else it },
            animationSpec = getAnimateTween()
        ) + fadeIn(),
        exit = slideOutHorizontally(
            targetOffsetX = { if (isRtl) -it else it },
            animationSpec = getAnimateTween()
        ) + fadeOut(),
        visible = show
    ) {
        BackgroundCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = AerixSpacing.xs),
            influencedByBackground = false,
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = AerixSurface.panel,
                contentColor = Color.White
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
        ) {
            Column {
                CardTitleLayout(blur = 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AerixSpacing.mdPlus, vertical = AerixSpacing.smPlus),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Download Tasks (${tasks.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        IconButton(
                            modifier = Modifier.size(28.dp),
                            onClick = changeExpandedState
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = stringResource(R.string.generic_collapse),
                                tint = AerixSurface.textSecondary
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = AerixSpacing.md, vertical = AerixSpacing.smCompact)
                ) {
                    items(tasks) { task ->
                        val taskProgress by task.progress.collectAsStateWithLifecycle()
                        val taskMessage by task.message.collectAsStateWithLifecycle()
                        val rateBytesPerSec by task.rateBytesPerSec.collectAsStateWithLifecycle()

                        TaskItem(
                            taskProgress = taskProgress,
                            taskMessage = taskMessage,
                            rateBytesPerSec = rateBytesPerSec,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = AerixSpacing.smCompact)
                        ) {
                            //取消任务
                            TaskSystem.cancelTask(task.id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    taskProgress: Float,
    taskMessage: AndroidStringText?,
    rateBytesPerSec: Long?,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    color: Color = AerixSurface.panelRaised,
    contentColor: Color = Color.White,
    onCancelClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier.padding(all = AerixSpacing.smPlus),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(AerixSurface.accentContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(R.drawable.ic_download_2_filled),
                    contentDescription = null,
                    tint = AerixSurface.accent
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.xsPlus)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        taskMessage?.let { message ->
                            AndroidStringText(
                                text = message,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    taskProgress.takeIf { it >= 0f }?.let { progress ->
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = AerixSurface.accent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (taskProgress < 0) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp),
                        color = AerixSurface.accent,
                        trackColor = AerixSurface.panelRaised
                    )
                } else {
                    LinearProgressIndicator(
                        progress = { taskProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp),
                        color = AerixSurface.accent,
                        trackColor = AerixSurface.panelRaised
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rateBytesPerSec?.let { bytes ->
                        val text = remember(bytes) { "${formatFileSize(bytes)}/s" }
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelSmall,
                            color = AerixSurface.textSecondary
                        )
                    } ?: Text(
                        text = "Downloading...",
                        style = MaterialTheme.typography.labelSmall,
                        color = AerixSurface.textSecondary
                    )
                }
            }

            IconButton(
                modifier = Modifier
                    .size(26.dp)
                    .align(Alignment.CenterVertically),
                onClick = onCancelClick
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.generic_cancel),
                    tint = AerixSurface.textSecondary
                )
            }
        }
    }
}
