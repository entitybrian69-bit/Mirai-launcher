package com.movtery.zalithlauncher.viewmodel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movtery.zalithlauncher.BuildConfig
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.components.MarqueeText
import com.movtery.zalithlauncher.ui.components.SimpleListDialog
import com.movtery.zalithlauncher.ui.screens.content.elements.DisabledAlpha
import com.movtery.zalithlauncher.ui.upgrade.UpgradeDialog
import com.movtery.zalithlauncher.ui.upgrade.UpgradeFilesDialog
import com.movtery.zalithlauncher.upgrade.RemoteData
import com.movtery.zalithlauncher.upgrade.TooFrequentOperationException
import com.movtery.zalithlauncher.upgrade.UpgradeManifestResult
import com.movtery.zalithlauncher.upgrade.loadUpgradeManifest
import com.movtery.zalithlauncher.utils.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

private const val TAG = "LauncherUpgradeVM"

sealed interface LauncherUpgradeOperation {
    data object None : LauncherUpgradeOperation
    data class Upgrade(val data: RemoteData) : LauncherUpgradeOperation
    data class SelectApk(val data: RemoteData) : LauncherUpgradeOperation
    data class OpenCloudDrive(val cloudDrive: RemoteData.CloudDrive) : LauncherUpgradeOperation
}

private sealed interface RemoteDataFetchResult {
    data class Available(val data: RemoteData) : RemoteDataFetchResult
    data object ManifestUnavailable : RemoteDataFetchResult
    data class Failed(val error: Exception) : RemoteDataFetchResult
}

class LauncherUpgradeViewModel: ViewModel() {
    var operation by mutableStateOf<LauncherUpgradeOperation>(LauncherUpgradeOperation.None)
    private val checkMutex = Mutex()

    private fun isWithinRateLimit(time: Long, lastCheckTime: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        if (lastCheckTime > currentTime) return false
        return currentTime - lastCheckTime < time
    }

    private fun updateLastCheckTime() {
        AllSettings.lastUpgradeCheck.save(System.currentTimeMillis())
    }

    fun checkOnAppStart(onIsLatest: suspend () -> Unit = {}) {
        viewModelScope.launch {
            if (isWithinRateLimit(TimeUnit.HOURS.toMillis(1L), AllSettings.lastUpgradeCheck.getValue())) {
                Logger.info(TAG, "App start check: Within rate limit, skipping")
                return@launch
            }
            when (val result = fetchRemoteData()) {
                is RemoteDataFetchResult.Available -> checkForUpgrade(
                    data = result.data,
                    lastIgnored = AllSettings.lastIgnoredVersion.getValue(),
                    ignoreDismissedVersions = true,
                    onUpgrade = { data -> operation = LauncherUpgradeOperation.Upgrade(data) },
                    onIsLatest = onIsLatest
                )
                RemoteDataFetchResult.ManifestUnavailable -> Logger.info(TAG, "No Mirai update manifest is published yet.")
                is RemoteDataFetchResult.Failed -> Unit
            }
            updateLastCheckTime()
        }
    }

    suspend fun checkManually(
        onInProgress: suspend () -> Unit = {},
        onIsLatest: suspend () -> Unit = {},
        onManifestUnavailable: suspend () -> Unit = {},
        onRemoteFailure: suspend () -> Unit = {}
    ): Boolean {
        return checkMutex.withLock {
            if (isWithinRateLimit(TimeUnit.SECONDS.toMillis(5L), AllSettings.lastUpgradeCheck.getValue())) {
                throw TooFrequentOperationException()
            }
            onInProgress()
            val result = fetchRemoteData()
            updateLastCheckTime()
            when (result) {
                is RemoteDataFetchResult.Available -> {
                    checkForUpgrade(
                        data = result.data,
                        lastIgnored = AllSettings.lastIgnoredVersion.getValue(),
                        ignoreDismissedVersions = false,
                        onUpgrade = { data -> operation = LauncherUpgradeOperation.Upgrade(data) },
                        onIsLatest = onIsLatest
                    )
                    true
                }
                RemoteDataFetchResult.ManifestUnavailable -> {
                    // No published release yet == nothing newer than this build.
                    onIsLatest()
                    onManifestUnavailable()
                    false
                }
                is RemoteDataFetchResult.Failed -> {
                    onRemoteFailure()
                    false
                }
            }
        }
    }

    private suspend fun fetchRemoteData(): RemoteDataFetchResult = withContext(Dispatchers.IO) {
        when (val result = loadUpgradeManifest()) {
            is UpgradeManifestResult.Available -> RemoteDataFetchResult.Available(result.data)
            UpgradeManifestResult.Unavailable -> RemoteDataFetchResult.ManifestUnavailable
            is UpgradeManifestResult.Failed -> RemoteDataFetchResult.Failed(result.error)
        }
    }

    private suspend fun checkForUpgrade(
        data: RemoteData,
        lastIgnored: Int?,
        ignoreDismissedVersions: Boolean,
        onUpgrade: suspend (RemoteData) -> Unit,
        onIsLatest: suspend () -> Unit = {}
    ) {
        val currentVersionCode = BuildConfig.VERSION_CODE
        if (currentVersionCode < data.code) {
            when {
                ignoreDismissedVersions && lastIgnored == data.code -> {
                    Logger.info(TAG, "Launcher update detected but ignored by user")
                }
                else -> {
                    Logger.info(TAG, "Launcher update detected: $currentVersionCode -> ${data.code}")
                    onUpgrade(data)
                }
            }
        } else {
            Logger.info(TAG, "Launcher is running the latest version: $currentVersionCode")
            onIsLatest()
        }
    }
}

@Composable
fun LauncherUpgradeOperation(
    operation: LauncherUpgradeOperation,
    onChanged: (LauncherUpgradeOperation) -> Unit,
    onIgnoredClick: (code: Int) -> Unit,
    onLinkClick: (String) -> Unit
) {
    when (operation) {
        is LauncherUpgradeOperation.None -> {}
        is LauncherUpgradeOperation.Upgrade -> {
            UpgradeDialog(
                data = operation.data,
                onDismissRequest = { onChanged(LauncherUpgradeOperation.None) },
                onFilesClick = { onChanged(LauncherUpgradeOperation.SelectApk(operation.data)) },
                onIgnored = { onIgnoredClick(operation.data.code) },
                onLinkClick = onLinkClick,
                onCloudDriveClick = { cloudDrive -> onChanged(LauncherUpgradeOperation.OpenCloudDrive(cloudDrive)) }
            )
        }
        is LauncherUpgradeOperation.SelectApk -> {
            UpgradeFilesDialog(
                data = operation.data,
                onDismissRequest = { onChanged(LauncherUpgradeOperation.None) },
                onFileSelected = { file ->
                    onLinkClick(file.uri)
                    onChanged(LauncherUpgradeOperation.None)
                }
            )
        }
        is LauncherUpgradeOperation.OpenCloudDrive -> {
            val current by remember(operation) { mutableStateOf<RemoteData.CloudDrive.Link?>(null) }
            SimpleListDialog(
                title = stringResource(R.string.upgrade_cloud_drive),
                items = operation.cloudDrive.links,
                onItemSelected = { link -> onLinkClick(link.link) },
                onDismissRequest = { onChanged(LauncherUpgradeOperation.None) },
                current = current,
                itemLayout = { item, isCurrent, onClick ->
                    CloudDriveLayout(link = item, selected = isCurrent, onClick = onClick)
                },
                showConfirm = true,
                confirmText = { MarqueeText(text = stringResource(R.string.generic_confirm)) }
            )
        }
    }
}

@Composable
private fun CloudDriveLayout(
    link: RemoteData.CloudDrive.Link,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier.clip(shape = MaterialTheme.shapes.large).clickable(enabled = enabled, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick, enabled = enabled)
        Column(
            modifier = Modifier.alpha(if (enabled) 1.0f else DisabledAlpha),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            MarqueeText(modifier = Modifier.fillMaxWidth(), text = link.name, style = MaterialTheme.typography.labelMedium)
            MarqueeText(modifier = Modifier.fillMaxWidth().alpha(0.7f), text = link.link, style = MaterialTheme.typography.labelSmall)
        }
    }
}
