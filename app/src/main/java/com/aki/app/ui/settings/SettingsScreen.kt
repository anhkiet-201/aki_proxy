package com.aki.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aki.akiproxy.R
import com.aki.app.ui.components.CommonTopAppBar
import com.aki.app.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = stringResource(id = R.string.settings_title),
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(modifier = Modifier.padding(it).fillMaxSize().padding(16.dp)) {
            item {
                SettingsGroup {
                    val autoConnect by viewModel.autoConnect.collectAsState()
                    val notifications by viewModel.notificationsEnabled.collectAsState()

                    SettingsItem(
                        icon = Icons.Default.PowerSettingsNew,
                        iconDescription = stringResource(R.string.settings_auto_connect),
                        title = stringResource(R.string.settings_auto_connect),
                        onClick = { viewModel.onAutoConnectChanged(!autoConnect) }
                    ) {
                        Switch(checked = autoConnect, onCheckedChange = viewModel::onAutoConnectChanged)
                    }
                    SettingsDivider()
                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        iconDescription = stringResource(R.string.settings_notifications),
                        title = stringResource(R.string.settings_notifications),
                        onClick = { viewModel.onNotificationsChanged(!notifications) }
                    ) {
                        Switch(checked = notifications, onCheckedChange = viewModel::onNotificationsChanged)
                    }
                }
            }

            item {
                SettingsGroup(modifier = Modifier.padding(top = 16.dp)) {
                    SettingsItem(
                        icon = Icons.Default.Language,
                        iconDescription = stringResource(R.string.settings_language),
                        title = stringResource(R.string.settings_language),
                        onClick = { /* TODO */ }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.settings_language_value), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForwardIos, 
                                contentDescription = null, 
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    SettingsDivider()
                    SettingsItem(
                        icon = Icons.Default.Info,
                        iconDescription = stringResource(R.string.settings_about_us),
                        title = stringResource(R.string.settings_about_us),
                        onClick = { /* TODO */ }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForwardIos, 
                            contentDescription = null, 
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    SettingsDivider()
                    SettingsItem(
                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                        iconDescription = stringResource(R.string.settings_help),
                        title = stringResource(R.string.settings_help),
                        onClick = { /* TODO */ }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForwardIos, 
                            contentDescription = null, 
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsGroup(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        content()
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    iconDescription: String,
    title: String,
    onClick: () -> Unit,
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(8.dp)
            )
            Text(text = title, fontWeight = FontWeight.SemiBold)
        }
        trailingContent()
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(modifier = Modifier.padding(start = 68.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.background)
}
