package com.aki.core.domain.usecase

import com.aki.core.base.BaseUseCase
import com.aki.core.data.repository.VpnRepository
import javax.inject.Inject

/**
 * Use case to stop the VPN session.
 */
class StopVpnUseCase @Inject constructor(
    private val vpnRepository: VpnRepository
) : BaseUseCase<Unit, Unit> {
    override suspend fun execute(input: Unit) {
        vpnRepository.stopVpn()
    }
}
