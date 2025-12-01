package com.aki.core.domain.usecase

import com.aki.core.base.BaseUseCase
import com.aki.core.data.repository.VpnRepository
import com.aki.core.domain.model.VpnConfig
import javax.inject.Inject

/**
 * Use case để bắt đầu phiên VPN.
 */
class StartVpnUseCase @Inject constructor(
    private val vpnRepository: VpnRepository
) : BaseUseCase<VpnConfig, Unit> {
    override suspend fun execute(input: VpnConfig) {
        vpnRepository.startVpn(input)
    }
}
