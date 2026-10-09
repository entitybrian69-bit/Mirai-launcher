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

package com.movtery.zalithlauncher.utils.platform

import android.app.ActivityManager
import android.content.Context
import androidx.annotation.WorkerThread
import com.movtery.zalithlauncher.utils.device.Architecture
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.round

private const val BYTES_PER_MB = 1024L * 1024
private const val ADDRESS_SPACE_HEAP_RESERVE_MB = 128
private const val MINIMUM_JVM_HEAP_MB = 256
private const val LOW_MEMORY_RESERVE_MB = 800
private const val HIGH_MEMORY_RESERVE_MB = 1024
private const val MAX_32_BIT_JVM_HEAP_MB = 1536
private val MAPS_RANGE_REGEX = Regex("^([0-9a-fA-F]+)-([0-9a-fA-F]+)(?:\\s|$)")

private inline val Context.activityManager: ActivityManager
    get() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

private fun getMemoryInfo(context: Context): ActivityManager.MemoryInfo {
    return ActivityManager.MemoryInfo().apply {
        context.activityManager.getMemoryInfo(this)
    }
}

/**
 * 获取系统总内存（单位：字节）
 */
@WorkerThread
fun getTotalMemory(context: Context) = getMemoryInfo(context).totalMem

/**
 * 获取已使用内存（单位：字节）
 */
@WorkerThread
fun getUsedMemory(context: Context): Long {
    val info = getMemoryInfo(context)
    return info.totalMem - info.availMem
}

/**
 * 获取当前可用内存（单位：字节）
 */
@WorkerThread
fun getFreeMemory(context: Context) = getMemoryInfo(context).availMem

/**
 * 为设置项获取最大可设置的内存值（为系统预留一些可用内存）
 */
@WorkerThread
fun getMaxMemoryForSettings(context: Context): Int = maxMemoryForSettings(
    deviceRamMb = getTotalMemory(context).bytesToMB(),
    is64BitProcess = Architecture.is64BitsProcess
)

/**
 * Cap the launch heap against the current 32-bit process's largest free virtual-address range.
 * The bundled JVM runs in this process, so a fragmented address space can be a tighter limit
 * than the amount of physical RAM or the nominal 32-bit address-space ceiling.
 */
@WorkerThread
fun getMaxMemoryForLaunch(context: Context): Int {
    val settingsLimitMb = getMaxMemoryForSettings(context)
    if (Architecture.is64BitsProcess) return settingsLimitMb

    val maps = runCatching { File("/proc/self/maps").readText() }.getOrNull()
        ?: return settingsLimitMb
    val addressSpaceEndExclusive = Architecture.ADDRESS_SPACE_LIMIT_32_BIT + 1L
    val largestHoleMb = largestAddressSpaceHoleBytes(maps, addressSpaceEndExclusive)
        ?.div(BYTES_PER_MB)
        ?.toInt()

    return maxMemoryForLaunch(
        settingsLimitMb = settingsLimitMb,
        is64BitProcess = false,
        largestAddressSpaceHoleMb = largestHoleMb
    )
}

/**
 * Keep the heap limit within the address space available to this launcher process.
 * A 32-bit APK can run on 64-bit-capable hardware, so device bitness alone is not enough.
 */
internal fun maxMemoryForSettings(deviceRamMb: Double, is64BitProcess: Boolean): Int {
    if (!is64BitProcess) {
        // A 32-bit process has a much smaller and fragmented virtual address space. Allow up to
        // 1536 MiB on high-memory devices, while leaving physical-RAM headroom on smaller phones;
        // getMaxMemoryForLaunch() applies a second cap based on the largest free /proc/maps range.
        val reserveMb = if (deviceRamMb < 3064) LOW_MEMORY_RESERVE_MB else HIGH_MEMORY_RESERVE_MB
        return min(
            MAX_32_BIT_JVM_HEAP_MB.toDouble(),
            (deviceRamMb - reserveMb).coerceAtLeast(MINIMUM_JVM_HEAP_MB.toDouble())
        ).toInt()
    }

    if (deviceRamMb < 2048) return min(1024.0, deviceRamMb).toInt()
    // To have a minimum for the device to breathe.
    val reserveMb = if (deviceRamMb < 3064) LOW_MEMORY_RESERVE_MB else HIGH_MEMORY_RESERVE_MB
    return (deviceRamMb - reserveMb).toInt()
}

/**
 * Account for the largest contiguous free mapping available to the embedded JVM on 32-bit
 * Android. Subtract a 128 MiB margin for non-heap structures, but keep the launcher's 256 MiB
 * minimum heap setting when the detected range is unusually small. If maps are unreadable, use
 * the existing physical/process RAM limit instead.
 */
internal fun maxMemoryForLaunch(
    settingsLimitMb: Int,
    is64BitProcess: Boolean,
    largestAddressSpaceHoleMb: Int?
): Int {
    if (is64BitProcess || largestAddressSpaceHoleMb == null || largestAddressSpaceHoleMb <= 0) {
        return settingsLimitMb
    }

    val heapLimitFromAddressSpace =
        (largestAddressSpaceHoleMb - ADDRESS_SPACE_HEAP_RESERVE_MB).coerceAtLeast(MINIMUM_JVM_HEAP_MB)
    return min(settingsLimitMb, heapLimitFromAddressSpace)
}

/** Finds the largest unmapped byte range below [addressSpaceEndExclusive] from /proc maps text. */
internal fun largestAddressSpaceHoleBytes(
    mapsContent: String,
    addressSpaceEndExclusive: Long
): Long? {
    if (addressSpaceEndExclusive <= 0) return null

    val mappings = mapsContent.lineSequence()
        .mapNotNull { line ->
            val match = MAPS_RANGE_REGEX.find(line.trimStart()) ?: return@mapNotNull null
            val start = match.groupValues[1].toLongOrNull(16) ?: return@mapNotNull null
            val end = match.groupValues[2].toLongOrNull(16) ?: return@mapNotNull null
            (start to end).takeIf { start >= 0 && end > start }
        }
        .sortedBy { it.first }
        .toList()
    if (mappings.isEmpty()) return null

    var cursor = 0L
    var largestHole = 0L
    for ((rawStart, rawEnd) in mappings) {
        val start = rawStart.coerceAtMost(addressSpaceEndExclusive)
        val end = rawEnd.coerceAtMost(addressSpaceEndExclusive)
        if (end <= cursor) continue

        if (start > cursor) {
            largestHole = maxOf(largestHole, start - cursor)
        }
        cursor = maxOf(cursor, end)
        if (cursor >= addressSpaceEndExclusive) break
    }
    if (cursor < addressSpaceEndExclusive) {
        largestHole = maxOf(largestHole, addressSpaceEndExclusive - cursor)
    }

    return largestHole.takeIf { it > 0 }
}

internal fun clampRamAllocation(requestedRamMb: Int, maxRamMb: Int): Int =
    min(requestedRamMb, maxRamMb)

/**
 * 转换为 MB 单位
 */
fun Long.bytesToMB(decimals: Int = 2, roundDown: Boolean = false): Double {
    val megaBytes = this.toDouble() / BYTES_PER_MB
    return if (decimals == 0) {
        if (roundDown) floor(megaBytes) else round(megaBytes)
    } else {
        val roundingMode = if (roundDown) RoundingMode.DOWN else RoundingMode.HALF_UP
        BigDecimal(megaBytes).setScale(decimals, roundingMode).toDouble()
    }
}