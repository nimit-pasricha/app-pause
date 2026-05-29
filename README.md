# Beat

*(working title)*

A screen-time app for Android that **doesn't block apps** — it inserts a short, boring, deliberately-designed pause before you open the apps you tend to open on autopilot, so your brain catches up with your thumb. The pause always lets you through; it just gives you a beat to change your mind. Built for personal use, sideloaded.

- **Pause, don't block.** Friction and a moment of reflection, never a wall.
- **Opinionated, not configurable.** The only thing you set is *which apps to watch*.
- **Boredom is the intervention.** A fixed, gently-draining wait with nothing to do.
- **Track the wins.** The one number that matters: how often you backed out.

---

## Tech stack

- Kotlin + Jetpack Compose (Material 3), single `:app` module
- minSdk 29, compile/target SDK 36 (Android 16)
- AGP 8.9.2 · Gradle 8.11.1 · JDK 17+ (JDK 21 used in development)
- Jetpack DataStore for on-device state (no backend, no accounts, no telemetry)

## Project layout

```
app/src/main/java/com/nimitpasricha/pause/
  service/   PauseAccessibilityService — detects the foreground app
  ui/        PauseActivity, PauseScreen, MainActivity (setup + wins)
  domain/    TimerPolicy, PauseLines (the copy pool)
  data/      VisitTracker, WatchedApps, Stats (DataStore)
  theme/     Theme, ThemeProvider, themes/ (Blossom, Sky)
```

---

## Building locally (Arch Linux + Android Studio)

Android Studio bundles its own JDK and installs the Android SDK, so you don't need a separate Java install.

1. Open the project in Android Studio; let it sync Gradle (it will offer to install any missing SDK bits).
2. Or build from the terminal, pointing at Studio's JDK and SDK:
   ```bash
   export JAVA_HOME=/opt/android-studio/jbr
   export ANDROID_HOME=$HOME/Android/Sdk
   ./gradlew assembleDebug          # build the debug APK
   ./gradlew testDebugUnitTest      # run unit tests
   ```
   `local.properties` (git-ignored) already points `sdk.dir` at the SDK.

## Installing on your phone

1. On the phone: **Settings → About phone → tap Build number 7×** to unlock Developer options, then **Developer options → enable USB debugging**.
2. Plug in over USB, accept the RSA prompt, then:
   ```bash
   ./gradlew installDebug
   ```
3. **Turn the service on (one time):** **Settings → Accessibility → Beat → enable.** Android requires this to be done manually; the app cannot enable it for you.
4. Open Beat, pick which apps to watch, then open one of them — the pause appears.

> The app can always be disabled from the same Accessibility settings — it's your phone. The goal is to break the *reflexive* open, not to be an uncircumventable wall.

---

## Releases (CI/CD)

- **Every push / PR** to `main` runs a CI build + unit tests + lint (see `.github/workflows/ci.yml`).
- **Pushing a `v*` tag** builds a signed release APK and attaches it to a GitHub Release (`.github/workflows/release.yml`).

### One-time release-signing setup

Generate a keystore (keep it safe — it's how the app updates in place; losing it means uninstall/reinstall to update):

```bash
keytool -genkeypair -v -keystore beat-release.jks -keyalg RSA -keysize 2048 \
  -validity 10000 -alias beat -dname "CN=Beat"
# you'll be prompted for store/key passwords

base64 -w0 beat-release.jks   # copy this single-line output
```

Add four repository secrets (Settings → Secrets and variables → Actions, or `gh secret set`):

| Secret | Value |
| --- | --- |
| `SIGNING_KEY_BASE64` | the base64 output above |
| `KEY_ALIAS` | `beat` |
| `KEYSTORE_PASSWORD` | the store password you chose |
| `KEY_PASSWORD` | the key password you chose |

Then cut a release:

```bash
git tag v0.1.0 && git push origin v0.1.0
```

The keystore file itself is git-ignored and must never be committed.
