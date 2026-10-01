# Mirai Launcher — Phase 0 Audit

Date: 2026-10-01  
Repo: entitybrian69-bit/Mirai-launcher  
Branch audited: `Mirai-launcher` @ `ea08a890`  
App identity (`ZalithLauncher/gradle.properties`): `Mirai Launcher` / version `2.6.1` (`200043`)

## Stack

- Gradle multi-module Android app. Root project name `MiraiLauncher`.
- App module: `:ZalithLauncher` (package `com.movtery.zalithlauncher`).
- Supporting modules: CardGrid, ColorPicker, Guide, InputMap, LayerController, LWJGL (+ 3.3.3 / 3.4.1 / patches), Terracotta, third_party/LTW.
- UI: Jetpack Compose + Material 3 + AndroidX Navigation 3 (`NavDisplay` / typed `NavKey`s).
- Min API 26, compile against API 37, JDK 21 (per README).
- License: GPL-3.0 with Zalith additional naming terms.

## Existing top-level screens (do not remove)

From `NestedNavKey` + `NormalNavKey`:

| Screen | Nav key | File |
|---|---|---|
| Splash / unpack deps | `Splash` / `UnpackDeps` | `ui/screens/splash` |
| Home / launch | `LauncherMain` | `LauncherScreen.kt` |
| Discover / download hub | `Download` + game/modpack/mod/resource/saves/shaders/favorites | `DownloadScreen.kt` + `content/download` |
| Asset details | `AssetInfo` / `DownloadAssets` | `content/assetinfo` |
| Library / instances | `VersionsManager` | `VersionsManageScreen.kt` |
| Instance settings | `VersionSettings` (overview, config, loader, mods, saves, resource packs, shaders, screenshots, servers) | `content/versions` |
| Export modpack | `VersionExport` | `VersionExportScreen.kt` |
| Accounts | `AccountManager` | `AccountManageScreen.kt` |
| Multiplayer | `Multiplayer` | `MultiplayerScreen.kt` |
| File selector / manager | `FileSelector` + `OpenFileManager` event | `FileSelectorScreen.kt` |
| WebView | `WebScreen` | `WebViewScreen.kt` |
| Logs | `LogView` | `LogViewScreen.kt` |
| License viewer | `License` | `LicenseScreen.kt` |
| Settings tabs | Renderer, Game, Control, Gamepad, Launcher, Java, Control layouts, About | `content/settings/*` |
| Control editor | `ControlEditorActivity` | separate activity |
| Game runtime | `VMActivity` | separate activity |

## Feature inventory (must stay reachable)

- Install / manage multiple game instances and loaders
- Launch, edit, delete, duplicate, export, isolate versions
- Modrinth + CurseForge browse/download for mods, modpacks, resource packs, worlds, shaders
- Favorites and search-by-id
- Microsoft / offline / Yggdrasil accounts, offline wardrobe / skins
- Java runtime manager + launch args + RAM
- Renderer + Vulkan driver + graphics API (GL4ES / VirGL / Zink come from `Renderers` + renderer plugins)
- Touch control layouts + gamepad remapping
- Built-in file manager
- Multiplayer (Terracotta)
- Task overlay, crash logs, log share
- Background / theme / appearance (`LauncherSettingsScreen`)
- In-app update check (broken for this fork until Phase 2)

## Current navigation (already started)

`MainScreen.kt` already hosts a left `MiraiNavigationRail`:

- Home → `LauncherMain`
- Discover → download hub (mods first)
- Library → `VersionsManager`
- Multiplayer → `Multiplayer`
- Plus → download/create game version
- Settings → settings nested graph
- Account shortcut at bottom

Top bar still exposes Home / Download / Settings / File manager / task drawer. **No feature is deleted by the rail; it is an additional shell.**

## Renderer selection

- Global: `AllSettings.renderer` on `RendererSettingsScreen` via `Renderers.getRenderers()`.
- Vulkan driver: `AllSettings.vulkanDriver` + `DriverPluginManager`.
- Graphics API: `AllSettings.graphicsApi`.
- Zink-specific toggles: `zinkPreferSystemDriver`, `vsyncInZink`.
- Per-instance overrides live under Version Settings → Config (`Versions.Config`).

Assumption: keep plugin-provided renderer list. Do not hard-replace it with three static labels.

## About + update checker

- UI: `AboutInfoScreen.kt` (Settings → About). Already shows owner name `entitybrian` and project/GitHub links.
- Manual button fires `EventViewModel.Event.CheckUpdate`.
- Implementation: `LauncherUpgradeViewModel` fetched `mirai-update.json` from GitHub Releases latest/download.
- **Why it fails:** there are **no GitHub Releases** on this repo, so the JSON 404s and the button appears to do nothing.
- Dialogs `UpgradeDialog` / `UpgradeFilesDialog` are fine once they receive `RemoteData`.

## Assumptions

1. Update source of truth is **this** repo (`entitybrian69-bit/Mirai-launcher`), not `EntityBrian/ZalithLauncher2`.
2. Existing Zalith screens stay mounted behind the new shell.
3. Multiplayer stays on the rail (removing it would drop a feature).
4. Full `./gradlew clean assembleDebug` must be run on a machine with JDK 21 + Android SDK.
