package org.polyfrost.polynametag.render

import cc.polyfrost.oneconfig.utils.dsl.mc
import club.sk1er.patcher.config.PatcherConfig
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import org.polyfrost.polynametag.PolyNametag
import org.polyfrost.polynametag.config.ModConfig
import org.polyfrost.polynametag.mixin.FontRendererAccessor
import java.util.regex.Pattern
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
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

fun drawBackground(text: String, maxAlpha: Int = 255) {
    drawBackground(mc.fontRendererObj.getStringWidth(text) / 2, maxAlpha)
}

private fun drawBackground(textHalfWidth: Int, maxAlpha: Int = 255) {
    val halfWidth = textHalfWidth + 1.0 + ModConfig.paddingX
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
        begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION)
        for (i in 1..360) {
            val radius = ModConfig.radius
            val quadrants = i / 90
            val centerX = when (quadrants) {
                0, 1 -> xEnd - radius
                2, 3 -> xStart + radius
                else -> 0.0
            }
            val centerY = when (quadrants) {
                0, 3 -> 8.0 + ModConfig.paddingY - radius
                1, 2 -> -1.0 - ModConfig.paddingY + radius
                else -> 0.0
            }
            val x = sin(Math.toRadians(i.toDouble())) * radius
            val y = cos(Math.toRadians(i.toDouble())) * radius
            pos(centerX + x, centerY + y, 0.0).endVertex()
        }
    }

    tessellator.draw()

    GL.enableTexture2D()
}

private val regex = Pattern.compile("(?i)\u00A7[0-9a-f]")
var isDrawingBorder = false
fun FontRenderer.drawBorderedText(text: String, x: Float, y: Float, opacity: Int): Int {
    if (this !is FontRendererAccessor) return -1
    val noColors = regex.matcher(text).replaceAll("\u00A7r")
    var yes = 0
    if (opacity / 4 > 3) {
        for (xOff in -2..2) {
            for (yOff in -2..2) {
                if (xOff * xOff != yOff * yOff) {
                    yes = max(
                        invokeRenderString(
                            noColors, xOff / 2f + x, yOff / 2f + y, opacity / 4 shl 24, false
                        ), yes
                    )
                }
            }
        }
    }
    return yes
}

internal fun FontRenderer.drawStringWithoutZFighting(text: String, x: Int, y: Float, color: Int): Int {
    if (this !is FontRendererAccessor) return -1
    GL.pushMatrix()
    GL.translate(0f, 0f, -0.01f)

    when (ModConfig.textType) {
        0 -> {
            val i = drawString(text, x.toFloat(), y, color, false)
            GL.popMatrix()
            return i
        }
        1 -> {
            GL.enableAlpha()
            invokeResetStyles()

            val shadowX = invokeRenderString(text, x + 1f, y + 1f, color, true)
            GL.translate(0f, 0f, -0.01f)
            val normalX = drawString(text, x.toFloat(), y, color, false)
            GL.popMatrix()

            return max(shadowX, normalX)
        }
        2 -> {
            GL.enableAlpha()
            invokeResetStyles()

            isDrawingBorder = true

            val shadowX = drawBorderedText(text, x.toFloat(), y, 255)
            GL.translate(0f, 0f, -0.01f)
            val normalX = drawString(text, x.toFloat(), y, color, false)

            isDrawingBorder = false
            GL.popMatrix()

            return max(shadowX, normalX)
        }
    }
    return -1
}
