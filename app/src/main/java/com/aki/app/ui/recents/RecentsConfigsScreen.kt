package com.aki.app.ui.recents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.SignalCellularAlt1Bar
import androidx.compose.material.icons.filled.SignalCellularAlt2Bar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aki.akiproxy.R
import com.aki.app.ui.LocalAppViewModel
import com.aki.app.ui.components.CommonTopAppBar
import com.aki.app.viewmodel.RecentsConfigsViewModel
import com.aki.app.viewmodel.ServerDisplayInfo
import com.aki.core.domain.model.SelectedVpnConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentsConfigsScreen(
    viewModel: RecentsConfigsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAddConfig: () -> Unit,
    onConfigSelected: () -> Unit
) {
    val appViewModel = LocalAppViewModel.current
    val servers by viewModel.filteredServers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedConfig by appViewModel.selectedConfig.collectAsState()

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = stringResource(id = R.string.recents_title),
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddConfig) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.recents_add_config_description))
            }
        }
    ) {
        Column(modifier = Modifier.padding(it).fillMaxSize()) {
            SearchBar(query = searchQuery, onQueryChange = viewModel::onSearchQueryChanged)
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(servers, key = { it.config.host + it.config.port }) { server ->
                    var isDismissed by remember { mutableStateOf(false) }
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                isDismissed = true
                                true
                            } else {
                                false
                            }
                        }
                    )

                    LaunchedEffect(isDismissed) {
                        if (isDismissed && server.config != selectedConfig?.config) {
                            viewModel.onServerSwiped(server.config)
                        }
                    }

                    AnimatedVisibility(
                        visible = !isDismissed,
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = { DeleteBackground() }
                        ) {
                            ServerItem(
                                server = server,
                                isSelected = server.config == selectedConfig?.config,
                                onSelected = {
                                    viewModel.onServerSelected(
                                        SelectedVpnConfig(
                                            server.config,
                                            "DISCONNECTED"
                                        )
                                    )
                                    onConfigSelected()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Red.copy(alpha = 0.8f))
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            Icons.Default.Delete,
            contentDescription = "Delete",
            tint = Color.White
        )
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(R.string.recents_search_placeholder)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
private fun ServerItem(server: ServerDisplayInfo, isSelected: Boolean, onSelected: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.5.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onSelected)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // TODO: Replace with Coil or other image loader
            Box(modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Gray))

            Column {
                Text(server.name, fontWeight = FontWeight.Bold)
                Text(server.location, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val (signalIcon, signalColor) = when (server.signalStrength) {
                3 -> Icons.Default.SignalCellularAlt to Color.Green
                2 -> Icons.Default.SignalCellularAlt2Bar to Color(0xFFFFA500)
                else -> Icons.Default.SignalCellularAlt1Bar to Color.Red
            }
            Icon(
                imageVector = signalIcon,
                contentDescription = stringResource(R.string.recents_signal_strength_description),
                tint = signalColor,
                modifier = Modifier.size(20.dp)
            )
            RadioButton(selected = isSelected, onClick = onSelected)
        }
    }
}
