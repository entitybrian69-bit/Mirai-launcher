/*
 * Copyright (C) 2026 Mirai Launcher contributors.
 */

package com.movtery.zalithlauncher.upgrade

import com.movtery.zalithlauncher.path.GLOBAL_CLIENT
import com.movtery.zalithlauncher.path.URL_GITHUB_RELEASES_API
import com.movtery.zalithlauncher.path.URL_LATEST_RELEASE_INFO
import com.movtery.zalithlauncher.utils.logging.Logger
import com.movtery.zalithlauncher.utils.network.safeBodyAsJson
import com.movtery.zalithlauncher.utils.network.withRetry
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException

private const val TAG = "UpgradeManifest"

sealed interface UpgradeManifestResult {
    data class Available(val data: RemoteData) : UpgradeManifestResult
    data object Unavailable : UpgradeManifestResult
    data class Failed(val error: Exception) : UpgradeManifestResult
}

/**
 * Prefer GitHub Releases API, then the historical mirai-update.json asset.
 */
suspend fun loadUpgradeManifest(): UpgradeManifestResult {
    return try {
        val release = withRetry(logTag = "LauncherUpgrade", maxRetries = 2) {
            GLOBAL_CLIENT.get(URL_GITHUB_RELEASES_API) {
                header(HttpHeaders.Accept, "application/vnd.github+json")
            }.safeBodyAsJson<GithubRelease>()
        }
        Logger.info(TAG, "GitHub latest release: ${release.tagName}")
        UpgradeManifestResult.Available(release.toRemoteData())
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: ClientRequestException) {
        if (error.response.status == HttpStatusCode.NotFound) {
            loadLegacyJsonManifest()
        } else {
            Logger.warning(TAG, "GitHub Releases API failed", error)
            UpgradeManifestResult.Failed(error)
        }
    } catch (error: Exception) {
        Logger.warning(TAG, "GitHub Releases API failed", error)
        loadLegacyJsonManifest()
    }
}

private suspend fun loadLegacyJsonManifest(): UpgradeManifestResult {
    return try {
        val data = withRetry(logTag = "LauncherUpgrade", maxRetries = 2) {
            GLOBAL_CLIENT.get(URL_LATEST_RELEASE_INFO).safeBodyAsJson<RemoteData>()
        }
        UpgradeManifestResult.Available(data)
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: ClientRequestException) {
        if (error.response.status == HttpStatusCode.NotFound) {
            Logger.info(TAG, "No GitHub release and no mirai-update.json yet")
            UpgradeManifestResult.Unavailable
        } else {
            Logger.warning(TAG, "Legacy update JSON failed", error)
            UpgradeManifestResult.Failed(error)
        }
    } catch (error: Exception) {
        Logger.warning(TAG, "Legacy update JSON failed", error)
        UpgradeManifestResult.Failed(error)
    }
}
