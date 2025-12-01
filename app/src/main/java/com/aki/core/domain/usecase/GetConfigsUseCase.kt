package com.aki.core.domain.usecase

import com.aki.core.base.BaseUseCase
import com.aki.core.data.storage.VpnStorage
import com.aki.core.domain.model.VpnConfig
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConfigsUseCase @Inject constructor(
    private val vpnStorage: VpnStorage
) : BaseUseCase<Unit, Flow<List<VpnConfig>>> {
    override suspend fun execute(input: Unit): Flow<List<VpnConfig>> {
        return vpnStorage.configs
    }
}
