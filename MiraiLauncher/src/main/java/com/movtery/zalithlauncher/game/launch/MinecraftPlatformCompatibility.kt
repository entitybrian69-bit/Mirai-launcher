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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.game.launch

/**
 * Minecraft 1.20.5 and newer require a 64-bit operating system.
 * Unknown/non-release version identifiers are left unclassified rather than blocked.
 */
internal object MinecraftPlatformCompatibility {
    private const val FIRST_64_BIT_ONLY_PATCH = 5
    private val releasePrefix = Regex("^\\s*(\\d+)\\.(\\d+)(?:\\.(\\d+))?")

    fun requires64BitOperatingSystem(minecraftVersion: String): Boolean {
        val match = releasePrefix.find(minecraftVersion) ?: return false
        val major = match.groupValues[1].toIntOrNull() ?: return false
        val minor = match.groupValues[2].toIntOrNull() ?: return false
        val patch = match.groupValues[3].toIntOrNull() ?: 0

        // Minecraft has also introduced year-style release numbers (for example, 26.1).
        if (major != 1) return major > 1
        if (minor != 20) return minor > 20
        return patch >= FIRST_64_BIT_ONLY_PATCH
    }

    fun isSupportedOnPlatform(
        minecraftVersion: String,
        supports64BitOperatingSystem: Boolean,
        is64BitProcess: Boolean
    ): Boolean {
        if (!requires64BitOperatingSystem(minecraftVersion)) return true
        return supports64BitOperatingSystem && is64BitProcess
    }
}
