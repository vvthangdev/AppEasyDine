# EasyDine Android App

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.x-orange)
![Android](https://img.shields.io/badge/Android-API_28+-green)
![License](https://img.shields.io/badge/license-MIT-blue)

**EasyDine Android App** là ứng dụng di động cho dự án EasyDine, được xây dựng bằng **Kotlin** trên nền tảng Android. Hướng dẫn này giúp bạn cài đặt và chạy ứng dụng trên máy local (Windows, macOS, hoặc Linux) mà không cần Docker. Mã nguồn được giả định đã giải nén từ file zip.

## Mục Lục
- [Yêu Cầu Hệ Thống](#yêu-cầu-hệ-thống)
- [Cài Đặt](#cài-đặt)
- [Chạy Ứng Dụng](#chạy-ứng-dụng)
- [Kiểm Tra Kết Quả](#kiểm-tra-kết-quả)
- [Lưu Ý](#lưu-ý)
- [Khắc Phục Lỗi](#khắc-phục-lỗi)

## Yêu Cầu Hệ Thống
- **Hệ điều hành**: Windows, macOS, hoặc Linux
- **Android Studio**: Phiên bản mới nhất (khuyến nghị 2023.x hoặc cao hơn)
- **JDK**: Phiên bản 17
- **Git**: Tùy chọn, để kiểm tra mã nguồn
- **Thiết bị Android hoặc trình giả lập**: Android API Level 28+ (Android 9.0 Pie trở lên)
- **Trình chỉnh sửa văn bản**: Android Studio hoặc VS Code

## Cài Đặt

1. **Cài Đặt Android Studio**  
   Tải và cài đặt Android Studio từ [developer.android.com](https://developer.android.com/studio). Trong quá trình cài đặt, chọn:
   - Android SDK (API Level 28 hoặc cao hơn, ví dụ: Android 14.0 - API Level 34).
   - Android Virtual Device (AVD) để chạy trình giả lập.  
     Kiểm tra cài đặt:
   - Mở Android Studio, vào **File > Settings > Appearance & Behavior > System Settings > Android SDK**.
   - Cài đặt **SDK Platforms** (ví dụ: Android 14.0) và **SDK Tools** (Android SDK Build-Tools, Android Emulator).  
     Cấu hình trình giả lập:
   - Vào **Device Manager** (hoặc AVD Manager), tạo thiết bị giả lập (ví dụ: Pixel 6, API 34).
   - Hoặc kết nối thiết bị Android vật lý (bật **Developer Options** và **USB Debugging**).

2. **Chuẩn Bị Mã Nguồn**  
   Giải nén file zip chứa mã nguồn vào một thư mục, ví dụ: `C:\AppEasyDine` (Windows) hoặc `~/AppEasyDine` (macOS/Linux).  
   Mở Android Studio, chọn **Open an existing project**, và chọn thư mục `AppEasyDine`.  
   Chờ Android Studio đồng bộ hóa dự án (Gradle sync). Nếu gặp lỗi, đảm bảo:
   - File `build.gradle` (cả project và app module) không bị thiếu.
   - File `local.properties` có đường dẫn đến Android SDK (ví dụ: `sdk.dir=/Users/username/Library/Android/sdk`).

3. **Cài Đặt Phụ Thuộc**  
   Mở file `build.gradle` (module app) để kiểm tra các phụ thuộc (như AndroidX, Retrofit, v.v.).  
   Nhấn **Sync Project with Gradle Files** trong Android Studio để tải phụ thuộc.

4. **Thiết Lập Biến Môi Trường**  
   Ứng dụng sử dụng `productFlavors` để quản lý API URL. File `build.gradle` (app) đã được cấu hình như sau:
   ```gradle
   productFlavors {
       dev {
           dimension "env"
           buildConfigField "String", "API_URL", "\"https://hust-cv-student-20215643.id.vn/\""
       }
       staging {
           dimension "env"
           buildConfigField "String", "API_URL", "\"https://www.google.com\""
       }
       product {
           dimension "env"
           buildConfigField "String", "API_URL", "\"https://www.google.com\""
       }
   }
   - Đảm bảo backend `be-EasyDine` đang chạy trên `http://localhost:8080` (theo hướng dẫn trước).  
   - Nếu ứng dụng kết nối đến MinIO (`http://128.199.246.55:3001/upload`), kiểm tra kết nối mạng bằng `curl http://128.199.246.55:3001` hoặc trình duyệt.

## Chạy Ứng Dụng
Trong Android Studio:
- Chọn thiết bị giả lập hoặc thiết bị Android vật lý từ menu **Run > Select Device**.
- Nhấn **Run** (nút tam giác xanh) hoặc dùng lệnh:  
  ./gradlew installDebug  
  Ứng dụng sẽ được cài đặt và chạy trên thiết bị hoặc giả lập.

## Kiểm Tra Kết Quả
1. Mở ứng dụng trên thiết bị hoặc giả lập.
2. Kiểm tra **Logcat** trong Android Studio (**View > Tool Windows > Logcat**) để xem log lỗi.
3. Thử truy cập các tính năng như đăng nhập hoặc gọi API.

## Lưu Ý
- **Backend**: Đảm bảo backend `be-EasyDine` chạy trước trên `http://localhost:8080`.
- **Giả lập**: Khi dùng trình giả lập, thay `localhost` bằng `10.0.2.2` (ví dụ: `http://10.0.2.2:8080`) trong `BACKEND_URL`.
- **Phụ thuộc**: Nếu lỗi Gradle, chạy `./gradlew clean` và đồng bộ lại.
- **Firebase (nếu có)**: Tải file `google-services.json` từ [Firebase Console](https://console.firebase.google.com/) và đặt vào thư mục `app/`.

## Khắc Phục Lỗi
- **Lỗi kết nối backend**: Đảm bảo backend chạy trên `http://localhost:8080` (hoặc `10.0.2.2:8080` cho giả lập).
- **Lỗi MinIO**: Kiểm tra server `128.199.246.55:3001` hoạt động.
- **Lỗi Gradle**: Kiểm tra file `build.gradle` hoặc cung cấp log lỗi để hỗ trợ chi tiết.