/*
 * Zalith Launcher 2
 * Copyright (C) 2026 Aerix Launcher contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.movtery.zalithlauncher.game.renderer

import com.movtery.zalithlauncher.game.renderer.renderers.LTWRenderer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LTWRendererTest {
    @Test
    fun rendererIsRegisteredAsBuiltIn() {
        Renderers.init(reset = true)

        assertTrue(Renderers.getRenderers().contains(LTWRenderer))
    }

    @Test
    fun rendererUsesItsBundledOpenGlAndEglLibrary() {
        assertEquals("opengles3_ltw", LTWRenderer.getRendererId())
        assertEquals("libltw.so", LTWRenderer.getRendererLibrary())
        assertEquals("libltw.so", LTWRenderer.getRendererEGL())
        val env = LTWRenderer.getRendererEnv().value
        assertEquals("3", env["LIBGL_ES"])
        assertEquals("1", env["LIBGL_NOERROR"])
        assertEquals("true", env["force_glsl_extensions_warn"])
        assertEquals("true", env["allow_higher_compat_version"])
        assertEquals("true", env["allow_glsl_extension_directive_midshader"])
    }

    @Test
    fun rendererAdvertisesMinecraftVersionsStartingAtOneSeventeen() {
        assertEquals("1.17", LTWRenderer.getMinMCVersion())
        assertEquals(null, LTWRenderer.getMaxMCVersion())
        assertEquals(3, LTWRenderer.getMinimumGlesVersion())
    }
}
