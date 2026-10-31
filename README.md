# MCGG Auto Assistant — starter prototype

This is an Android project scaffold for the offline lineup manager.

## Current state
- Basic Android app shell and configuration UI.
- Auto Roll preference toggle (configuration only).
- Gold reserve selector (configuration only).
- Manual hero-name input and target-star selector (prototype only).
- Local `heroes.json` seed file with two entries verified from the supplied recording: Sora (4 Gold) and Thamuz (5 Gold).

## Not implemented
- Complete season roster and portrait assets.
- Screen capture / shop recognition.
- Automated tapping, buying, or rolling.
- Persistent lineup storage.
- Floating overlay.

The app intentionally does not interact with the game yet. Those parts require separate testing and a compatibility/policy review. No game files are modified and no anti-cheat bypass is included.

## Build from GitHub Actions
1. Upload this project to a GitHub repository.
2. Open the **Actions** tab.
3. Run **Build Android APK** (or push to `main`).
4. Download the `MCGG-Auto-Assistant-debug` artifact from the workflow run.
5. Extract the artifact and install `app-debug.apk` on Android.

The project uses Android Gradle Plugin 8.7.3, Gradle 8.9, Java 17, minSdk 26, targetSdk 35.
