package org.polyfrost.polynametag.render

import cc.polyfrost.oneconfig.utils.dsl.mc
import club.sk1er.patcher.config.PatcherConfig
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.polyfrost.polynametag.PolyNametag
import org.polyfrost.polynametag.config.ModConfig
import org.polyfrost.polynametag.mixin.FontRendererAccessor
import kotlin.math.max
import net.minecraft.client.renderer.GlStateManager as GL

internal fun shouldDrawBackground() = ModConfig.background && (!PolyNametag.isPatcher || !PatcherConfig.disableNametagBoxes)

val NO_COLOR_FLOAT = floatArrayOf(0f, 0f, 0f, 0f)
fun getBackBackgroundGLColorOrEmpty(): FloatArray =
    if (shouldDrawBackground()) with(ModConfig.backgroundColor) {
        floatArrayOf(red / 255f, green / 255f, blue / 255f, alpha.coerceAtMost(0x20) / 255f)
    } else {
        NO_COLOR_FLOAT
    }

val NO_COLOR_INT = intArrayOf(0, 0, 0, 0)
fun getBackBackgroundColorOrEmpty(): IntArray =
    if (shouldDrawBackground()) with(ModConfig.backgroundColor) {
        intArrayOf(red, green, blue, alpha.coerceAtMost(0x20))
    } else {
        NO_COLOR_INT
    }

fun drawFrontBackgroundForText(text: String) {
    drawBackground(mc.fontRendererObj.getStringWidth(text) / 2, maxAlpha = 0xFF)
}

private fun drawBackground(textHalfWidth: Int, maxAlpha: Int = 255) {
    val halfWidth = textHalfWidth + 1.0
    drawBackground(-halfWidth, halfWidth, maxAlpha)
}

fun drawBackground(xStart: Double, xEnd: Double, maxAlpha: Int = 255) {
    if (!ModConfig.enabled) return
    if (!shouldDrawBackground()) return

    GL.disableTexture2D()

    with(ModConfig.backgroundColor) {
        GL.color(red / 255f, green / 255f, blue / 255f, alpha.coerceAtMost(maxAlpha) / 255f)
    }

    val tessellator = Tessellator.getInstance()

    with(tessellator.worldRenderer) {
        begin(7, DefaultVertexFormats.POSITION)
        pos(xStart, -1.0, 0.01).endVertex()
        pos(xStart, 8.0, 0.01).endVertex()
        pos(xEnd, 8.0, 0.01).endVertex()
        pos(xEnd, -1.0, 0.01).endVertex()
    }

    tessellator.draw()

    GL.enableTexture2D()
}

internal fun FontRenderer.drawStringWithoutZFighting(text: String, x: Int, y: Float, color: Int): Int {
    if (this !is FontRendererAccessor) return -1
    GL.pushMatrix()
    GL.translate(0f, 0f, -0.01f)

    if (!ModConfig.textShadow) {
        val i = drawString(text, x.toFloat(), y, color, false)
        GL.popMatrix()
        return i
    }

    GL.enableAlpha()
    invokeResetStyles()

    val shadowX = invokeRenderString(text, x + 1f, y + 1f, color, true)
    GL.translate(0f, 0f, -0.01f)
    val normalX = drawString(text, x.toFloat(), y, color, false)
    GL.popMatrix()

    return max(shadowX, normalX)
}
