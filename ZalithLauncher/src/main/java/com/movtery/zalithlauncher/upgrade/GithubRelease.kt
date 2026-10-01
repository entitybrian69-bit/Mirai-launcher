/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 * Copyright (C) 2026 Mirai Launcher contributors.
 */

package com.movtery.zalithlauncher.upgrade

import com.movtery.zalithlauncher.BuildConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** GitHub Releases API payload for GET /repos/{owner}/{repo}/releases/latest */
@Serializable
data class GithubRelease(
    @SerialName("tag_name") val tagName: String,
    val name: String? = null,
    val body: String? = null,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("published_at") val publishedAt: String? = null,
    val prerelease: Boolean = false,
    val draft: Boolean = false,
    val assets: List<GithubReleaseAsset> = emptyList()
)

@Serializable
data class GithubReleaseAsset(
    val name: String,
    @SerialName("browser_download_url") val browserDownloadUrl: String,
    val size: Long = 0L
)

private val VERSION_TOKEN = Regex("\\d+")

/** Compare dotted versions. Returns >0 if [remote] is newer than [local]. */
fun compareVersionNames(remote: String, local: String): Int {
    val r = VERSION_TOKEN.findAll(remote.removePrefix("v").removePrefix("V")).map { it.value.toInt() }.toList()
    val l = VERSION_TOKEN.findAll(local.removePrefix("v").removePrefix("V")).map { it.value.toInt() }.toList()
    val size = maxOf(r.size, l.size)
    for (i in 0 until size) {
        val rv = r.getOrElse(i) { 0 }
        val lv = l.getOrElse(i) { 0 }
        if (rv != lv) return rv.compareTo(lv)
    }
    return 0
}

fun GithubRelease.isNewerThanInstalled(): Boolean {
    return compareVersionNames(tagName, BuildConfig.VERSION_NAME) > 0
}

fun GithubRelease.toRemoteData(): RemoteData {
    val files = assets
        .filter { it.name.endsWith(".apk", ignoreCase = true) }
        .map { asset ->
            RemoteData.RemoteFile(
                fileName = asset.name,
                uri = asset.browserDownloadUrl,
                arch = when {
                    asset.name.contains("arm64", true) -> RemoteData.RemoteFile.Arch.ARM64
                    asset.name.contains("armeabi", true) || asset.name.contains("armv7", true) -> RemoteData.RemoteFile.Arch.ARM
                    asset.name.contains("x86_64", true) -> RemoteData.RemoteFile.Arch.X86_64
                    asset.name.contains("x86", true) -> RemoteData.RemoteFile.Arch.X86
                    else -> RemoteData.RemoteFile.Arch.ALL
                },
                size = asset.size
            )
        }
        .ifEmpty {
            listOf(
                RemoteData.RemoteFile(
                    fileName = name ?: tagName,
                    uri = htmlUrl,
                    arch = RemoteData.RemoteFile.Arch.ALL,
                    size = 0L
                )
            )
        }

    val markdown = buildString {
        append("**")
        append(name ?: tagName)
        append("**\n\n")
        append(body?.takeIf { it.isNotBlank() } ?: "_No changelog provided._")
    }

    val remoteCode = if (isNewerThanInstalled()) {
        BuildConfig.VERSION_CODE + 1
    } else {
        BuildConfig.VERSION_CODE
    }

    return RemoteData(
        code = remoteCode,
        version = tagName.removePrefix("v").removePrefix("V"),
        createdAt = publishedAt ?: "",
        defaultCloudDrive = null,
        cloudDrives = emptyList(),
        files = files,
        defaultBody = RemoteData.RemoteBody(language = "en", markdown = markdown),
        bodies = emptyList()
    )
}
