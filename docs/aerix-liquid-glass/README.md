# Aerix Liquid Glass — launcher refresh

The redesign establishes an aurora-lit, liquid-glass identity across the persistent launcher shell, home dashboard, instance library, discovery, multiplayer, settings, account/skin and instance-detail surfaces. Existing routes and actions remain wired to their current screens.

## Ten mock screens

These 1600 × 900 PNGs are concept references for the new layout, not screenshots from a running emulator:

1. `mockups/01-home.png` — launch hero, recent instances, selected-world controls and quick tools.
2. `mockups/02-library.png` — searchable, filterable adaptive instance grid.
3. `mockups/03-create-instance.png` — version, loader and profile creation flow.
4. `mockups/04-discover.png` — curated discovery and category browsing.
5. `mockups/05-mod-details.png` — project overview, compatibility and install actions.
6. `mockups/06-multiplayer.png` — quick connect, favorites and server health.
7. `mockups/07-settings.png` — settings categories and glass/background controls.
8. `mockups/08-wallpapers.png` — wallpaper gallery and material preview.
9. `mockups/09-account-skin.png` — linked account and player-skin studio.
10. `mockups/10-instance-overview.png` — launch controls, content, runtime and health.

## Regenerate

From the repository root, run:

```sh
python3 docs/aerix-liquid-glass/generate_mockups.py
```

The script requires ImageMagick (`convert`) and uses the wallpapers and profile art already shipped in `MiraiLauncher`. It renders the images deterministically with crisp UI text; no image-generation service or Python imaging package is needed.

## Material and performance notes

Glass is built from cached Compose gradients, spectral highlights and fine edge reflections. The wallpaper stays visible with `backgroundBlur = 0`; the refresh adds no always-running animation or per-frame full-screen blur. Elevations were reduced for frequently repeated surfaces, while the existing background-capture/blur cache and saved blur preference remain intact.
