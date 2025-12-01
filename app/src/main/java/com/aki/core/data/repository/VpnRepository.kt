package com.aki.core.data.repository

import com.aki.core.base.BaseRepository
import com.aki.core.domain.model.VpnConfig
import com.aki.core.domain.model.VpnState
import kotlinx.coroutines.flow.Flow

/**
 * Repository để quản lý trạng thái và hoạt động của VPN.
 */
interface VpnRepository : BaseRepository {

    /**
     * Luồng (Flow) chứa trạng thái hiện tại của VPN.
     */
    val vpnState: Flow<VpnState>

    /**
     * Bắt đầu phiên VPN với cấu hình được cung cấp.
     * @param config Cấu hình kết nối.
     */
    suspend fun startVpn(config: VpnConfig)

    /**
     * Dừng phiên VPN hiện tại.
     */
    suspend fun stopVpn()
}
