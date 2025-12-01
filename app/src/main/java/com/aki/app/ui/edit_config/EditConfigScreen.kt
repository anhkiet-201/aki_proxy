package com.aki.app.ui.edit_config

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aki.akiproxy.R
import com.aki.app.ui.components.CommonTextField
import com.aki.app.ui.components.CommonTopAppBar
import com.aki.app.viewmodel.EditConfigViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditConfigScreen(
    viewModel: EditConfigViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onSave: () -> Unit
) {
    val host by viewModel.host.collectAsState()
    val port by viewModel.port.collectAsState()
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = stringResource(id = R.string.edit_config_title),
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigateBack = onNavigateBack,
                actions = {
                    TextButton(onClick = {
                        viewModel.saveConfig()
                        onSave()
                    }) {
                        Text(stringResource(id = R.string.edit_config_save))
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    viewModel.saveConfig()
                    onSave()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.edit_config_save_button), modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it).padding(horizontal = 16.dp)) {
            item {
                Section(title = stringResource(R.string.edit_config_section_connection)) {
                    CommonTextField(
                        value = host,
                        onValueChange = viewModel::onHostChange,
                        label = stringResource(R.string.edit_config_host),
                        placeholder = stringResource(R.string.edit_config_host_placeholder)
                    )
                    CommonTextField(
                        value = port,
                        onValueChange = viewModel::onPortChange,
                        label = stringResource(R.string.edit_config_port),
                        placeholder = stringResource(R.string.edit_config_port_placeholder),
                        keyboardType = KeyboardType.Number
                    )
                }
            }

            item {
                Section(title = "Loại kết nối") {
                    var selectedType by remember { mutableStateOf("SOCKS") }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(4.dp)
                    ) {
                        Button(
                            onClick = { selectedType = "SOCKS" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = if (selectedType == "SOCKS") ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("SOCKS", color = if (selectedType == "SOCKS") Color.Black else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = { selectedType = "HTTP" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = if (selectedType == "HTTP") ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("HTTP", color = if (selectedType == "HTTP") Color.Black else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                Section(title = stringResource(R.string.edit_config_section_auth)) {
                    CommonTextField(
                        value = username,
                        onValueChange = viewModel::onUsernameChange,
                        label = stringResource(R.string.edit_config_username),
                        placeholder = stringResource(R.string.edit_config_username_placeholder)
                    )
                    CommonTextField(
                        value = password,
                        onValueChange = viewModel::onPasswordChange,
                        label = stringResource(R.string.edit_config_password),
                        placeholder = stringResource(R.string.edit_config_password_placeholder),
                        keyboardType = KeyboardType.Password,
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content()
        }
    }
}
