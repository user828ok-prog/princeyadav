WAKE UP PRO — NATIVE ANDROID PROJECT SOURCE

This is an Android Studio project source conversion of the uploaded PWA.
It is NOT a compiled APK yet.

BUILD:
1. Install Android Studio on a computer.
2. Extract this ZIP.
3. Open the extracted WakeUpPro folder in Android Studio.
4. Allow Gradle sync to finish (internet required for Android Gradle Plugin).
5. Connect an Android phone with USB debugging enabled, or use Build > Build Bundle(s) / APK(s) > Build APK(s).
6. Install app/build/outputs/apk/debug/app-debug.apk on your phone.

IMPORTANT:
- On Android 12+, allow exact alarms in Android Settings if offered.
- On Android 13+, allow notifications.
- Disable battery optimization for Wake Up Pro if your device offers that setting.
- Test with a near-future alarm while the screen is locked.
- Android manufacturers may still restrict background execution.
- The app uses Android TextToSpeech and requests Hindi (India); available voice quality and gender depend on installed TTS voices.
- The study planner remains in the WebView. This first native bridge schedules the main recurring alarm.
- This source is a starter project and needs testing on your specific Android version/device. No background alarm can be guaranteed on every Android device.
