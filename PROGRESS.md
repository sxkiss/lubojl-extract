# 鹿播精灵 (com.clement.livetools) 功能復刻進度

## 目標
復刻原版「鹿播精灵 V60.11.01」全部功能，基於 AndroidX 重寫。

---

## ✅ 已完成（構建通過 + 可運行）

### 基礎架構
- [x] Gradle 構建配置（AGP 8.5 + Gradle 8.7）
- [x] AndroidManifest.xml 完整聲明（22 個 Activity + 3 Service + 3 Provider）
- [x] AndroidX 遷移（useAndroidX + media/appcompat/material 依賴）
- [x] 簽名配置（CI 自動生成 keystore）
- [x] CI/CD（GitHub Actions 自動構建 + Release）

### UI 框架
- [x] SplashActivity — 啟動頁（3 秒自動跳轉）
- [x] LoginActivity — 登錄頁（直接進入主頁）
- [x] HomeActivity — 主頁（9 宀功能入口網格）

### 核心功能（有實際邏輯）
- [x] LiveDownloadActivity — 直播錄製/下載（URL 解析 + VideoView 預覽 + MediaRecorder 錄製 + HTTP 下載）
- [x] AudioRecordActivity — 音頻錄製（MediaRecorder AAC 128kbps + 計時器）
- [x] MusicService — 音樂播放服務（前台通知 + MediaPlayer）
- [x] VoiceLibraryPlayService — 語音庫播放服務
- [x] Socks5ProxyService — Socks5 代理服務（前台通知）
- [x] DownloadInstallerProvider — APK 下載安裝 Provider

---

## 🔧 待實現（Activity 存在但無功能邏輯）

### P0 核心功能
- [ ] RecordActivity — 屏幕錄製（MediaProjection + MediaCodec）
- [ ] RecordSetVideoActivity — 錄影設置（分辨率/幀率/碼率）
- [ ] RecordSetAudioActivity — 錄音設置（採樣率/聲道）
- [ ] RecordSetCameraActivity — 攝像頭設置（前置/後置/分辨率）
- [ ] AudioLibraryActivity — 音樂庫（掃描本地音樂 + 播放列表）
- [ ] AudioSelectActivity — 音頻選擇（文件瀏覽器）

### P1 AI 功能
- [ ] RecordSetAiManActivity — AI 虛擬主播設置（人像摳圖 + 虛擬人物選擇）
- [ ] AudioSynthesisActivity — TTS 語音合成（聲音選擇 + 文本輸入 + 播放）

### P2 工具功能
- [ ] CaptureActivity — 二維碼掃描（CameraX + ML Kit）
- [ ] PushInfoSetActivity — 推送/代理設置（Socks5 配置）
- [ ] BaseWebViewActivity — WebView 容器（加載網頁）
- [ ] SetWebViewBaseDialogActivity — WebView 設置對話框
- [ ] PermissionActivity — 權限請求頁面

### P3 圖片功能
- [ ] PictureSelectorActivity — 圖片選擇器
- [ ] PicturePreviewActivity — 圖片預覽
- [ ] PictureExternalPreviewActivity — 外部圖片預覽
- [ ] PictureVideoPlayActivity — 視頻播放
- [ ] PictureCustomCameraActivity — 自定義相機
- [ ] PictureSelectorCameraEmptyActivity — 相機空頁面
- [ ] PictureSelectorWeChatStyleActivity — 微信風格選擇器
- [ ] PictureSelectorPreviewWeChatStyleActivity — 微信風格預覽
- [ ] PictureMultiCuttingActivity — 多圖裁剪
- [ ] UCropActivity — 圖片裁剪

---

## 📦 原版依賴（需要集成的 native library）

| 模塊 | 原版方案 | 當前狀態 |
|------|----------|----------|
| 直播播放 | ijkplayer + FFmpeg | ❌ 未集成 |
| 視頻編碼 | FFmpeg | ❌ 未集成 |
| AI 推理 | ONNX Runtime + RVM | ❌ 未集成 |
| 語音合成 | Microsoft Speech SDK | ❌ 未集成 |
| 音頻編碼 | LAME MP3 | ❌ 未集成 |
| 攝像頭 | Camera2 NDK | ❌ 未集成 |
| 代理 | tun2socks | ❌ 未集成 |
| 圖片選擇 | PictureSelector | ✅ UI 存在 |
| 圖片裁剪 | UCrop | ✅ UI 存在 |

---

## 🏗️ 構建狀態

| 項目 | 狀態 |
|------|------|
| GitHub Actions 構建 | ✅ 通過 |
| 本地安裝 | ✅ 可安裝 |
| APP 啟動 | ✅ 不閃退 |
| 功能完整性 | 🔧 約 30% |
| UI 完整性 | 🔧 約 60% |

---

## 📋 下一步優先級

1. **補全所有 Activity 的 UI + 邏輯**（RecordActivity、AudioLibraryActivity 等）
2. **集成 native libraries**（從原版 APK 提取 .so 文件）
3. **實現屏幕錄製**（MediaProjection API）
4. **實現音樂播放列表**（MediaStore 掃描）
5. **實現 AI 虛擬主播**（ONNX Runtime + CameraX）
6. **實現 TTS**（Android 原生 TTS API）

---

*最後更新: 2026-08-08*
