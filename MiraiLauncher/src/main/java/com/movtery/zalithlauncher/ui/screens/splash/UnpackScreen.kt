package com.movtery.zalithlauncher.ui.screens.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.components.InstallableItem
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.BackgroundCard
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.viewmodel.SplashBackStackViewModel

@Composable
fun UnpackScreen(
    items: List<InstallableItem>,
    screenViewModel: SplashBackStackViewModel,
    onAgreeClick: () -> Unit = {}
) {
    BaseScreen(screenKey = NormalNavKey.UnpackDeps, currentKey = screenViewModel.splashScreen.currentKey) {
        Row(
            modifier = Modifier.fillMaxSize().padding(start = AerixSpacing.lg, end = AerixSpacing.lg, bottom = AerixSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.lg)
        ) {
            UnpackTaskList(items = items, modifier = Modifier.weight(7f).fillMaxHeight())
            ActionMenu(modifier = Modifier.weight(3f).fillMaxHeight(), onAgreeClick = onAgreeClick)
        }
    }
}

@Composable
private fun ActionMenu(modifier: Modifier = Modifier, onAgreeClick: () -> Unit = {}) {
    var installing by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth().weight(1f),
            text = stringResource(if (installing) R.string.splash_screen_installing else R.string.splash_screen_unpack_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
        Button(
            enabled = !installing,
            onClick = { installing = true; onAgreeClick() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AerixRadii.hero),
            colors = ButtonDefaults.buttonColors(
                containerColor = AerixSurface.accent.copy(alpha = 0.6f),
                contentColor = AerixSurface.onAccent,
                disabledContainerColor = AerixSurface.accent.copy(alpha = 0.33f),
                disabledContentColor = AerixSurface.onAccent
            )
        ) {
            Text(stringResource(R.string.splash_screen_agree))
        }
    }
}

@Composable
private fun UnpackTaskList(items: List<InstallableItem>, modifier: Modifier = Modifier) {
    BackgroundCard(modifier = modifier, influencedByBackground = false, shape = MaterialTheme.shapes.extraLarge) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = AerixSpacing.md, vertical = AerixSpacing.smCompact)
        ) {
            items(items, key = { it.name }) { item ->
                TaskItem(item = item, modifier = Modifier.padding(vertical = AerixSpacing.smCompact))
            }
        }
    }
}

@Composable
private fun TaskItem(item: InstallableItem, modifier: Modifier = Modifier) {
    val state by item.state.collectAsStateWithLifecycle()
    Row(modifier = modifier.fillMaxWidth().padding(horizontal = AerixSpacing.sm, vertical = AerixSpacing.xs), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, style = MaterialTheme.typography.labelMedium, color = Color.White)
            item.summary?.let { Text(text = it, style = MaterialTheme.typography.labelSmall, color = AerixSurface.textSecondary) }
        }
        val iconModifier = Modifier.padding(horizontal = AerixSpacing.md).size(18.dp)
        when (state) {
            InstallableItem.State.RUNNING -> CircularProgressIndicator(modifier = iconModifier, strokeWidth = 2.dp, color = AerixSurface.success)
            InstallableItem.State.FINISHED -> Icon(painterResource(R.drawable.ic_check), contentDescription = null, modifier = iconModifier, tint = AerixSurface.success)
            InstallableItem.State.PENDING -> Icon(painterResource(R.drawable.ic_update), contentDescription = null, modifier = iconModifier, tint = Color.White)
            else -> Icon(painterResource(R.drawable.ic_folder_zip_outlined), contentDescription = null, modifier = iconModifier, tint = Color.White)
        }
    }
}
