# Battery Temperature

[![Build & Release](https://github.com/wized2/Battery-Temperature/actions/workflows/build.yml/badge.svg)](https://github.com/wized2/Battery-Temperature/actions/workflows/build.yml)


My **first Android app** (early 2024), built with help from an early ChatGPT.

It shows the phone’s **battery temperature** in °C using the sticky `ACTION_BATTERY_CHANGED` broadcast, with a simple refresh button.

## What it does

- Reads `BatteryManager.EXTRA_TEMPERATURE` (tenths of a degree) via `registerReceiver(null, BATTERY_CHANGED)`
- Displays `Battery Temp: X.Y °C`
- Refresh button re-reads the sticky intent

## Project notes

- Package: `com.alpha.battery`
- Original language: **Kotlin** (`MainActivity.kt`)
- UI: one screen (`activity_main`) — background image, battery art, temperature text, refresh button
- Theme: Material 3 DayNight, no action bar

## Source recovery

The original project folder was lost. This repository was **reconstructed from the APK** (`Battery Temperature.apk`):

| Tool | Role |
|------|------|
| **apktool** | Resources, manifest, smali |
| **jadx** | Java view of `MainActivity` (from `classes3.dex`) |

Clean Kotlin under `app/` matches the decompiled logic. Raw jadx output is kept in [`docs/decompiled/MainActivity.java`](docs/decompiled/MainActivity.java) for transparency.

## Build

Open in Android Studio (Giraffe+) or:

```bash
./gradlew :app:assembleDebug
```

(Add a Gradle wrapper if your environment does not provide one.)

## License

MIT — see [LICENSE](LICENSE).

---

*First steps into Android. Kept public as a small piece of history.*
