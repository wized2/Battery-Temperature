# Battery Temperature

[![Build & Release](https://github.com/wized2/Battery-Temperature/actions/workflows/build.yml/badge.svg)](https://github.com/wized2/Battery-Temperature/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Simple **battery thermal monitor** for Android. Reads the system battery sticky intent and shows temperature, status, level, voltage, health, and charging source.

**Releases:** https://github.com/wized2/Battery-Temperature/releases

## Features

- Large live temperature with **°C / °F** toggle
- Status chip: Normal · Warm · Hot · Cold
- Level, voltage, power source, battery health
- Auto-refresh every 3 seconds while open
- Material 3 DayNight UI
- Offline — no network permission

## History

My **first Android app** (early 2024). The original project folder was lost; this repo was reconstructed from the APK and kept as FOSS history, with a cleaner UI on main.

## Build

```bash
./gradlew :app:assembleRelease
```

## License

MIT — see [LICENSE](LICENSE).
