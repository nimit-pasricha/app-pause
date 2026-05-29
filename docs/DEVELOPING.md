# Developing Beat

Maintainer notes — building, testing, and how releases are produced. (Users don't need any of this; see the [README](../README.md) to just install the app.)

## Stack

- Kotlin + Jetpack Compose (Material 3), single `:app` module
- minSdk 29, compile/target SDK 36 (Android 16)
- AGP 8.9.2 · Gradle 8.11.1 · JDK 17+
- Jetpack DataStore for all on-device state — no backend, accounts, or telemetry

```
app/src/main/java/com/nimitpasricha/pause/
  service/   PauseAccessibilityService — detects the foreground app
  ui/        PauseActivity, PauseScreen, MainActivity / SetupScreen
  domain/    TimerPolicy, DailyVisits, PauseLines (the copy pool)
  data/      VisitTracker, WatchedApps, Stats (DataStore)
  theme/     Theme, ThemeProvider, Fonts, themes/ (Blossom, Sky)
```

## Building locally (Android Studio)

Android Studio bundles its own JDK and the Android SDK, so no separate Java install is needed. Open the project and let Gradle sync, or build from the terminal:

```bash
export JAVA_HOME=/opt/android-studio/jbr
export ANDROID_HOME=$HOME/Android/Sdk
./gradlew assembleDebug          # build the debug APK
./gradlew testDebugUnitTest      # run unit tests
./gradlew lintDebug              # lint
```

`local.properties` (git-ignored) points `sdk.dir` at the SDK.

### Run on a phone

Enable **Developer options → USB debugging** on the phone, plug in over USB, then:

```bash
./gradlew installDebug
```

Then enable the accessibility service and "display over other apps" when the app prompts. The accessibility-launch-over-app behavior is best verified on real hardware.

## CI / releases

- **`ci.yml`** runs on every pull request to `main`: build + unit tests + lint, and uploads the debug APK as an artifact.
- **`release.yml`** runs on every merge to `main`: it tests, builds, and publishes a signed APK to a new GitHub Release, versioned `1.0.<run number>`. Nothing else to do — merging is the release.

### Signing

Releases are signed automatically:

- **With no setup**, the APK is signed with a debug key. It's installable, but the signature isn't stable across builds, so an update may require uninstalling first.
- **For stable, update-in-place signing**, add a release keystore as repository secrets. Generate one once:

  ```bash
  keytool -genkeypair -v -keystore beat-release.jks -keyalg RSA -keysize 2048 \
    -validity 10000 -alias beat -dname "CN=Beat"
  base64 -w0 beat-release.jks   # copy this single-line output
  ```

  Then add four secrets (Settings → Secrets and variables → Actions, or `gh secret set`):

  | Secret | Value |
  | --- | --- |
  | `SIGNING_KEY_BASE64` | the base64 output above |
  | `KEY_ALIAS` | `beat` |
  | `KEYSTORE_PASSWORD` | the store password you chose |
  | `KEY_PASSWORD` | the key password you chose |

  The keystore file itself is git-ignored and must never be committed. Keep it safe — it's what lets future releases update the app in place.

## Naming

The display name ("Beat") is a working title; change it in `app/src/main/res/values/strings.xml`. The package id `com.nimitpasricha.pause` is fixed (changing it breaks update-in-place).
