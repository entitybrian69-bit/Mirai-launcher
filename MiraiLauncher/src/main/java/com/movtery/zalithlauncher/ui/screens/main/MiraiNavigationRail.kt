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

package com.movtery.zalithlauncher.ui.screens.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.ui.theme.AerixMetrics
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.ui.components.liquidGlass
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.account.Account
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.ui.screens.content.elements.PlayerFace
import com.movtery.zalithlauncher.ui.theme.MiraiThemeManager

/** Primary destinations kept visible while nested launcher screens are open. */
enum class LauncherSection {
    HOME,
    LIBRARY,
    DISCOVER,
    WALLPAPERS,
    MULTIPLAYER,
    SETTINGS,
    ACCOUNTS
}

private val AerixRailDivider = AerixSurface.borderSoft

@Composable
fun MiraiNavigationRail(
    selectedSection: LauncherSection?,
    onNavigate: (LauncherSection) -> Unit,
    onCreateInstance: () -> Unit,
    onAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val activeAccent = MiraiThemeManager.currentAccent()

    Row(modifier = modifier.fillMaxHeight()) {
        Surface(
            modifier = Modifier
                .width(AerixMetrics.navigationRailWidth)
                .fillMaxHeight()
                .padding(horizontal = AerixSpacing.xs, vertical = AerixSpacing.smPlus)
                .liquidGlass(
                    shape = RoundedCornerShape(AerixRadii.pill),
                    tint = AerixSurface.glassTint,
                    strength = 1f,
                    elevation = AerixMetrics.glassRailElevation
                ),
            shape = RoundedCornerShape(AerixRadii.pill),
            color = Color.Transparent,
            contentColor = Color.White,
            border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(scrollState)
                    .padding(vertical = AerixSpacing.smPlus),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
            ) {
                // Aerix 'M' Logo Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(AerixRadii.panelSmall))
                        .background(activeAccent.copy(alpha = 0.18f))
                        .liquidGlass(
                            shape = RoundedCornerShape(AerixRadii.panelSmall),
                            tint = activeAccent,
                            strength = 1f,
                            elevation = AerixMetrics.glassSelectedElevation
                        )
                        .border(
                            AerixSpacing.hairline,
                            AerixSurface.borderHighlight.copy(alpha = 0.72f),
                            RoundedCornerShape(AerixRadii.panelSmall)
                        )
                        .clickable(role = Role.Button) { onNavigate(LauncherSection.HOME) }
                        .semantics { contentDescription = "Aerix Home" },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mirai_mark),
                        contentDescription = "Aerix",
                        tint = activeAccent,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(Modifier.height(AerixSpacing.xxs))

                RailIconItem(
                    iconRes = R.drawable.ic_home_filled,
                    label = "Home",
                    selected = selectedSection == LauncherSection.HOME,
                    accentColor = activeAccent,
                    onClick = { onNavigate(LauncherSection.HOME) }
                )

                RailIconItem(
                    iconRes = R.drawable.ic_add,
                    label = "New Instance",
                    selected = false,
                    accentColor = activeAccent,
                    emphasized = true,
                    onClick = onCreateInstance
                )

                RailIconItem(
                    iconRes = R.drawable.ic_dashboard_filled,
                    label = "Library",
                    selected = selectedSection == LauncherSection.LIBRARY,
                    accentColor = activeAccent,
                    onClick = { onNavigate(LauncherSection.LIBRARY) }
                )

                RailIconItem(
                    iconRes = R.drawable.ic_public,
                    label = "Discover",
                    selected = selectedSection == LauncherSection.DISCOVER,
                    accentColor = activeAccent,
                    onClick = { onNavigate(LauncherSection.DISCOVER) }
                )

                RailIconItem(
                    iconRes = R.drawable.ic_group_filled,
                    label = "Multiplayer",
                    selected = selectedSection == LauncherSection.MULTIPLAYER,
                    accentColor = activeAccent,
                    onClick = { onNavigate(LauncherSection.MULTIPLAYER) }
                )

                RailIconItem(
                    iconRes = R.drawable.ic_format_paint_outlined,
                    label = "Wallpapers",
                    selected = selectedSection == LauncherSection.WALLPAPERS,
                    accentColor = activeAccent,
                    onClick = { onNavigate(LauncherSection.WALLPAPERS) }
                )

                HorizontalDivider(
                    modifier = Modifier
                        .width(28.dp)
                        .padding(vertical = AerixSpacing.xxs),
                    color = AerixRailDivider
                )

                RailIconItem(
                    iconRes = R.drawable.ic_settings_filled,
                    label = "Settings",
                    selected = selectedSection == LauncherSection.SETTINGS,
                    accentColor = activeAccent,
                    onClick = { onNavigate(LauncherSection.SETTINGS) }
                )

                AccountAvatarRailButton(
                    account = account,
                    selected = selectedSection == LauncherSection.ACCOUNTS,
                    onClick = onAccountClick
                )
            }
        }
    }
}

@Composable
private fun RailIconItem(
    iconRes: Int,
    label: String,
    selected: Boolean,
    accentColor: Color,
    emphasized: Boolean = false,
    onClick: () -> Unit
) {
    val active = selected || emphasized
    val containerColor by animateColorAsState(
        targetValue = when {
            emphasized -> accentColor.copy(alpha = 0.78f)
            selected -> accentColor.copy(alpha = 0.18f)
            else -> Color.Transparent
        },
        animationSpec = tween(150),
        label = "railContainerColor"
    )
    val iconTint by animateColorAsState(
        targetValue = when {
            emphasized -> AerixSurface.onAccent
            selected -> accentColor
            else -> AerixSurface.textSecondary
        },
        animationSpec = tween(150),
        label = "railIconTint"
    )
    val shape = RoundedCornerShape(AerixRadii.panelSmall)

    Box(
        modifier = Modifier
            .size(width = 46.dp, height = 42.dp)
            .clip(shape)
            .background(containerColor)
            .then(
                if (active) {
                    Modifier.liquidGlass(
                        shape = shape,
                        tint = accentColor,
                        strength = if (emphasized) 1f else 0.94f,
                        elevation = AerixMetrics.glassSubtleElevation
                    )
                } else Modifier
            )
            .then(
                if (selected || emphasized) {
                    Modifier.border(
                        BorderStroke(
                            width = if (selected) 1.5.dp else AerixSpacing.hairline,
                            color = if (emphasized) Color.White.copy(alpha = 0.68f) else accentColor.copy(alpha = 0.72f)
                        ),
                        shape
                    )
                } else Modifier
            )
            .clickable(role = Role.Tab, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(21.dp)
        )
    }
}

@Composable
private fun AccountAvatarRailButton(
    account: Account?,
    selected: Boolean,
    onClick: () -> Unit
) {
    val activeAccent = MiraiThemeManager.currentAccent()
    val ringColor by animateColorAsState(
        targetValue = if (selected) activeAccent else AerixSurface.borderSoft,
        animationSpec = tween(150),
        label = "accountAvatarRing"
    )

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(AerixSurface.panel)
            .liquidGlass(
                shape = CircleShape,
                tint = activeAccent,
                strength = if (selected) 0.82f else 0.34f,
                elevation = AerixMetrics.glassSelectedElevation
            )
            .border(
                width = if (selected) 2.dp else AerixSpacing.hairline,
                color = ringColor,
                shape = CircleShape
            )
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = account?.username ?: "Accounts" },
        contentAlignment = Alignment.Center
    ) {
        PlayerFace(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape),
            account = account
        )
    }
}
