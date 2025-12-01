# AkiProxy - Ứng dụng SOCKS5 VPN cho Android

## Giới thiệu

AkiProxy là một ứng dụng Android mã nguồn mở, được xây dựng để hoạt động như một máy khách VPN, tạo ra một đường hầm mạng (network tunnel) thông qua một máy chủ proxy SOCKS5. Ứng dụng này sử dụng `VpnService` của Android và một engine `tun2socks` ở tầng native để định tuyến tất cả lưu lượng mạng của thiết bị qua proxy đã được chỉ định.

Dự án này được xây dựng với mục tiêu tuân thủ các phương pháp kiến trúc phần mềm hiện đại, bao gồm MVVM, Clean Architecture, và lập trình phản ứng (reactive programming).

## Tính năng chính

- **Kết nối SOCKS5:** Tạo một kết nối VPN toàn thiết bị thông qua một máy chủ SOCKS5.
- **Giao diện hiện đại:** Giao diện người dùng được xây dựng hoàn toàn bằng Jetpack Compose và Material 3, mang lại trải nghiệm mượt mà và hiện đại.
- **Quản lý cấu hình:** Người dùng có thể lưu, chọn, và xóa nhiều cấu hình máy chủ khác nhau.
- **Điều khiển từ xa:** Hỗ trợ điều khiển việc kết nối/ngắt kết nối từ bên ngoài thông qua các lệnh `adb`.
- **Hỗ trợ Always-on VPN:** Tương thích với tính năng "Always-on VPN" của Android.
- **Tự động kết nối khi khởi động:** Tự động kết nối lại vào máy chủ đã chọn cuối cùng sau khi thiết bị khởi động lại.

## Kiến trúc & Công nghệ sử dụng

Dự án tuân thủ chặt chẽ kiến trúc MVVM và các nguyên tắc của Clean Architecture, với sự phân tách rõ ràng giữa các lớp:

- **app (Lớp Presentation):** Chứa tất cả các thành phần liên quan đến giao diện người dùng và Android Framework.
    - **UI:** Jetpack Compose & Material 3.
    - **State Management:** `ViewModel` và `StateFlow`.
    - **Dependency Injection:** Hilt.
    - **Navigation:** Jetpack Navigation for Compose.
- **core (Lớp Domain & Data):** Chứa logic nghiệp vụ và logic truy cập dữ liệu, hoàn toàn độc lập với Android Framework.
    - **Asynchronous:** Kotlin Coroutines & Flow.
    - **Data Persistence:** SharedPreferences và Gson để lưu trữ cấu hình.
    - **Native Layer:** Giao tiếp với engine `tun2socks` qua JNI.

## Cách sử dụng

### Hướng dẫn sử dụng trên giao diện

1.  **Màn hình chính:**
    - Hiển thị trạng thái kết nối hiện tại.
    - Nhấn vào nút nguồn lớn để bắt đầu/dừng kết nối.
    - Nhấn vào khu vực "Vị trí hiện tại" để vào màn hình chọn máy chủ.

2.  **Màn hình chọn máy chủ:**
    - Hiển thị danh sách các cấu hình đã lưu.
    - Chọn một cấu hình từ danh sách để đặt nó làm máy chủ kết nối mặc định.
    - Vuốt một mục sang trái để xóa nó.
    - Nhấn vào nút `+` (Floating Action Button) để thêm một cấu hình mới.

3.  **Màn hình thêm/sửa cấu hình:**
    - Nhập các thông tin của máy chủ SOCKS5 (Host, Port, Username, Password) và lưu lại.

### Hướng dẫn sử dụng qua ADB

Bạn có thể điều khiển VPN từ dòng lệnh.

**Để kết nối:**
```sh
adb shell am broadcast -a com.aki.proxy.CONNECT \
--es HOST "your_proxy_host" \
--es PORT "1080" \
--es USER "your_username" \
--es PASS "your_password" \
-n "com.aki.akiproxy/com.aki.app.receiver.ProxyCommandReceiver"
```
*(Lưu ý: `--es USER` và `--es PASS` là tùy chọn.)*

**Để ngắt kết nối:**
```sh
adb shell am broadcast -a com.aki.proxy.DISCONNECT \
-n "com.aki.akiproxy/com.aki.app.receiver.ProxyCommandReceiver"
```

## Thiết lập dự án

1.  **Clone repository:**
    ```sh
    git clone <your-repo-url>
    ```
2.  **Thư viện Native (`tun2socks`):
    - Dự án này yêu cầu một thư viện native `libtun2socks.so`.
    - Bạn cần tự biên dịch engine `tun2socks` (từ các dự án như `xjasonlyu/tun2socks`) cho các kiến trúc Android (`armeabi-v7a`, `arm64-v8a`, `x86`, `x86_64`).
    - Đặt các tệp `.so` đã biên dịch vào thư mục `app/src/main/jniLibs/<architecture>/`.

3.  **Mở trong Android Studio và Build.**
