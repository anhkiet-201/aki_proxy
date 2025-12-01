package com.aki.core.domain.usecase

import android.util.Log
import com.aki.core.base.BaseUseCase
import com.aki.core.data.storage.VpnStorage
import com.aki.core.domain.model.SelectedVpnConfig
import javax.inject.Inject

class SaveSelectedConfigUseCase @Inject constructor(
    private val vpnStorage: VpnStorage
) : BaseUseCase<SelectedVpnConfig, Unit> {
    override suspend fun execute(input: SelectedVpnConfig) {
        vpnStorage.saveSelectedConfig(input)
    }
}
