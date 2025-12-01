package com.aki.app.ui.home

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aki.akiproxy.R
import com.aki.app.ui.LocalAppViewModel
import com.aki.core.domain.model.VpnState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToRecents: () -> Unit
) {
    val appViewModel = LocalAppViewModel.current
    val vpnState by appViewModel.vpnState.collectAsState()
    val selectedConfig by appViewModel.selectedConfig.collectAsState()

    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            appViewModel.proceedToConnect()
        }
    }

    LaunchedEffect(key1 = appViewModel) {
        appViewModel.permissionEvent.collectLatest { intent ->
            vpnPermissionLauncher.launch(intent)
        }
    }

    Scaffold(
        topBar = { TopAppBar(onSettingsClick = onNavigateToSettings) },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ConnectionStatus(vpnState)
            ConnectButton(vpnState, appViewModel::onConnectClick, appViewModel::onDisconnectClick)
            ServerSelection(
                serverName = selectedConfig?.config?.host ?: stringResource(id = R.string.default_server_name),
            ) {
                if (vpnState !is VpnState.Connecting && vpnState !is VpnState.Connected) {
                    onNavigateToRecents()
                }
            }
        }
    }
}

@Composable
private fun TopAppBar(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Outlined.Menu, contentDescription = stringResource(R.string.home_menu_content_description), modifier = Modifier.size(28.dp))
        }
        Text(text = stringResource(R.string.home_title), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        IconButton(onClick = onSettingsClick) {
            Icon(Icons.Outlined.Settings, contentDescription = stringResource(R.string.home_settings_content_description), modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun ConnectionStatus(state: VpnState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 40.dp)) {
        val iconColor: Color
        val statusText: String
        val descriptionText: String

        when (state) {
            is VpnState.Connected -> {
                iconColor = Color(0xFF2BEE8C)
                statusText = stringResource(R.string.status_connected)
                descriptionText = stringResource(R.string.description_connected)
            }
            is VpnState.Connecting -> {
                iconColor = Color.Gray
                statusText = stringResource(R.string.status_connecting)
                descriptionText = stringResource(R.string.description_connecting)
            }
            is VpnState.Disconnected -> {
                iconColor = Color.Red
                statusText = stringResource(R.string.status_disconnected)
                descriptionText = stringResource(R.string.description_disconnected)
            }
            is VpnState.Error -> {
                iconColor = Color.Red
                statusText = stringResource(R.string.status_error)
                descriptionText = state.message
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (state is VpnState.Connecting) {
                CircularProgressIndicator(modifier = Modifier.size(80.dp))
            } else {
                Icon(Icons.Outlined.Shield, contentDescription = stringResource(R.string.status_icon_content_description), modifier = Modifier.size(100.dp), tint = iconColor)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = statusText, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = descriptionText, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ConnectButton(state: VpnState, onConnect: () -> Unit, onDisconnect: () -> Unit) {
    val isConnected = state is VpnState.Connected
    val buttonColor = if (isConnected) Color.Red else Color(0xFF2BEE8C)
    val onClickAction = if (isConnected) onDisconnect else onConnect

    Box(
        modifier = Modifier
            .size(160.dp)
            .clip(CircleShape)
            .background(buttonColor)
            .clickable(onClick = onClickAction, enabled = state !is VpnState.Connecting),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Outlined.PowerSettingsNew, contentDescription = stringResource(R.string.connect_button_content_description), modifier = Modifier.size(72.dp), tint = Color.Black)
    }
}

@Composable
private fun ServerSelection(serverName: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(imageVector = Icons.Outlined.Shield, contentDescription = stringResource(R.string.country_flag_content_description), modifier = Modifier.size(40.dp)) // Placeholder
            Column {
                Text(stringResource(R.string.current_location), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(serverName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = stringResource(R.string.select_server_content_description))
    }
}
