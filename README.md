# Battery Temperature

[![Build & Release](https://github.com/wized2/Battery-Temperature/actions/workflows/build.yml/badge.svg)](https://github.com/wized2/Battery-Temperature/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Live **battery temperature** for Android — my first Android app (early 2024), recovered from the original APK and kept as FOSS.

**Releases:** https://github.com/wized2/Battery-Temperature/releases

## Features

- Large live temperature (°C / °F toggle)
- Status chip: Normal · Warm · Hot · Cold (color-coded)
- Battery level, voltage, charging source, health
- Auto-refresh every 3 seconds while the screen is open
- Material 3 DayNight theme
- Offline — no internet permission, no ads, no trackers

## How it works

Reads the sticky `ACTION_BATTERY_CHANGED` broadcast:

- `BatteryManager.EXTRA_TEMPERATURE` (tenths of a °C)
- Level, scale, voltage, plugged state, health

## Build

```bash
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

## History

Original project folder was lost. Source was reconstructed from `Battery Temperature.apk` (apktool + jadx) and cleaned up under `app/`. Decompiled reference: [`docs/decompiled/MainActivity.java`](docs/decompiled/MainActivity.java).

## License

MIT — see [LICENSE](LICENSE).

---

*First steps into Android — kept public as a small piece of history.*
