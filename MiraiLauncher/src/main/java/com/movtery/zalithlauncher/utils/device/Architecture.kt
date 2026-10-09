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

package com.movtery.zalithlauncher.utils.device

import android.os.Build
import android.os.Process

/**
 * [from Architecture.java](https://github.com/PojavLauncherTeam/PojavLauncher/blob/v3_openjdk/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/Architecture.java)
 */
object Architecture {
    const val UNSUPPORTED_ARCH = -1
    const val ARCH_ARM64 = 0x1
    const val ARCH_ARM = 0x2
    const val ARCH_X86 = 0x4
    const val ARCH_X86_64 = 0x8

    const val ADDRESS_SPACE_LIMIT_32_BIT: Long = 0xbfffffffL
    const val ADDRESS_SPACE_LIMIT_64_BIT: Long = 0x7fffffffffL

    fun getAddressSpaceLimit() = if (is64BitsProcess) ADDRESS_SPACE_LIMIT_64_BIT else ADDRESS_SPACE_LIMIT_32_BIT

    /** True when the physical device supports a 64-bit ABI (not necessarily this app's ABI). */
    val is64BitsDevice: Boolean
        get() = Build.SUPPORTED_64_BIT_ABIS.isNotEmpty()

    val is32BitsDevice: Boolean
        get() = !is64BitsDevice

    /**
     * Native libraries and bundled runtimes must match the ABI of this process. A 32-bit build
     * can run on a 64-bit phone, so checking only the device's supported ABIs selects the wrong
     * LWJGL/JRE directories for that case.
     */
    val is64BitsProcess: Boolean
        get() = Process.is64Bit()

    /** Returns the current app-process ABI; the legacy method name is kept for call-site compatibility. */
    fun getDeviceArchitecture(): Int = selectProcessArchitecture(
        is64BitProcess = is64BitsProcess,
        supported32BitAbis = Build.SUPPORTED_32_BIT_ABIS,
        supported64BitAbis = Build.SUPPORTED_64_BIT_ABIS
    )

    internal fun selectProcessArchitecture(
        is64BitProcess: Boolean,
        supported32BitAbis: Array<String>,
        supported64BitAbis: Array<String>
    ): Int {
        val processAbis = if (is64BitProcess) supported64BitAbis else supported32BitAbis
        return processAbis.firstNotNullOfOrNull { abi ->
            archAsInt(abi).takeIf { it != UNSUPPORTED_ARCH }
        } ?: UNSUPPORTED_ARCH
    }

    fun isx86Device(): Boolean {
        val architecture = getDeviceArchitecture()
        return architecture == ARCH_X86 || architecture == ARCH_X86_64
    }

    fun archAsInt(arch: String?): Int {
        val normalizedArch = arch?.lowercase()?.trim()?.replace(" ", "") ?: return UNSUPPORTED_ARCH
        return when {
            normalizedArch.contains("arm64") || normalizedArch == "aarch64" -> ARCH_ARM64
            normalizedArch.contains("arm") || normalizedArch == "aarch32" -> ARCH_ARM
            normalizedArch.contains("x86_64") || normalizedArch.contains("amd64") -> ARCH_X86_64
            normalizedArch.contains("x86") || (normalizedArch.startsWith("i") && normalizedArch.endsWith("86")) -> ARCH_X86
            else -> UNSUPPORTED_ARCH
        }
    }

    fun archAsString(arch: Int): String = when (arch) {
        ARCH_ARM64 -> "arm64"
        ARCH_ARM -> "arm"
        ARCH_X86_64 -> "x86_64"
        ARCH_X86 -> "x86"
        else -> "UNSUPPORTED_ARCH"
    }

    /** Android ABI 目录名 */
    fun archAsStringAndroid(arch: Int): String = when (arch) {
        ARCH_ARM64 -> "arm64-v8a"
        ARCH_ARM -> "armeabi-v7a"
        ARCH_X86_64 -> "x86_64"
        ARCH_X86 -> "x86"
        else -> "UNSUPPORTED_ARCH"
    }
}
