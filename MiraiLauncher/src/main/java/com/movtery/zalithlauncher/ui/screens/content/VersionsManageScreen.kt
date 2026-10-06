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
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.content

import android.os.Environment
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.movtery.zalithlauncher.ui.theme.AerixMetrics
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.ui.theme.MiraiThemeManager
import com.movtery.zalithlauncher.game.path.GamePathManager
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionComparator
import com.movtery.zalithlauncher.game.version.installed.VersionType
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.game.version.installed.cleanup.GameAssetCleaner
import com.movtery.zalithlauncher.ui.activities.MainActivity
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.AerixPillTab
import com.movtery.zalithlauncher.ui.components.AerixPillTabRow
import com.movtery.zalithlauncher.ui.components.liquidGlass
import com.movtery.zalithlauncher.ui.components.MarqueeText
import com.movtery.zalithlauncher.ui.components.ScalingActionButton
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.content.elements.CleanupOperation
import com.movtery.zalithlauncher.ui.screens.content.elements.GamePathItemLayout
import com.movtery.zalithlauncher.ui.screens.content.elements.GamePathOperation
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionCategory
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionsOperation
import com.movtery.zalithlauncher.ui.screens.content.home.ModrinthCompactSearchField
import com.movtery.zalithlauncher.ui.screens.content.home.ModrinthMetaPill
import com.movtery.zalithlauncher.ui.screens.content.home.resolveRendererShortLabel
import com.movtery.zalithlauncher.utils.animation.swapAnimateDpAsState
import com.movtery.zalithlauncher.utils.canHandlePermission
import com.movtery.zalithlauncher.utils.checkStoragePermissions
import com.movtery.zalithlauncher.viewmodel.ErrorViewModel
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel
import com.movtery.zalithlauncher.viewmodel.sendKeepScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private enum class LibraryFilterGroup(val label: String) {
    ALL("All"),
    MODPACKS("Modpacks"),
    VANILLA("Vanilla"),
    PINNED("Pinned")
}

private enum class LibrarySortMode(val label: String) {
    LAST_PLAYED("Sort: Pinned & Active"),
    NAME_ASC("Sort: Name (A–Z)"),
    MC_VERSION("Sort: MC Version")
}

private class VersionsScreenViewModel : ViewModel() {
    var versionCategory by mutableStateOf(VersionCategory.ALL)
        private set
    var resortKey by mutableIntStateOf(0)
        private set

    var gamePathOperation by mutableStateOf<GamePathOperation>(GamePathOperation.None)

    var allVersionsCount by mutableIntStateOf(0)
    var vanillaVersionsCount by mutableIntStateOf(0)
    var modloaderVersionsCount by mutableIntStateOf(0)

    fun startRefreshVersions() {
        if (!VersionsManager.isRefreshing.value) {
            VersionsManager.refresh("VersionsScreenViewModel.startRefreshVersions")
        }
    }

    private var currentJob: Job? = null
    private var mutex: Mutex = Mutex()

    fun changeCategory(category: VersionCategory) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            mutex.withLock {
                this@VersionsScreenViewModel.versionCategory = category
            }
        }
    }

    fun resortVersions() {
        resortKey++
    }

    var cleanupOperation by mutableStateOf<CleanupOperation>(CleanupOperation.None)
    var cleaner by mutableStateOf<GameAssetCleaner?>(null)

    fun cleanUnusedFiles(
        onStart: () -> Unit = {},
        onStop: () -> Unit = {}
    ) {
        cleaner = GameAssetCleaner(
            scope = viewModelScope
        ).also {
            cleanupOperation = CleanupOperation.Clean
            it.start(
                onEnd = { count, size ->
                    cleaner = null
                    cleanupOperation = CleanupOperation.Success(count, size)
                    onStop()
                },
                onThrowable = { th ->
                    cleaner = null
                    cleanupOperation = CleanupOperation.Error(th)
                    onStop()
                }
            )
        }
        onStart()
    }

    fun cancelCleaner() {
        cleaner?.cancel()
        cleaner = null
        cleanupOperation = CleanupOperation.None
    }

    override fun onCleared() {
        cancelCleaner()
        currentJob?.cancel()
    }
}

@Composable
private fun rememberVersionViewModel(): VersionsScreenViewModel {
    return viewModel(
        key = NormalNavKey.VersionsManager.toString()
    ) {
        VersionsScreenViewModel()
    }
}

@Composable
private fun rememberVersions(
    versions: StateFlow<List<Version>>,
    viewModel: VersionsScreenViewModel,
): State<List<Version>> {
    val vers by versions.collectAsStateWithLifecycle()
    val category = viewModel.versionCategory
    val resortKey = viewModel.resortKey

    return remember(vers, category, resortKey) {
        derivedStateOf {
            viewModel.allVersionsCount = vers.size

            val vanillaVersions = vers
                .filter { ver -> ver.versionType == VersionType.VANILLA }
                .also { viewModel.vanillaVersionsCount = it.size }
            val modloaderVersions = vers
                .filter { ver -> ver.versionType == VersionType.MODLOADERS }
                .also { viewModel.modloaderVersionsCount = it.size }

            when (category) {
                VersionCategory.ALL -> vers
                VersionCategory.VANILLA -> vanillaVersions
                VersionCategory.MODLOADER -> modloaderVersions
            }.sortedWith(VersionComparator)
        }
    }
}

@Composable
fun VersionsManageScreen(
    backScreenViewModel: ScreenBackStackViewModel,
    navigateToVersions: (Version) -> Unit,
    navigateToExport: (Version) -> Unit,
    eventViewModel: EventViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val viewModel = rememberVersionViewModel()

    val versions by rememberVersions(VersionsManager.versions, viewModel)
    val currentVersion by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val isRefreshing by VersionsManager.isRefreshing.collectAsStateWithLifecycle()
    var showGamePathDrawer by rememberSaveable { mutableStateOf(false) }

    GamePathOperation(
        gamePathOperation = viewModel.gamePathOperation,
        changeState = { viewModel.gamePathOperation = it },
        submitError = submitError
    )

    BaseScreen(
        screenKey = NormalNavKey.VersionsManager,
        currentKey = backScreenViewModel.mainScreen.currentKey
    ) { isVisible ->
        Row(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = showGamePathDrawer,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(240.dp),
                    color = AerixSurface.panel,
                    border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
                ) {
                    LeftMenu(
                        isVisible = isVisible,
                        isRefreshing = isRefreshing,
                        swapToFileSelector = { path ->
                            backScreenViewModel.mainScreen.backStack.navigateToFileSelector(
                                startPath = path,
                                selectFile = false,
                                saveKey = NormalNavKey.VersionsManager
                            ) { selectedPath ->
                                viewModel.gamePathOperation = GamePathOperation.AddNewPath(selectedPath)
                            }
                        },
                        onCleanupGameFiles = {
                            if (viewModel.cleanupOperation == CleanupOperation.None) {
                                viewModel.cleanupOperation = CleanupOperation.Tip
                            }
                        },
                        changePathOperation = {
                            viewModel.gamePathOperation = it
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            VersionsLayout(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                isVisible = isVisible,
                isRefreshing = isRefreshing,
                versions = versions,
                currentVersion = currentVersion,
                versionCategory = viewModel.versionCategory,
                onCategoryChange = { viewModel.changeCategory(it) },
                allVersionsCount = viewModel.allVersionsCount,
                vanillaVersionsCount = viewModel.vanillaVersionsCount,
                modloaderVersionsCount = viewModel.modloaderVersionsCount,
                showGamePathDrawer = showGamePathDrawer,
                onToggleGamePathDrawer = { showGamePathDrawer = !showGamePathDrawer },
                navigateToVersions = navigateToVersions,
                navigateToExport = navigateToExport,
                onLaunchVersion = { version ->
                    VersionsManager.saveVersion(version)
                    eventViewModel.sendEvent(EventViewModel.Event.Launch.Game(version))
                },
                submitError = submitError,
                onRefresh = {
                    viewModel.startRefreshVersions()
                },
                onVersionPinned = {
                    viewModel.resortVersions()
                },
                onInstall = {
                    backScreenViewModel.navigateToDownload()
                }
            )

            CleanupOperation(
                operation = viewModel.cleanupOperation,
                changeOperation = { viewModel.cleanupOperation = it },
                cleaner = viewModel.cleaner,
                onClean = {
                    viewModel.cleanUnusedFiles(
                        onStart = {
                            eventViewModel.sendKeepScreen(true)
                        },
                        onStop = {
                            eventViewModel.sendKeepScreen(false)
                        }
                    )
                },
                onCancel = {
                    viewModel.cancelCleaner()
                    eventViewModel.sendKeepScreen(false)
                },
                submitError = submitError
            )
        }
    }
}

@Composable
private fun LeftMenu(
    isVisible: Boolean,
    isRefreshing: Boolean,
    swapToFileSelector: (path: String) -> Unit,
    onCleanupGameFiles: () -> Unit,
    changePathOperation: (GamePathOperation) -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceXOffset by swapAnimateDpAsState(
        targetValue = (-40).dp,
        swapIn = isVisible,
        isHorizontal = true
    )

    Column(
        modifier = modifier.offset { IntOffset(x = surfaceXOffset.roundToPx(), y = 0) },
    ) {
        val gamePaths by GamePathManager.gamePathData.collectAsStateWithLifecycle()
        val currentPath by GamePathManager.currentPath.collectAsStateWithLifecycle()
        val context = LocalContext.current

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = AerixSpacing.md,
                end = AerixSpacing.md,
                top = AerixSpacing.md,
                bottom = AerixSpacing.md
            )
        ) {
            items(gamePaths, key = { it.id }) { pathItem ->
                GamePathItemLayout(
                    item = pathItem,
                    selected = currentPath == pathItem.path,
                    enabled = canHandlePermission,
                    onClick = {
                        if (!isRefreshing) {
                            if (pathItem.id == GamePathManager.DEFAULT_ID) {
                                GamePathManager.saveDefaultPath()
                            } else {
                                (context as? MainActivity)?.let { activity ->
                                    checkStoragePermissions(
                                        activity = activity,
                                        message = activity.getString(R.string.versions_manage_game_storage_permissions),
                                        messageSdk30 = activity.getString(R.string.versions_manage_game_storage_permissions_sdk30),
                                        hasPermission = {
                                            GamePathManager.saveCurrentPath(pathItem.id)
                                        }
                                    )
                                }
                            }
                        }
                    },
                    onDelete = {
                        changePathOperation(GamePathOperation.DeletePath(pathItem))
                    },
                    onRename = {
                        changePathOperation(GamePathOperation.RenamePath(pathItem))
                    }
                )
            }
        }

        ScalingActionButton(
            modifier = Modifier
                .padding(horizontal = AerixSpacing.md)
                .padding(top = AerixSpacing.sm)
                .fillMaxWidth(),
            onClick = {
                (context as? MainActivity)?.let { activity ->
                    checkStoragePermissions(
                        activity = activity,
                        message = activity.getString(R.string.versions_manage_game_path_storage_permissions),
                        messageSdk30 = activity.getString(R.string.versions_manage_game_path_storage_permissions_sdk30),
                        hasPermission = {
                            swapToFileSelector(Environment.getExternalStorageDirectory().absolutePath)
                        }
                    )
                }
            },
            enabled = canHandlePermission
        ) {
            MarqueeText(text = stringResource(R.string.versions_manage_game_path_add_new))
        }

        ScalingActionButton(
            modifier = Modifier
                .padding(start = AerixSpacing.md, end = AerixSpacing.md, top = AerixSpacing.sm, bottom = AerixSpacing.md)
                .fillMaxWidth(),
            onClick = onCleanupGameFiles
        ) {
            MarqueeText(text = stringResource(R.string.versions_manage_cleanup))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)
@Composable
private fun VersionsLayout(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    isRefreshing: Boolean,
    versions: List<Version>,
    currentVersion: Version?,
    versionCategory: VersionCategory,
    onCategoryChange: (VersionCategory) -> Unit,
    allVersionsCount: Int,
    vanillaVersionsCount: Int,
    modloaderVersionsCount: Int,
    showGamePathDrawer: Boolean,
    onToggleGamePathDrawer: () -> Unit,
    navigateToVersions: (Version) -> Unit,
    navigateToExport: (Version) -> Unit,
    onLaunchVersion: (Version) -> Unit,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit,
    onRefresh: () -> Unit,
    onVersionPinned: () -> Unit,
    onInstall: () -> Unit,
) {
    val surfaceYOffset by swapAnimateDpAsState(
        targetValue = (-40).dp,
        swapIn = isVisible
    )

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(LibraryFilterGroup.ALL) }
    var sortMode by rememberSaveable { mutableStateOf(LibrarySortMode.LAST_PLAYED) }
    var showSortMenu by remember { mutableStateOf(false) }

    LaunchedEffect(selectedFilter) {
        when (selectedFilter) {
            LibraryFilterGroup.MODPACKS -> if (versionCategory != VersionCategory.MODLOADER) onCategoryChange(VersionCategory.MODLOADER)
            LibraryFilterGroup.VANILLA -> if (versionCategory != VersionCategory.VANILLA) onCategoryChange(VersionCategory.VANILLA)
            else -> if (versionCategory != VersionCategory.ALL) onCategoryChange(VersionCategory.ALL)
        }
    }

    val displayedVersions = remember(versions, currentVersion, searchQuery, selectedFilter, sortMode) {
        val q = searchQuery.trim().lowercase()
        val filtered = versions.filter { version ->
            val info = version.getVersionInfo()
            val mcVer = info?.minecraftVersion.orEmpty()
            val loader = info?.loaderInfo?.loader?.displayName.orEmpty()

            val matchesFilter = when (selectedFilter) {
                LibraryFilterGroup.ALL -> true
                LibraryFilterGroup.MODPACKS -> info?.loaderInfo != null
                LibraryFilterGroup.VANILLA -> info?.loaderInfo == null
                LibraryFilterGroup.PINNED -> version.pinnedState
            }
            val matchesQuery = q.isEmpty() ||
                version.getVersionName().lowercase().contains(q) ||
                version.getVersionSummary().lowercase().contains(q) ||
                mcVer.lowercase().contains(q) ||
                loader.lowercase().contains(q)
            matchesFilter && matchesQuery
        }

        when (sortMode) {
            LibrarySortMode.LAST_PLAYED -> filtered
            LibrarySortMode.NAME_ASC -> filtered.sortedBy { it.getVersionName().lowercase() }
            LibrarySortMode.MC_VERSION -> filtered.sortedByDescending { it.getVersionInfo()?.minecraftVersion.orEmpty() }
        }
    }

    Box(
        modifier = modifier.offset { IntOffset(x = 0, y = surfaceYOffset.roundToPx()) }
    ) {
        if (isRefreshing) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        } else {
            var versionsOperation by remember { mutableStateOf<VersionsOperation>(VersionsOperation.None) }
            VersionsOperation(
                versionsOperation = versionsOperation,
                updateVersionsOperation = { versionsOperation = it },
                submitError = submitError
            )

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val columns = when {
                    maxWidth > maxHeight && maxWidth >= 1040.dp -> 4
                    maxWidth > maxHeight && maxWidth >= 700.dp -> 3
                    else -> 2
                }
                val compactToolbar = maxWidth < 520.dp

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = AerixSpacing.mdPlus, vertical = AerixSpacing.smPlus),
                    verticalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
                ) {
                    // Top Mobile Library Toolbar (Mockup #2: Search + Sort Icon + New Instance CTA)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                    ) {
                        ModrinthCompactSearchField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = "Search instances...",
                            modifier = Modifier.weight(1f)
                        )

                        Box {
                            Surface(
                                modifier = Modifier.size(36.dp),
                                shape = RoundedCornerShape(AerixRadii.controlSmall),
                                color = AerixSurface.panel,
                                border = BorderStroke(AerixSpacing.hairline, AerixSurface.border),
                                onClick = { showSortMenu = true }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_sort),
                                        contentDescription = "Sort",
                                        tint = AerixSurface.textSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                LibrarySortMode.entries.forEach { mode ->
                                    DropdownMenuItem(
                                        text = { Text(mode.label) },
                                        onClick = {
                                            sortMode = mode
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(AerixRadii.controlSmall),
                            color = if (showGamePathDrawer) AerixSurface.accentContainer else AerixSurface.panel,
                            border = BorderStroke(
                                AerixSpacing.hairline,
                                if (showGamePathDrawer) MiraiThemeManager.currentAccent() else AerixSurface.border
                            ),
                            onClick = onToggleGamePathDrawer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_folder_outlined),
                                    contentDescription = "Directories",
                                    tint = if (showGamePathDrawer) MiraiThemeManager.currentAccent() else AerixSurface.textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .height(36.dp)
                                .widthIn(min = 36.dp),
                            shape = RoundedCornerShape(AerixRadii.control),
                            color = MiraiThemeManager.currentAccent(),
                            contentColor = AerixSurface.onAccent,
                            onClick = onInstall
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = if (compactToolbar) AerixSpacing.sm else AerixSpacing.mdPlus,
                                    vertical = AerixSpacing.smTight
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_add),
                                    contentDescription = if (compactToolbar) "New Instance" else null,
                                    modifier = Modifier.size(16.dp)
                                )
                                if (!compactToolbar) {
                                    Text(
                                        text = "New Instance",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }

                    AerixPillTabRow(modifier = Modifier.fillMaxWidth()) {
                        LibraryFilterGroup.entries.forEach { group ->
                            val selected = selectedFilter == group
                            AerixPillTab(
                                selected = selected,
                                onClick = { selectedFilter = group }
                            ) {
                                Text(
                                    text = group.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Adaptive library grid expands to 3–4 cards across in landscape.
                    if (displayedVersions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(AerixRadii.card),
                                color = AerixSurface.panel,
                                border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
                            ) {
                                Column(
                                    modifier = Modifier.padding(AerixSpacing.xxl),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
                                ) {
                                    Text(
                                        text = stringResource(R.string.versions_manage_no_versions),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Button(
                                        onClick = onInstall,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MiraiThemeManager.currentAccent(),
                                            contentColor = AerixSurface.onAccent
                                        )
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_add),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(AerixSpacing.smCompact))
                                        Text(
                                            text = "Create First Instance",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        val rows = displayedVersions.chunked(columns)
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(AerixSpacing.md),
                            contentPadding = PaddingValues(bottom = AerixSpacing.mdPlus)
                        ) {
                            items(
                                count = rows.size,
                                key = { idx -> rows[idx].joinToString("_") { it.toString() } }
                            ) { idx ->
                                val rowItems = rows[idx]
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.md)
                                ) {
                                    rowItems.forEach { version ->
                                        ModrinthLibraryInstanceCard(
                                            version = version,
                                            selected = version == currentVersion,
                                            onSelect = {
                                                if (version != currentVersion) {
                                                    if (!VersionsManager.saveVersion(version)) {
                                                        versionsOperation = VersionsOperation.InvalidDelete(version)
                                                    }
                                                }
                                            },
                                            onPlayClick = { onLaunchVersion(version) },
                                            onSettingsClick = { navigateToVersions(version) },
                                            onPinToggle = {
                                                runCatching {
                                                    version.setPinnedAndSave(!version.pinnedState)
                                                }.onSuccess {
                                                    onVersionPinned()
                                                }
                                            },
                                            onRenameClick = { versionsOperation = VersionsOperation.Rename(version) },
                                            onCopyClick = { versionsOperation = VersionsOperation.Copy(version) },
                                            onExportClick = { navigateToExport(version) },
                                            onDeleteClick = { versionsOperation = VersionsOperation.Delete(version) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    repeat((columns - rowItems.size).coerceAtLeast(0)) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModrinthLibraryInstanceCard(
    version: Version,
    selected: Boolean,
    onSelect: () -> Unit,
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPinToggle: () -> Unit,
    onRenameClick: () -> Unit,
    onCopyClick: () -> Unit,
    onExportClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "libCardScale"
    )
    val activeAccent = MiraiThemeManager.currentAccent()
    val borderColor by animateColorAsState(
        targetValue = if (selected) activeAccent.copy(alpha = 0.84f) else AerixSurface.borderSoft,
        animationSpec = tween(220),
        label = "libCardBorder"
    )

    val info = version.getVersionInfo()
    val mcVer = info?.minecraftVersion ?: "Unknown"
    val loaderName = info?.loaderInfo?.loader?.displayName ?: "Vanilla"
    var menuExpanded by remember { mutableStateOf(false) }

    val cardShape = RoundedCornerShape(AerixRadii.cardLarge)
    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .liquidGlass(
                shape = cardShape,
                tint = if (selected) activeAccent else AerixSurface.glassBlue,
                strength = if (selected) 0.92f else 0.62f,
                elevation = AerixMetrics.glassSubtleElevation
            ),
        shape = cardShape,
        color = Color.Transparent,
        border = BorderStroke(if (selected) 1.5.dp else AerixSpacing.hairline, borderColor),
        onClick = onSelect
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AerixSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
            ) {
                Surface(
                    shape = RoundedCornerShape(AerixRadii.controlSmall),
                    color = AerixSurface.panelRaised,
                    border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
                ) {
                    VersionIconImage(
                        version = version,
                        modifier = Modifier
                            .padding(AerixSpacing.xsPlus)
                            .size(36.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AerixSpacing.xsPlus)
                ) {
                    Text(
                        text = version.getVersionName(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smCompact),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ModrinthMetaPill(
                            text = "$loaderName $mcVer",
                            backgroundColor = AerixSurface.panelRaised,
                            textColor = AerixSurface.textPrimary
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Overflow Options Gear Button (Mockup #2)
                Box {
                    Surface(
                        shape = RoundedCornerShape(AerixRadii.compact),
                        color = AerixSurface.panelRaised,
                        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft),
                        contentColor = AerixSurface.textSecondary,
                        onClick = { menuExpanded = true }
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_more_horiz),
                                contentDescription = stringResource(R.string.generic_more),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.versions_manage_pin)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(
                                        if (version.pinnedState) R.drawable.ic_pinned_filled else R.drawable.ic_pinned_outlined
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onPinToggle()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.generic_rename)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_edit_filled),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onRenameClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.generic_copy)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_file_copy_filled),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onCopyClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.versions_export)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_folder_zip_filled),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onExportClick()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.generic_delete),
                                    color = AerixSurface.danger
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_delete_filled),
                                    contentDescription = null,
                                    tint = AerixSurface.danger,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onDeleteClick()
                            }
                        )
                    }
                }

                // Right: Settings Gear + Emerald '▶ Play' Pill Button (Mockup #2)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(AerixRadii.compact),
                        color = AerixSurface.panelRaised,
                        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft),
                        contentColor = AerixSurface.textSecondary,
                        onClick = onSettingsClick
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_settings_filled),
                                contentDescription = stringResource(R.string.versions_manage_settings),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(AerixRadii.card),
                        color = MiraiThemeManager.currentAccent(),
                        contentColor = AerixSurface.onAccent,
                        onClick = onPlayClick
                    ) {
                        Row(
                            modifier = Modifier
                                .height(32.dp)
                                .padding(horizontal = AerixSpacing.mdPlus),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_play_arrow_filled),
                                contentDescription = stringResource(R.string.main_launch_game),
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Play",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}
