package com.aki.proxy

import android.util.Log

/**
 * Interface JNI để giao tiếp với thư viện Native (C/C++ hoặc Go).
 * Bạn cần build thư viện tun2socks thành file .so (ví dụ: libtun2socks.so)
 * và nạp nó vào project.
 */
object Tun2SocksJni {
    // Tên thư viện phải khớp với tên file .so trong thư mục jniLibs
    init {
        try {
            System.loadLibrary("tun2socks")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("Tun2Socks", "Không tìm thấy thư viện native! Hãy đảm bảo bạn đã build file .so", e)
        }
    }

    /**
     * Hàm native để khởi chạy Tun2Socks.
     * @param vpnInterfaceFileDescriptor: File descriptor của TUN interface (lấy từ VpnService).
     * @param vpnMtu: Maximum Transmission Unit (thường là 1500).
     * @param socksServerAddress: Địa chỉ IP/Host của SOCKS5 server (VD: "1.2.3.4:1080").
     * @param username: (Optional) Username SOCKS5.
     * @param password: (Optional) Password SOCKS5.
     */
    external fun startTun2Socks(
        vpnInterfaceFileDescriptor: Int,
        vpnMtu: Int,
        socksServerAddress: String,
        username: String?,
        password: String?
    ): Int

    /**
     * Hàm native để dừng Tun2Socks
     */
    external fun stopTun2Socks()
}
