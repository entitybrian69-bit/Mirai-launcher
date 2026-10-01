# Changelog — Modrinth shell + updater

## 2026-10-01

### Phase 0
- Added `AUDIT.md` with screen/feature map, renderer location, and why Check for Update fails.

### Phase 1
- Left `MiraiNavigationRail` is the Modrinth-style shell. Existing screens remain the destinations so no feature is removed.
- Discover = download graph. Play/Home = `LauncherMain`. Library = instance grid. Plus = create version. Gear = settings.
- Multiplayer stays on the rail.

### Phase 2
- Added GitHub Releases API models and mapping (`GithubRelease.kt`).
- Updater target: `GET https://api.github.com/repos/entitybrian69-bit/Mirai-launcher/releases/latest`.
- Compares `tag_name` to `BuildConfig.VERSION_NAME`.
- Falls back to `mirai-update.json` if present.
- No published release (404) → treat manual check as up to date.

### Phase 3
- About tab already shows maintainer **entitybrian**, Zalith upstream credit, GitHub/license links, and Check for Update.

### Phase 4
- Agent host has no Android SDK. Run `./gradlew :ZalithLauncher:assembleDebug -Darch=all` locally.

## Manual tests
1. Rail destinations all open previous screens.
2. About → Check for Update: no crash offline; up-to-date if no newer tag; dialog if a newer GitHub Release exists.
3. Renderer / accounts / instance gear / Discover install still work.
