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

package com.movtery.zalithlauncher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.setting.enums.isLauncherInDarkTheme
import com.movtery.zalithlauncher.ui.components.influencedByBackgroundColor

/** 应用整体背景的颜色 */
@Composable
fun backgroundColor(): Color = MaterialTheme.colorScheme.surfaceContainer
@Composable
fun onBackgroundColor(): Color = MaterialTheme.colorScheme.onSurfaceVariant

/**
 * 卡片背景颜色
 * [androidx.compose.material3.Card]
 * [com.movtery.zalithlauncher.ui.components.BackgroundCard]
 * [androidx.compose.ui.window.Dialog]
 * @param influencedByBackground 是否受背景内容影响，更改自身不透明度
 */
@Composable
fun cardColor(
    influencedByBackground: Boolean = true
): Color = influencedByBackgroundColor(
    color = MaterialTheme.colorScheme.surfaceContainerHigh,
    enabled = influencedByBackground
)
@Composable
fun onCardColor(): Color = MaterialTheme.colorScheme.onSurface
/**
 * 卡片顶部Title的背景颜色，半透明的surface
 */
@Composable
fun cardTitleColor(
    alpha: Float = 0.5f
): Color = influencedByBackgroundColor(
    color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = alpha),
    influencedAlpha = alpha
)

/**
 * 卡片上的Item的背景颜色
 * @param influencedByBackground 是否受背景内容影响，更改自身不透明度
 */
@Composable
fun itemColor(
    influencedByBackground: Boolean = true,
    isDark: Boolean = isLauncherInDarkTheme()
): Color {
    return influencedByBackgroundColor(
        color = if (isDark) {
            MaterialTheme.colorScheme.surfaceContainerHighest
        } else {
            MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        },
        enabled = influencedByBackground
    )
}
@Composable
fun onItemColor() = MaterialTheme.colorScheme.onSurface

/**
 * 玻璃卡片的描边颜色，深浅色主题都可见
 */
@Composable
fun glassBorderColor(alpha: Float = 0.10f): Color =
    MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)

/**
 * 玻璃卡片顶部的受光高光
 */
@Composable
fun glassSheenColor(alpha: Float = 0.045f): Color =
    Color.White.copy(alpha = alpha)