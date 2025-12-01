package com.aki.core.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.aki.core.domain.model.SelectedVpnConfig
import com.aki.core.domain.model.VpnConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VpnStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("vpn_storage", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_CONFIGS = "vpn_configs"
        private const val KEY_SELECTED_CONFIG = "selected_vpn_config"
    }

    val configs: Flow<List<VpnConfig>> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_CONFIGS) {
                trySend(loadConfigsFromPrefs())
            }
        }
        trySend(loadConfigsFromPrefs())
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    val selectedConfig: Flow<SelectedVpnConfig?> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_SELECTED_CONFIG) {
                trySend(loadSelectedConfigFromPrefs())
            }
        }
        trySend(loadSelectedConfigFromPrefs())
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    private fun loadConfigsFromPrefs(): List<VpnConfig> {
        val json = prefs.getString(KEY_CONFIGS, null) ?: return emptyList()
        val type = object : TypeToken<List<VpnConfig>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveConfigsToPrefs(configs: List<VpnConfig>) {
        val json = gson.toJson(configs)
        prefs.edit().putString(KEY_CONFIGS, json).apply()
    }

    fun addConfig(config: VpnConfig) {
        val currentConfigs = loadConfigsFromPrefs().toMutableList()
        currentConfigs.add(0, config)
        saveConfigsToPrefs(currentConfigs)
    }

    fun removeConfig(config: VpnConfig) {
        val currentConfigs = loadConfigsFromPrefs().toMutableList()
        currentConfigs.removeAll { it == config }
        saveConfigsToPrefs(currentConfigs)
    }

    private fun loadSelectedConfigFromPrefs(): SelectedVpnConfig? {
        val json = prefs.getString(KEY_SELECTED_CONFIG, null) ?: return null
        return gson.fromJson(json, SelectedVpnConfig::class.java)
    }

    fun saveSelectedConfig(config: SelectedVpnConfig) {
        val json = gson.toJson(config)
        prefs.edit().putString(KEY_SELECTED_CONFIG, json).apply()
    }
}
