package com.aki.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents the possible states of the VPN.
 */

sealed class VpnState {
    /**
     * The VPN is running and the connection is established.
     */
    object Connected : VpnState()

    /**
     * The VPN is starting up and establishing a connection.
     */
    object Connecting: VpnState()

    /**
     * The VPN is stopped or has not been started.
     */
    object Disconnected : VpnState()

    /**
     * An error occurred during VPN operation.
     * @param message The error message.
     */
    data class Error(val message: String) : VpnState()
}
