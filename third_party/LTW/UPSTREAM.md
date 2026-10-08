# Large Thin Wrapper integration

The `ltw/` Android library is vendored from [MojoLauncher/LTW](https://github.com/MojoLauncher/LTW), pinned at upstream commit [`9dc80cb121bd1a1ba9f3b18d10c811753d4261e3`](https://github.com/MojoLauncher/LTW/commit/9dc80cb121bd1a1ba9f3b18d10c811753d4261e3) (2026-10-06). This includes the upstream shader-optimizer change that preserves linked shader inputs/outputs during post-link optimization and its follow-up fix to iterate the linked IR (`ir`).

Upstream copyright and source notices are preserved in the vendored files. The upstream project is licensed under LGPL-3.0; see [`LICENSE`](LICENSE) and the upstream [README](README-upstream.md). The unused prebuilt host `glsl_compiler` helper was omitted; the Android CMake build compiles from the preserved sources.

Mirai's renderer adapter lives in `MiraiLauncher/src/main/java/com/movtery/zalithlauncher/game/renderer/renderers/LTWRenderer.kt`. It retains the upstream renderer/library identifiers and wires in the GLES 3 compatibility and environment behavior used by the reference integration. Gradle builds the native `libltw.so` for each requested Android ABI from the upstream CMake sources.
