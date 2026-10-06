#!/usr/bin/env python3
"""Render ten crisp, repeatable Aerix Liquid Glass launcher mockups.

Requires ImageMagick 6 or 7 (`convert`). The source wallpapers are the licensed
assets already shipped by the launcher. Images are design references, not claimed
as device captures of the running app.
"""
from __future__ import annotations

import subprocess
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
WALLPAPERS = ROOT / "MiraiLauncher/src/main/assets/wallpapers"
DRAWABLES = ROOT / "MiraiLauncher/src/main/res/drawable-nodpi"
OUTPUT = Path(__file__).resolve().parent / "mockups"
WIDTH, HEIGHT = 1600, 900

WHITE = "#F3F8FC"
MUTED = "#C0D0DD"
QUIET = "#92A9BA"
CYAN = "#9AF2EC"
BLUE = "#9DE8FF"
VIOLET = "#C2B5FF"
ROSE = "#F1B1DC"
GREEN = "#9AE8C4"
AMBER = "#FFD08B"
INK = "#0A1721"


def run(args: list[str]) -> None:
    subprocess.run(args, check=True, stdout=subprocess.DEVNULL)


def wallpaper(name: str) -> Path:
    return WALLPAPERS / name


def rounded_crop(source: Path, width: int, height: int, radius: int, out: Path) -> None:
    mask = [
        "(", "-size", f"{width}x{height}", "xc:none", "-fill", "white",
        "-draw", f"roundrectangle 0,0 {width - 1},{height - 1} {radius},{radius}", ")",
    ]
    run([
        "convert", str(source), "-resize", f"{width}x{height}^", "-gravity", "center",
        "-extent", f"{width}x{height}", *mask, "-compose", "CopyOpacity", "-composite",
        "-strip", str(out),
    ])


class Mockup:
    def __init__(self, background: Path, section: str, selected: str):
        self.temp = Path(tempfile.mkdtemp(prefix="aerix-mock-"))
        self.base = self.temp / "base.png"
        self.args = [
            "convert", str(background), "-resize", f"{WIDTH}x{HEIGHT}^", "-gravity", "center",
            "-extent", f"{WIDTH}x{HEIGHT}", "-modulate", "82,92,100",
            "-fill", "rgba(4,10,21,0.52)", "-stroke", "none", "-draw", f"rectangle 0,0 {WIDTH},{HEIGHT}",
            "-fill", "rgba(3,9,19,0.20)", "-draw", f"rectangle 0,0 {WIDTH},145",
            "-strip", str(self.base),
        ]
        run(self.args)
        self.args = ["convert", str(self.base)]
        self.chrome(section, selected)

    @staticmethod
    def _n(value: float) -> str:
        return str(int(round(value)))

    def draw(self, command: str, fill: str = "none", stroke: str = "none", width: int = 1) -> None:
        self.args.extend(["-fill", fill, "-stroke", stroke, "-strokewidth", str(width), "-draw", command])

    def rect(self, x: int, y: int, width: int, height: int, fill: str,
             stroke: str = "none", radius: int = 0, stroke_width: int = 1) -> None:
        x2, y2 = x + width, y + height
        shape = f"roundrectangle {x},{y} {x2},{y2} {radius},{radius}" if radius else f"rectangle {x},{y} {x2},{y2}"
        self.draw(shape, fill, stroke, stroke_width)

    def line(self, x1: int, y1: int, x2: int, y2: int, color: str, width: int = 1) -> None:
        self.draw(f"line {x1},{y1} {x2},{y2}", "none", color, width)

    def circle(self, cx: int, cy: int, radius: int, fill: str, stroke: str = "none", width: int = 1) -> None:
        self.draw(f"circle {cx},{cy} {cx + radius},{cy}", fill, stroke, width)

    def panel(self, x: int, y: int, width: int, height: int, radius: int = 28,
              tint: str = "rgba(158,205,226,0.13)", rim: str = "rgba(241,251,255,0.34)") -> None:
        self.rect(x + 1, y + 7, width, height, "rgba(0,4,12,0.24)", radius=radius)
        self.rect(x, y, width, height, tint, rim, radius, 2)
        self.line(x + radius, y + 2, x + width - radius, y + 2, "rgba(255,255,255,0.32)", 1)
        self.line(x + 2, y + radius + 12, x + 2, y + height - radius - 12, "rgba(255,255,255,0.12)", 1)

    def panel_outline(self, x: int, y: int, width: int, height: int, radius: int = 28,
                      rim: str = "rgba(242,252,255,0.42)") -> None:
        self.rect(x, y, width, height, "none", rim, radius, 2)
        self.line(x + radius, y + 2, x + width - radius, y + 2, "rgba(255,255,255,0.38)", 1)

    def text(self, x: int, y: int, value: str, size: int = 18, color: str = WHITE,
             bold: bool = False, tracking: float = 0) -> None:
        escaped = value.replace("\\", "\\\\").replace("'", "\\'")
        self.args.extend([
            "-font", "DejaVu-Sans-Bold" if bold else "DejaVu-Sans",
            "-pointsize", str(size), "-kerning", str(tracking), "-fill", color,
            "-stroke", "none", "-draw", f"text {x},{y} '{escaped}'",
        ])

    def pill(self, x: int, y: int, width: int, height: int, label: str,
             fill: str = "rgba(231,247,255,0.08)", stroke: str = "rgba(241,251,255,0.24)",
             text_color: str = WHITE, size: int = 13, bold: bool = True) -> None:
        self.rect(x, y, width, height, fill, stroke, height // 2, 1)
        self.text(x + 18, y + height // 2 + size // 3, label, size, text_color, bold)

    def button(self, x: int, y: int, width: int, height: int, label: str,
               fill: str = CYAN, text_color: str = INK, size: int = 15) -> None:
        self.rect(x, y, width, height, fill, "rgba(246,255,255,0.76)", height // 2, 1)
        self.text(x + 20, y + height // 2 + size // 3, label, size, text_color, True)
        self.circle(x + width - 25, y + height // 2, 10, "rgba(255,255,255,0.34)")
        self.text(x + width - 29, y + height // 2 + 5, "›", 15, text_color, True)

    def image(self, source: Path, x: int, y: int, width: int, height: int, radius: int = 24) -> None:
        crop = self.temp / f"crop-{len(list(self.temp.glob('crop-*.png'))):02d}.png"
        rounded_crop(source, width, height, radius, crop)
        self.args.extend([str(crop), "-geometry", f"+{x}+{y}", "-compose", "over", "-composite"])

    def photo_dim(self, x: int, y: int, width: int, height: int, radius: int = 24,
                  alpha: float = 0.18) -> None:
        self.rect(x, y, width, height, f"rgba(2,8,16,{alpha})", radius=radius)

    def title(self, eyebrow: str, heading: str, subtitle: str, y: int = 156) -> None:
        self.text(132, y, eyebrow.upper(), 11, CYAN, True, 1.8)
        self.text(132, y + 42, heading, 31, WHITE, True, -0.35)
        self.text(132, y + 68, subtitle, 14, MUTED)

    def chrome(self, section: str, selected: str) -> None:
        self.panel(22, 26, 82, 848, 40, "rgba(106,157,181,0.16)", "rgba(246,253,255,0.42)")
        self.panel(122, 26, 1456, 82, 35, "rgba(103,157,180,0.17)", "rgba(246,253,255,0.40)")
        self.circle(63, 66, 22, "rgba(136,240,233,0.18)", "rgba(220,255,255,0.60)", 2)
        self.text(55, 74, "A", 23, CYAN, True)
        self.text(153, 59, "AERIX", 17, WHITE, True, 1.8)
        self.text(153, 82, section.upper(), 10, CYAN, True, 1.2)
        self.pill(815, 45, 366, 44, "⌕   Search mods, versions, worlds", "rgba(218,241,250,0.07)", "rgba(244,252,255,0.23)", MUTED, 13, False)
        self.pill(1200, 45, 144, 44, "↓   DOWNLOADS", "rgba(135,237,231,0.13)", "rgba(198,255,255,0.38)", CYAN, 11)
        self.pill(1354, 45, 104, 44, "▣   FILES", "rgba(218,241,250,0.06)", "rgba(244,252,255,0.22)", WHITE, 11)
        self.circle(1518, 67, 22, "rgba(189,171,255,0.30)", "rgba(246,251,255,0.54)", 2)
        self.text(1509, 73, "EB", 11, WHITE, True)

        nav = [
            ("H", "HOME"), ("L", "LIBRARY"), ("D", "DISCOVER"),
            ("M", "SERVERS"), ("W", "WALLPAPERS"),
        ]
        for i, (glyph, label) in enumerate(nav):
            y = 158 + i * 76
            is_selected = label == selected
            if is_selected:
                self.rect(38, y - 23, 50, 48, "rgba(142,239,232,0.20)", "rgba(220,255,255,0.53)", 20, 1)
                self.circle(36, y, 3, CYAN)
                self.text(55, y + 7, glyph, 18, CYAN, True)
            else:
                self.circle(63, y, 22, "rgba(228,245,255,0.035)", "rgba(235,248,255,0.16)", 1)
                self.text(57, y + 6, glyph, 16, MUTED, True)
        self.line(43, 560, 83, 560, "rgba(233,250,255,0.20)", 1)
        self.circle(63, 610, 22, "rgba(228,245,255,0.035)", "rgba(235,248,255,0.16)", 1)
        self.text(56, 616, "S", 16, MUTED, True)
        self.circle(63, 821, 23, "rgba(189,171,255,0.30)", "rgba(246,251,255,0.48)", 1)
        self.text(54, 826, "EB", 11, WHITE, True)

    def finish(self, name: str) -> Path:
        out = OUTPUT / name
        out.parent.mkdir(parents=True, exist_ok=True)
        self.args.extend(["-strip", "-define", "png:compression-level=9", str(out)])
        run(self.args)
        return out


def home() -> Path:
    m = Mockup(wallpaper("wp_07_ocean_arch.jpg"), "Home", "HOME")
    # The wide composition follows MiraiHomeDashboard: launch hero, three navigation
    # actions, recent-instance strip, selected-world panel and quick tools.
    hero = (132, 132, 964, 352)
    m.image(DRAWABLES / "mirai_hero_bg.webp", hero[0], hero[1], hero[2], hero[3], 32)
    m.photo_dim(*hero, alpha=0.17)
    m.panel_outline(*hero, 32)
    m.circle(184, 174, 15, "rgba(255,255,255,0.16)", "rgba(240,252,255,0.38)", 1)
    m.text(179, 180, "A", 14, CYAN, True)
    m.text(210, 178, "AERIX  /  YOUR WORLD", 11, CYAN, True, 1.3)
    m.text(168, 252, "A new world", 38, WHITE, True, -0.5)
    m.text(168, 297, "awaits you.", 38, WHITE, True, -0.5)
    m.text(170, 327, "One calm place to shape, tune and launch your worlds.", 13, MUTED)
    m.pill(168, 394, 620, 48, "Vanilla 1.21.4   ·   Minecraft 1.21.4", "rgba(205,239,249,0.10)", "rgba(242,252,255,0.28)", WHITE, 12)
    m.button(806, 394, 174, 48, "PLAY NOW", CYAN, INK, 13)

    # SelectedWorldPanel: the chosen instance is actionable without opening the library.
    m.panel(1120, 132, 428, 444, 30, "rgba(115,162,183,0.16)")
    m.text(1152, 174, "ACTIVE WORLD", 10, CYAN, True, 1.3)
    m.text(1152, 207, "Your selected instance", 19, WHITE, True)
    m.rect(1152, 226, 54, 54, "rgba(135,220,233,0.18)", "rgba(226,250,255,0.32)", 14, 1)
    m.text(1163, 260, "1.21", 15, CYAN, True)
    m.text(1220, 248, "Vanilla 1.21.4", 17, WHITE, True)
    m.text(1220, 270, "Vanilla  /  1.21.4", 11, MUTED)
    m.line(1152, 300, 1516, 300, "rgba(233,249,255,0.22)")
    for i, (label, value) in enumerate([("RENDERER", "LTW"), ("MINECRAFT", "1.21.4"), ("PROFILE", "Vanilla 1.21.4")]):
        y = 334 + i * 35
        m.text(1152, y, label, 9, QUIET, True, 0.8)
        m.text(1400, y, value, 12, WHITE, True)
    m.button(1152, 497, 230, 44, "LAUNCH", CYAN, INK, 12)
    m.pill(1394, 497, 122, 44, "SETTINGS", "rgba(220,240,250,0.08)", "rgba(240,252,255,0.22)", WHITE, 10)

    actions = [
        ("+", "New instance", "Build a world", CYAN),
        ("◇", "Discover", "Mods & packs", BLUE),
        ("▦", "Your library", "All instances", VIOLET),
    ]
    for i, (glyph, label, detail, color) in enumerate(actions):
        x = 132 + i * 326
        m.panel(x, 500, 310, 76, 20, "rgba(120,174,197,0.14)")
        m.circle(x + 33, 538, 18, f"rgba(144,234,237,0.15)", f"{color}80", 1)
        m.text(x + 27, 544, glyph, 15, color, True)
        m.text(x + 61, 532, label, 15, WHITE, True)
        m.text(x + 61, 552, detail, 10, MUTED)
        m.text(x + 286, 543, "›", 20, color, True)

    m.text(132, 617, "Recently played", 19, WHITE, True)
    m.text(132, 638, "3 saved worlds", 10, MUTED)
    m.text(1004, 628, "OPEN LIBRARY  →", 10, CYAN, True, 0.7)
    recent = [
        ("Vanilla 1.21.4", "1.21.4  ·  Vanilla", CYAN),
        ("Sodium 1.20.1", "1.20.1  ·  Fabric", BLUE),
        ("Create: Above", "1.20.4  ·  Forge", VIOLET),
    ]
    for i, (name, detail, color) in enumerate(recent):
        x, y = 132 + i * 326, 656
        m.panel(x, y, 310, 184, 22, "rgba(125,174,196,0.13)")
        m.rect(x + 16, y + 69, 42, 42, f"{color}2A", f"{color}88", 12, 1)
        m.text(x + 23, y + 96, "MC", 11, color, True)
        m.text(x + 71, y + 82, name, 13, WHITE, True)
        m.text(x + 71, y + 103, detail.split("  ·  ")[0], 9, MUTED)
        m.text(x + 71, y + 121, detail.split("  ·  ")[-1], 9, color)
        m.circle(x + 265, y + 85, 15, "rgba(228,245,255,0.07)", "rgba(236,250,255,0.18)", 1)
        m.text(x + 259, y + 91, "▶", 10, color, True)
        m.circle(x + 265, y + 128, 15, "rgba(228,245,255,0.07)", "rgba(236,250,255,0.18)", 1)
        m.text(x + 260, y + 133, "⚙", 9, MUTED, True)

    # Two-by-two quick tools mirror the live performance, diagnostics and task actions.
    m.panel(1120, 596, 428, 244, 25, "rgba(126,172,195,0.14)")
    m.text(1148, 630, "TOOLS & STATUS", 10, CYAN, True, 1.2)
    m.text(1148, 649, "Small refinements, one tap away", 10, MUTED)
    for i, (label, detail, color) in enumerate([
        ("FPS", "Performance", CYAN),
        ("JRE / GC", "Memory tuning", BLUE),
        ("Crash Doctor", "Find a fix", AMBER),
        ("0 tasks", "Downloads & files", VIOLET),
    ]):
        col, row = i % 2, i // 2
        x, y = 1148 + col * 190, 669 + row * 76
        m.rect(x, y, 178, 64, "rgba(223,242,249,0.06)", "rgba(241,251,255,0.19)", 15, 1)
        m.circle(x + 20, y + 32, 10, f"{color}40", f"{color}90", 1)
        m.text(x + 39, y + 28, label, 11, WHITE, True)
        m.text(x + 39, y + 47, detail, 9, MUTED)
    return m.finish("01-home.png")


def library() -> Path:
    m = Mockup(wallpaper("wp_10_spruce_mist.jpg"), "Library", "LIBRARY")
    # Match VersionsLayout's adaptive, searchable four-column instance grid.
    m.pill(132, 136, 1070, 44, "⌕   Search instances...", "rgba(218,241,249,0.07)", "rgba(244,252,255,0.23)", MUTED, 13, False)
    m.pill(1214, 136, 44, 44, "≡", "rgba(218,241,249,0.08)", "rgba(244,252,255,0.23)", WHITE, 15)
    m.pill(1270, 136, 44, 44, "▣", "rgba(218,241,249,0.08)", "rgba(244,252,255,0.23)", WHITE, 12)
    m.button(1326, 136, 222, 44, "NEW INSTANCE", CYAN, INK, 12)
    filters = [("ALL  16", 114), ("MODPACKS", 148), ("VANILLA", 126), ("PINNED", 116)]
    x = 132
    for i, (label, width) in enumerate(filters):
        m.pill(x, 196, width, 40, label,
               "rgba(141,235,229,0.18)" if i == 0 else "rgba(228,245,255,0.055)",
               "rgba(240,252,255,0.26)", CYAN if i == 0 else MUTED, 10)
        x += width + 10

    cards = [
        ("Vanilla 1.21.4", "Vanilla 1.21.4", CYAN),
        ("Sodium 1.20.1", "Fabric 1.20.1", BLUE),
        ("Create: Above", "Forge 1.20.4", VIOLET),
        ("Skybound", "Quilt 1.20.1", ROSE),
        ("Cherry Grove", "Vanilla 1.20.4", CYAN),
        ("Shader Lab", "Fabric 1.21.3", BLUE),
        ("Survival Plus", "Forge 1.19.4", AMBER),
        ("Better Adventures", "Fabric 1.20.1", GREEN),
        ("Builder's World", "Quilt 1.21.1", VIOLET),
        ("Vanilla 1.20.1", "Vanilla 1.20.1", CYAN),
        ("Create: Above 2", "Forge 1.20.1", AMBER),
        ("Test Instance", "Fabric 1.21.4", ROSE),
        ("Creative Lab", "Fabric 1.21.2", BLUE),
        ("Legacy World", "Forge 1.18.2", AMBER),
        ("Performance Build", "Fabric 1.21.4", GREEN),
        ("Quilt Testing", "Quilt 1.20.1", VIOLET),
    ]
    card_width, card_height = 342, 138
    for i, (name, detail, accent) in enumerate(cards):
        col, row = i % 4, i // 4
        x, y = 132 + col * 358, 254 + row * 146
        selected = i == 0
        m.panel(x, y, card_width, card_height, 23,
                "rgba(139,193,211,0.17)" if selected else "rgba(124,175,197,0.12)",
                "rgba(158,244,236,0.56)" if selected else "rgba(241,251,255,0.30)")
        m.rect(x + 18, y + 14, 48, 48, f"{accent}25", f"{accent}88", 13, 1)
        m.text(x + 25, y + 44, "MC", 12, accent, True)
        m.text(x + 80, y + 34, name, 15, WHITE, True)
        m.pill(x + 80, y + 43, 142, 24, detail, "rgba(218,241,249,0.07)", "rgba(240,251,255,0.16)", MUTED, 8, False)
        m.circle(x + 35, y + 112, 15, "rgba(224,242,249,0.07)", "rgba(241,251,255,0.20)", 1)
        m.text(x + 29, y + 118, "···", 11, WHITE, True)
        m.circle(x + 235, y + 112, 15, "rgba(224,242,249,0.07)", "rgba(241,251,255,0.20)", 1)
        m.text(x + 230, y + 117, "⚙", 10, MUTED, True)
        m.pill(x + 270, y + 96, 62, 32, "PLAY", "rgba(143,237,230,0.87)", "rgba(244,255,255,0.68)", INK, 9)
    return m.finish("02-library.png")


def create_instance() -> Path:
    m = Mockup(wallpaper("wp_03_sunset_river.jpg"), "Create instance", "LIBRARY")
    m.title("A NEW BEGINNING", "Build an instance", "Choose a base version, select a loader, and tailor the details before install.")
    m.panel(132, 230, 1416, 76, 27, "rgba(121,174,197,0.14)")
    steps = [("01", "VERSION", True), ("02", "LOADER", False), ("03", "DETAILS", False)]
    for i, (num, name, active) in enumerate(steps):
        x = 172 + i * 440
        m.circle(x + 18, 268, 17, "rgba(143,239,231,0.20)" if active else "rgba(221,240,248,0.07)", "rgba(225,255,255,0.46)" if active else "rgba(242,251,255,0.20)", 1)
        m.text(x + 10, 273, num, 11, CYAN if active else MUTED, True)
        m.text(x + 49, 273, name, 12, WHITE if active else MUTED, True, 1)
        if i < 2:
            m.line(x + 236, 268, x + 407, 268, "rgba(241,251,255,0.20)", 2)
    m.panel(132, 330, 890, 494, 28, "rgba(124,177,199,0.15)")
    m.text(166, 374, "SELECT A GAME VERSION", 11, CYAN, True, 1.2)
    m.pill(166, 394, 344, 42, "⌕   Search releases and snapshots", "rgba(226,242,249,0.06)", "rgba(244,251,255,0.20)", MUTED, 12, False)
    m.pill(787, 394, 190, 42, "LATEST RELEASE  ▾", "rgba(226,242,249,0.06)", "rgba(244,251,255,0.20)", WHITE, 10)
    versions = [("1.21.4", "Latest release", True), ("1.21.3", "Release", False), ("1.20.6", "Release", False), ("1.20.4", "Release", False), ("24w14a", "Snapshot", False), ("1.19.4", "Release", False)]
    for i, (version, detail, selected) in enumerate(versions):
        col, row = i % 3, i // 3
        x, y = 166 + col * 273, 462 + row * 129
        m.rect(x, y, 252, 109, "rgba(216,239,248,0.06)", "rgba(241,251,255,0.20)", 21, 1)
        m.circle(x + 31, y + 31, 13, "rgba(140,237,231,0.19)" if selected else "rgba(221,241,248,0.08)", "rgba(236,250,255,0.19)", 1)
        m.text(x + 67, y + 38, version, 19, WHITE, True)
        m.text(x + 20, y + 77, detail, 11, MUTED)
        m.text(x + 20, y + 97, "MINECRAFT", 8, CYAN if selected else QUIET, True, 1)
        if selected:
            m.circle(x + 226, y + 27, 8, CYAN)
            m.circle(x + 226, y + 27, 3, INK)
    m.panel(1048, 330, 500, 494, 28, "rgba(139,179,199,0.17)")
    m.text(1082, 374, "INSTANCE PROFILE", 11, CYAN, True, 1.2)
    m.text(1082, 421, "Vanilla 1.21.4", 25, WHITE, True)
    m.text(1082, 449, "The essentials, ready for your touch.", 12, MUTED)
    m.text(1082, 502, "MOD LOADER", 10, QUIET, True, 1)
    for i, label in enumerate(["Vanilla", "Fabric", "Quilt", "Forge"]):
        m.pill(1082 + i * 105, 516, 96, 39, label, "rgba(141,238,233,0.18)" if i == 0 else "rgba(225,243,249,0.06)", "rgba(234,251,255,0.25)", CYAN if i == 0 else MUTED, 10)
    m.text(1082, 593, "INSTANCE NAME", 10, QUIET, True, 1)
    m.rect(1082, 607, 430, 46, "rgba(225,242,249,0.06)", "rgba(239,250,255,0.20)", 15, 1)
    m.text(1100, 636, "Vanilla 1.21.4", 13, WHITE)
    m.text(1082, 690, "GAME DIRECTORY", 10, QUIET, True, 1)
    m.pill(1082, 704, 430, 42, "Use a separate folder for this instance", "rgba(225,242,249,0.06)", "rgba(239,250,255,0.20)", MUTED, 11, False)
    m.button(1082, 764, 204, 44, "CONTINUE", CYAN, INK, 13)
    m.text(1305, 791, "Step 1 of 3", 11, MUTED)
    return m.finish("03-create-instance.png")


def discover() -> Path:
    m = Mockup(wallpaper("wp_09_flower_meadow.jpg"), "Discover", "DISCOVER")
    m.title("THE COMMUNITY, CURATED", "Find the next thing you love", "A more thoughtful way to discover mods, packs, worlds and shaders.")
    m.pill(1113, 159, 205, 42, "⌕   Search everything", "rgba(218,241,249,0.06)", "rgba(244,252,255,0.22)", MUTED, 12, False)
    m.pill(1330, 159, 218, 42, "TRENDING  ·  THIS WEEK", "rgba(143,238,231,0.16)", "rgba(228,255,252,0.36)", CYAN, 10)
    m.panel(132, 226, 894, 350, 31, "rgba(127,176,198,0.14)")
    m.image(wallpaper("wp_14_cozy_village.jpg"), 544, 239, 468, 324, 25)
    m.photo_dim(544, 239, 468, 324, 25, 0.18)
    m.panel_outline(132, 226, 894, 350, 31)
    m.pill(168, 260, 160, 34, "EDITOR'S PICK", "rgba(143,238,231,0.24)", "rgba(223,255,252,0.40)", CYAN, 9)
    m.text(168, 345, "A softer kind", 32, WHITE, True)
    m.text(168, 385, "of survival.", 32, WHITE, True)
    m.text(170, 425, "Handpicked worlds, reimagined.", 13, MUTED)
    m.text(170, 456, "2.4M installs  ·  Updated yesterday", 11, QUIET)
    m.button(168, 495, 174, 44, "EXPLORE PACK", CYAN, INK, 12)
    m.panel(1052, 226, 496, 350, 29, "rgba(149,168,199,0.17)")
    m.image(wallpaper("wp_13_frozen_glacier.jpg"), 1070, 244, 460, 191, 20)
    m.photo_dim(1070, 244, 460, 191, 20, 0.17)
    m.text(1084, 478, "WEEKLY SPOTLIGHT", 10, VIOLET, True, 1.2)
    m.text(1084, 510, "Quiet Horizons", 22, WHITE, True)
    m.text(1084, 538, "Built for exploration, not urgency.", 12, MUTED)
    m.text(1495, 541, "↗", 20, VIOLET, True)
    m.text(132, 623, "Browse by mood", 21, WHITE, True)
    m.text(1360, 623, "VIEW ALL CATEGORIES  →", 10, CYAN, True, 1)
    categories = [
        ("01", "Cozy evenings", "Warm light, softer edges", "wp_02_cherry_blossom.jpg", ROSE),
        ("02", "Big adventures", "A world with room to roam", "wp_18_crater_harbor.jpg", BLUE),
        ("03", "Built different", "Fresh systems and machines", "wp_05_nether_fortress.jpg", AMBER),
        ("04", "Lightweight play", "Smooth on every device", "wp_01_lush_caves.jpg", GREEN),
    ]
    for i, (n, name, detail, art, accent) in enumerate(categories):
        x = 132 + i * 356
        m.panel(x, 648, 336, 188, 24, "rgba(118,167,189,0.14)")
        m.image(wallpaper(art), x + 11, 659, 314, 108, 17)
        m.photo_dim(x + 11, 659, 314, 108, 17, 0.14)
        m.text(x + 20, 798, name, 16, WHITE, True)
        m.text(x + 20, 820, detail, 10, MUTED)
        m.circle(x + 304, 803, 10, f"rgba(143,230,235,0.25)")
        m.text(x + 300, 808, "›", 13, accent, True)
    return m.finish("04-discover.png")


def mod_details() -> Path:
    m = Mockup(wallpaper("wp_01_lush_caves.jpg"), "Mod details", "DISCOVER")
    m.title("MOD / DETAIL VIEW", "Make the world feel like yours", "A clear, considered install page with the information you actually need.")
    m.panel(132, 232, 892, 389, 30, "rgba(128,177,198,0.15)")
    m.image(wallpaper("wp_19_sinkhole_falls.jpg"), 148, 248, 860, 354, 24)
    m.photo_dim(148, 248, 860, 354, 24, 0.12)
    m.panel_outline(132, 232, 892, 389, 30)
    m.pill(170, 272, 141, 34, "MODRINTH  ·  FABRIC", "rgba(144,238,231,0.18)", "rgba(228,255,252,0.36)", CYAN, 9)
    m.text(170, 356, "Wildwood", 42, WHITE, True)
    m.text(172, 390, "An atmosphere worth getting lost in.", 14, MUTED)
    m.pill(170, 540, 129, 38, "1.21.4", "rgba(228,244,250,0.10)", "rgba(243,252,255,0.28)", WHITE, 11)
    m.pill(311, 540, 154, 38, "WORLD GENERATION", "rgba(228,244,250,0.10)", "rgba(243,252,255,0.28)", WHITE, 9)
    m.panel(1052, 232, 496, 389, 29, "rgba(138,176,198,0.17)")
    m.text(1086, 278, "PROJECT OVERVIEW", 10, CYAN, True, 1.3)
    m.text(1086, 320, "Wildwood", 27, WHITE, True)
    m.text(1086, 348, "by Team Meadow", 12, MUTED)
    m.text(1086, 385, "Explore lush clearings and deep caves,", 12, WHITE)
    m.text(1086, 405, "with quiet places worth finding.", 12, WHITE)
    m.pill(1086, 424, 108, 34, "★  4.9", "rgba(255,205,139,0.12)", "rgba(255,228,177,0.30)", AMBER, 10)
    m.pill(1205, 424, 132, 34, "2.4M DOWNLOADS", "rgba(228,244,250,0.07)", "rgba(243,252,255,0.20)", MUTED, 9)
    m.text(1086, 490, "SUPPORTED VERSIONS", 10, QUIET, True, 1)
    m.text(1086, 514, "1.20.1  ·  1.20.4  ·  1.21.4", 12, WHITE, True)
    m.button(1086, 548, 244, 44, "INSTALL TO LIBRARY", CYAN, INK, 12)
    m.pill(1343, 548, 174, 44, "ADD TO FAVORITES", "rgba(226,243,250,0.07)", "rgba(242,252,255,0.23)", WHITE, 10)
    m.panel(132, 650, 1416, 186, 27, "rgba(116,168,190,0.13)")
    m.text(168, 690, "Compatibility, in plain language", 18, WHITE, True)
    m.text(168, 720, "Every dependency is checked before install. Your existing worlds remain untouched.", 12, MUTED)
    facts = [("GAME VERSION", "1.20.1—1.21.4"), ("REQUIRES", "Fabric API"), ("LAST UPDATED", "2 days ago"), ("SOURCE", "Modrinth")]
    for i, (key, value) in enumerate(facts):
        x = 168 + i * 330
        m.text(x, 773, key, 9, QUIET, True, 1)
        m.text(x, 803, value, 14, CYAN if i == 0 else WHITE, True)
    return m.finish("05-mod-details.png")


def multiplayer() -> Path:
    m = Mockup(wallpaper("wp_17_night_clouds.jpg"), "Multiplayer", "SERVERS")
    m.title("YOUR PEOPLE ARE HERE", "A place for the next session", "Keep favorite servers close and join without breaking your flow.")
    m.panel(132, 229, 1416, 166, 30, "rgba(115,161,190,0.17)")
    m.image(wallpaper("wp_08_moonlit_lake.jpg"), 1070, 243, 459, 138, 20)
    m.photo_dim(1070, 243, 459, 138, 20, 0.20)
    m.text(170, 271, "QUICK CONNECT", 10, CYAN, True, 1.3)
    m.text(170, 318, "Join a world together.", 24, WHITE, True)
    m.text(170, 348, "Paste an address, or choose a familiar server below.", 12, MUTED)
    m.rect(170, 360, 605, 1, "rgba(237,250,255,0.20)")
    m.text(182, 383, "play.example.net", 12, WHITE)
    m.button(793, 344, 208, 42, "CONNECT", CYAN, INK, 12)
    m.panel(132, 417, 890, 419, 28, "rgba(126,172,195,0.14)")
    m.text(166, 461, "FAVORITE SERVERS", 11, CYAN, True, 1.2)
    m.pill(827, 434, 162, 38, "+  ADD SERVER", "rgba(144,238,231,0.16)", "rgba(230,255,253,0.35)", CYAN, 10)
    servers = [
        ("Lunar Grove", "play.lunargrove.net", "1.21.4", "34 ms", GREEN),
        ("The Orchard", "mc.theorchard.org", "1.21.3", "68 ms", CYAN),
        ("Cinder Realms", "play.cinderrealms.com", "1.20.4", "112 ms", AMBER),
        ("Cloud District", "join.clouddistrict.gg", "1.21.4", "—", VIOLET),
    ]
    for i, (name, address, version, ping, accent) in enumerate(servers):
        y = 493 + i * 78
        m.rect(161, y, 832, 66, "rgba(222,241,248,0.05)", "rgba(240,251,255,0.16)", 18, 1)
        m.circle(193, y + 32, 15, f"{accent}44", f"{accent}99", 1)
        m.text(188, y + 37, "S", 11, accent, True)
        m.text(224, y + 27, name, 14, WHITE, True)
        m.text(224, y + 47, address, 10, MUTED)
        m.pill(684, y + 18, 77, 30, version, "rgba(230,246,252,0.07)", "rgba(240,250,255,0.17)", MUTED, 9)
        m.circle(798, y + 32, 4, accent)
        m.text(811, y + 37, ping, 11, accent, True)
        m.text(954, y + 41, "›", 21, WHITE, True)
    m.panel(1052, 417, 496, 419, 28, "rgba(133,169,193,0.16)")
    m.text(1086, 461, "SERVER SIGNAL", 10, CYAN, True, 1.2)
    m.text(1086, 506, "Lunar Grove", 24, WHITE, True)
    m.text(1086, 533, "play.lunargrove.net", 12, MUTED)
    m.line(1086, 553, 1514, 553, "rgba(240,250,255,0.18)")
    m.circle(1101, 591, 5, GREEN)
    m.text(1118, 596, "ONLINE  ·  42 / 100 PLAYERS", 11, GREEN, True)
    m.text(1086, 633, "A friendly survival community with seasonal events.", 11, WHITE)
    m.text(1086, 651, "A place to build and play together.", 11, WHITE)
    m.pill(1086, 699, 134, 38, "SURVIVAL", "rgba(228,245,251,0.07)", "rgba(239,251,255,0.18)", MUTED, 9)
    m.pill(1232, 699, 138, 38, "COMMUNITY", "rgba(228,245,251,0.07)", "rgba(239,251,255,0.18)", MUTED, 9)
    m.button(1086, 762, 232, 44, "JOIN THIS SERVER", CYAN, INK, 12)
    return m.finish("06-multiplayer.png")


def settings() -> Path:
    m = Mockup(wallpaper("wp_20_sakura_sunbeams.jpg"), "Settings", "SETTINGS")
    m.title("MAKE IT YOURS", "A launcher that feels like home", "Thoughtful controls for appearance, performance, game files and your account.")
    m.panel(132, 232, 322, 606, 28, "rgba(118,166,191,0.15)")
    categories = ["Appearance", "Background & glass", "Game", "Performance", "Java runtime", "Storage", "Accounts", "About Aerix"]
    for i, label in enumerate(categories):
        y = 273 + i * 62
        selected = i == 1
        if selected:
            m.rect(151, y - 28, 284, 50, "rgba(142,240,232,0.16)", "rgba(226,255,252,0.34)", 19, 1)
        m.circle(177, y - 3, 8, "rgba(149,230,241,0.14)", "rgba(229,247,255,0.23)", 1)
        m.text(197, y + 2, label, 13, CYAN if selected else MUTED, selected)
        if selected:
            m.text(407, y + 3, "›", 17, CYAN, True)
    m.panel(480, 232, 1068, 606, 28, "rgba(139,177,199,0.16)")
    m.text(520, 279, "Background & glass", 24, WHITE, True)
    m.text(520, 309, "Set the atmosphere. The wallpaper stays sharp when blur is off.", 12, MUTED)
    m.line(520, 332, 1508, 332, "rgba(241,251,255,0.18)")
    settings_rows = [
        ("Wallpaper", "Moonlit lake", "Change the scene behind the glass"),
        ("Liquid glass", "On", "Reflections, soft edges and translucent layers"),
        ("Background blur", "0", "Keep the wallpaper crisp and interactions light"),
        ("Reduce motion", "Off", "Use short, restrained transitions"),
    ]
    for i, (name, value, detail) in enumerate(settings_rows):
        y = 379 + i * 92
        m.text(520, y, name, 14, WHITE, True)
        m.text(520, y + 24, detail, 11, MUTED)
        if i == 0:
            m.pill(1230, y - 18, 240, 42, value + "  ▾", "rgba(224,242,249,0.06)", "rgba(241,251,255,0.18)", WHITE, 12, False)
        elif i == 2:
            m.rect(1230, y - 2, 202, 5, "rgba(227,242,249,0.22)", radius=3)
            m.rect(1230, y - 2, 18, 5, CYAN, radius=3)
            m.circle(1248, y, 10, CYAN, "rgba(255,255,255,0.66)", 1)
            m.text(1454, y + 5, "0", 12, WHITE, True)
        else:
            on = i == 1
            m.rect(1409, y - 12, 61, 32, "rgba(143,239,231,0.37)" if on else "rgba(231,244,250,0.08)", "rgba(235,250,255,0.25)", 16, 1)
            m.circle(1450 if on else 1428, y + 4, 10, CYAN if on else MUTED)
            m.text(1372, y + 5, value, 10, CYAN if on else MUTED, True)
        if i < len(settings_rows) - 1:
            m.line(520, y + 49, 1508, y + 49, "rgba(237,249,255,0.13)")
    m.text(520, 763, "PREVIEW", 10, CYAN, True, 1.2)
    m.panel(520, 778, 988, 42, 18, "rgba(206,233,243,0.08)", "rgba(244,252,255,0.22)")
    m.text(541, 805, "Sharp wallpaper   ·   Translucent panes   ·   Cached reflections", 11, MUTED)
    m.text(1422, 805, "LIVE", 9, GREEN, True, 1)
    return m.finish("07-settings.png")


def wallpapers() -> Path:
    m = Mockup(wallpaper("wp_02_cherry_blossom.jpg"), "Wallpapers", "WALLPAPERS")
    m.title("SET THE ATMOSPHERE", "A little world behind the glass", "Choose a backdrop with enough room for the launcher to breathe.")
    m.panel(132, 228, 921, 507, 30, "rgba(115,161,188,0.17)")
    m.image(wallpaper("wp_02_cherry_blossom.jpg"), 148, 244, 889, 475, 25)
    m.photo_dim(148, 244, 889, 475, 25, 0.10)
    m.panel_outline(132, 228, 921, 507, 30)
    m.pill(177, 275, 147, 36, "CURRENT WALLPAPER", "rgba(12,24,36,0.48)", "rgba(243,252,255,0.34)", WHITE, 9)
    m.text(177, 676, "Cherry blossom valley", 23, WHITE, True)
    m.text(178, 702, "By the Aerix landscape collection", 12, WHITE)
    m.panel(1080, 228, 468, 507, 28, "rgba(137,175,197,0.17)")
    m.text(1114, 274, "GLASS & MOTION", 10, CYAN, True, 1.2)
    m.text(1114, 320, "Wallpaper opacity", 14, WHITE, True)
    m.rect(1114, 342, 364, 5, "rgba(226,242,249,0.20)", radius=3)
    m.rect(1114, 342, 284, 5, CYAN, radius=3)
    m.circle(1398, 344, 10, CYAN, "rgba(255,255,255,0.6)", 1)
    m.text(1487, 349, "78%", 11, WHITE, True)
    m.text(1114, 390, "Background blur", 14, WHITE, True)
    m.pill(1114, 406, 110, 38, "OFF", "rgba(143,238,231,0.18)", "rgba(229,255,252,0.35)", CYAN, 10)
    m.pill(1233, 406, 110, 38, "SUBTLE", "rgba(222,240,248,0.06)", "rgba(241,251,255,0.18)", MUTED, 10)
    m.pill(1352, 406, 150, 38, "SOFT FOCUS", "rgba(222,240,248,0.06)", "rgba(241,251,255,0.18)", MUTED, 10)
    m.text(1114, 485, "LIQUID GLASS PREVIEW", 10, QUIET, True, 1)
    m.panel(1114, 501, 400, 118, 23, "rgba(177,220,238,0.13)")
    m.text(1138, 542, "Reflected light, not heavy blur.", 13, WHITE, True)
    m.text(1138, 570, "Your wallpaper stays sharp at zero.", 11, MUTED)
    m.button(1114, 660, 192, 44, "APPLY WALLPAPER", CYAN, INK, 12)
    m.pill(1318, 660, 196, 44, "RESTORE DEFAULT", "rgba(225,242,249,0.06)", "rgba(241,251,255,0.20)", WHITE, 10)
    gallery = [
        ("Crystal river", "wp_04_crystal_river.jpg", False),
        ("Ocean arch", "wp_07_ocean_arch.jpg", False),
        ("Moonlit lake", "wp_08_moonlit_lake.jpg", False),
        ("Cherry bee", "wp_11_cherry_bee.jpg", True),
        ("Snow valley", "wp_12_snowy_valley.jpg", False),
    ]
    for i, (name, art, selected) in enumerate(gallery):
        x = 132 + i * 285
        m.panel(x, 760, 265, 96, 20, "rgba(117,164,187,0.13)", "rgba(230,250,255,0.30)" if selected else "rgba(241,251,255,0.18)")
        m.image(wallpaper(art), x + 9, 769, 247, 56, 13)
        m.text(x + 15, 846, name, 11, CYAN if selected else WHITE, True)
    return m.finish("08-wallpapers.png")


def accounts_skin() -> Path:
    m = Mockup(wallpaper("wp_14_cozy_village.jpg"), "Account & skin", "HOME")
    m.title("YOUR PRESENCE", "A familiar face in every world", "Manage linked accounts and make your player skin feel unmistakably yours.")
    m.panel(132, 230, 427, 606, 30, "rgba(127,172,196,0.16)")
    avatar = DRAWABLES / "img_avatar_entitybrian.png"
    m.image(avatar, 283, 263, 128, 128, 64)
    m.circle(418, 367, 10, GREEN, "rgba(255,255,255,0.70)", 1)
    m.text(167, 435, "MICROSOFT ACCOUNT", 10, CYAN, True, 1.3)
    m.text(167, 476, "EntityBrian69", 25, WHITE, True)
    m.text(167, 502, "Connected  ·  Verified", 12, GREEN, True)
    m.line(167, 527, 524, 527, "rgba(240,251,255,0.18)")
    m.text(167, 566, "PLAYER UUID", 9, QUIET, True, 1)
    m.text(167, 590, "72a4c13d  ·  918e  ·  4c6f", 11, WHITE)
    m.text(167, 630, "PROFILE VISIBILITY", 9, QUIET, True, 1)
    m.pill(167, 643, 151, 36, "PUBLIC  ·  LIVE", "rgba(145,239,231,0.13)", "rgba(222,255,252,0.30)", CYAN, 9)
    m.button(167, 703, 212, 44, "MANAGE ACCOUNTS", CYAN, INK, 11)
    m.pill(167, 764, 357, 42, "SIGN OUT SAFELY", "rgba(225,242,249,0.06)", "rgba(242,251,255,0.18)", MUTED, 10)
    m.panel(587, 230, 961, 606, 30, "rgba(126,172,198,0.16)")
    m.image(wallpaper("wp_09_flower_meadow.jpg"), 605, 248, 925, 339, 25)
    m.photo_dim(605, 248, 925, 339, 25, 0.19)
    m.panel_outline(587, 230, 961, 606, 30)
    m.pill(640, 277, 129, 34, "SKIN STUDIO", "rgba(9,20,33,0.42)", "rgba(241,251,255,0.32)", WHITE, 9)
    m.image(DRAWABLES / "img_avatar_fireplayz.png", 965, 312, 168, 168, 84)
    m.circle(1049, 403, 85, "none", "rgba(200,248,248,0.44)", 2)
    m.text(649, 530, "THE CRAFTSMAN", 24, WHITE, True)
    m.text(650, 557, "A classic silhouette with your own signature.", 12, MUTED)
    m.text(623, 632, "YOUR WARDROBE", 10, CYAN, True, 1.2)
    skins = [("Current", CYAN), ("Explorer", BLUE), ("Builder", VIOLET), ("Seasonal", ROSE)]
    for i, (label, accent) in enumerate(skins):
        x = 623 + i * 216
        m.panel(x, 649, 197, 119, 20, "rgba(228,245,250,0.055)", "rgba(155,244,236,0.50)" if i == 0 else "rgba(239,250,255,0.16)")
        m.circle(x + 40, 690, 22, f"{accent}55", f"{accent}99", 1)
        m.text(x + 76, 695, label, 12, CYAN if i == 0 else WHITE, True)
        m.text(x + 21, 746, "EDIT SKIN  →", 9, MUTED, True)
    m.button(1309, 778, 194, 42, "OPEN SKIN STUDIO", CYAN, INK, 10)
    return m.finish("09-account-skin.png")


def instance_detail() -> Path:
    m = Mockup(wallpaper("wp_12_snowy_valley.jpg"), "Instance overview", "LIBRARY")
    m.title("INSTANCE CONTROL", "Your world, from the inside out", "One place for launch settings, installed content, folders, logs and health.")
    m.panel(132, 232, 1416, 223, 30, "rgba(126,171,195,0.17)")
    m.image(wallpaper("wp_13_frozen_glacier.jpg"), 1002, 245, 530, 196, 23)
    m.photo_dim(1002, 245, 530, 196, 23, 0.23)
    m.panel_outline(132, 232, 1416, 223, 30)
    m.text(171, 274, "ACTIVE INSTANCE  /  FABRIC", 10, CYAN, True, 1.3)
    m.text(171, 326, "Sodium 1.20.1", 31, WHITE, True)
    m.text(172, 354, "A tuned, lightweight profile built for a smooth session.", 13, MUTED)
    m.pill(171, 378, 169, 42, "FABRIC  ·  32 MODS", "rgba(223,244,250,0.08)", "rgba(242,251,255,0.22)", WHITE, 10)
    m.button(358, 377, 176, 44, "LAUNCH", CYAN, INK, 12)
    m.pill(551, 377, 141, 44, "INSTANCE SETTINGS", "rgba(224,242,249,0.07)", "rgba(242,251,255,0.20)", WHITE, 9)
    m.pill(710, 377, 124, 44, "OPEN FOLDER", "rgba(224,242,249,0.07)", "rgba(242,251,255,0.20)", WHITE, 9)
    cards = [
        (132, "CONTENT", "32 mods", "Sodium · Iris · Lithium", CYAN, "VIEW MODS"),
        (418, "VISUALS", "4 packs", "Shader and resource profiles", BLUE, "MANAGE PACKS"),
        (704, "SESSION HEALTH", "All clear", "Runtime and files verified", GREEN, "RUN CHECK"),
    ]
    for x, title, number, detail, accent, action in cards:
        m.panel(x, 484, 268, 352, 25, "rgba(123,169,193,0.14)")
        m.text(x + 24, 527, title, 9, accent, True, 1.2)
        m.text(x + 24, 576, number, 24, WHITE, True)
        m.text(x + 24, 604, detail, 11, MUTED)
        if title == "CONTENT":
            mods = [("Sodium", "Performance"), ("Iris", "Shaders"), ("Lithium", "Optimization")]
            for i, (name, kind) in enumerate(mods):
                y = 650 + i * 43
                m.circle(x + 35, y - 5, 7, f"{accent}55", f"{accent}99", 1)
                m.text(x + 55, y, name, 11, WHITE, True)
                m.text(x + 186, y, kind, 9, QUIET)
        elif title == "VISUALS":
            for i, (name, art) in enumerate([("Lush shader", "wp_01_lush_caves.jpg"), ("Soft light", "wp_20_sakura_sunbeams.jpg")]):
                y = 646 + i * 55
                m.image(wallpaper(art), x + 22, y - 17, 72, 42, 10)
                m.text(x + 107, y, name, 10, WHITE, True)
        else:
            checks = [("Java runtime", "Ready"), ("Game files", "Verified"), ("Memory", "Optimized")]
            for i, (name, status) in enumerate(checks):
                y = 652 + i * 42
                m.circle(x + 34, y - 4, 5, GREEN)
                m.text(x + 49, y, name, 10, WHITE)
                m.text(x + 201, y, status, 9, GREEN, True)
        m.pill(x + 23, 788, 221, 34, action + "   →", "rgba(225,243,250,0.055)", "rgba(241,251,255,0.18)", accent, 9)
    m.panel(1010, 484, 538, 352, 26, "rgba(134,173,198,0.16)")
    m.text(1044, 527, "RUNTIME & PERFORMANCE", 10, CYAN, True, 1.2)
    m.text(1044, 573, "A balanced profile, ready for play.", 17, WHITE, True)
    m.text(1044, 601, "The launcher keeps resource controls visible without crowding your world.", 11, MUTED)
    metrics = [("JAVA", "21"), ("MEMORY", "4096 MB"), ("RENDERER", "LTW")]
    for i, (label, value) in enumerate(metrics):
        x = 1044 + i * 155
        m.text(x, 661, label, 9, QUIET, True, 1)
        m.text(x, 691, value, 13, WHITE, True)
    m.rect(1044, 714, 470, 1, "rgba(241,251,255,0.18)")
    m.text(1044, 750, "LAST SESSION", 9, QUIET, True, 1)
    m.text(1044, 777, "Today  ·  2h 18m", 12, WHITE, True)
    m.text(1044, 810, "OPEN LOGS  →", 9, CYAN, True, 1)
    return m.finish("10-instance-overview.png")


def main() -> None:
    OUTPUT.mkdir(parents=True, exist_ok=True)
    generators = [home, library, create_instance, discover, mod_details, multiplayer, settings, wallpapers, accounts_skin, instance_detail]
    for generator in generators:
        out = generator()
        print(out.relative_to(ROOT))


if __name__ == "__main__":
    main()
