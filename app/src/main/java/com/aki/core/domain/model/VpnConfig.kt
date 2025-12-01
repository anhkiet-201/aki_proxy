package com.aki.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Đại diện cho cấu hình kết nối VPN.
 * @Parcelize để có thể truyền giữa các thành phần (ví dụ: qua Intent).
 */
@Parcelize
data class VpnConfig(
    val host: String,
    val port: Int,
    val user: String? = null,
    val pass: String? = null
) : Parcelable

/**
 * Đại diện cho cấu hình kết nối VPN.
 * @Parcelize để có thể truyền giữa các thành phần (ví dụ: qua Intent).
 */
@Parcelize
data class SelectedVpnConfig(
    val config: VpnConfig,
    val status: String
) : Parcelable
