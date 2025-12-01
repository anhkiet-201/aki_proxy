package com.aki.core.domain.usecase

import com.aki.core.base.BaseUseCase
import com.aki.core.data.storage.VpnStorage
import com.aki.core.domain.model.VpnConfig
import javax.inject.Inject

class RemoveConfigUseCase @Inject constructor(
    private val vpnStorage: VpnStorage
) : BaseUseCase<VpnConfig, Unit> {
    override suspend fun execute(input: VpnConfig) {
        vpnStorage.removeConfig(input)
    }
}
