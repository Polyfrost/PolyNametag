package org.polyfrost.polynametag.render

import club.sk1er.patcher.config.PatcherConfig
import gg.essential.Essential
import gg.essential.config.EssentialConfig
import gg.essential.connectionmanager.common.enums.ProfileStatus
import gg.essential.data.OnboardingData
import gg.essential.handlers.OnlineIndicator
import gg.essential.universal.UMatrixStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import org.polyfrost.polynametag.NametagRenderer
import org.polyfrost.polynametag.PolyNametag
import org.polyfrost.polynametag.PolyNametag.drawingIndicator
import org.polyfrost.polynametag.PolyNametagConfig
import org.polyfrost.polynametag.mixin.FontRendererAccessor
import kotlin.math.cos
import kotlin.math.sin

var drawingText = false

var drawingWithDepth = false

private val mc by lazy { Minecraft.getMinecraft() }

internal fun shouldDrawBackground() =
    PolyNametagConfig.background && (!PolyNametag.isPatcher || !PatcherConfig.disableNametagBoxes)
fun getBackBackgroundAlpha(): Int = if (shouldDrawBackground()) PolyNametagConfig.backgroundColor.a.coerceAtMost(63) else 0

fun drawFrontBackground(text: String, entity: Entity) {
    drawFrontBackground(text, PolyNametagConfig.backgroundColor.r, PolyNametagConfig.backgroundColor.g, PolyNametagConfig.backgroundColor.b, PolyNametagConfig.backgroundColor.a, entity)
}

fun drawFrontBackground(text: String, red: Int, green: Int, blue: Int, alpha: Int, entity: Entity) {
    val halfWidth = mc.fontRendererObj.getStringWidth(text) / 2 + 1.0
    drawBackground(-halfWidth, halfWidth, red, green, blue, alpha, entity)
}

data class Vec2(val x: Int, val y: Int)

val points = listOf(Vec2(1, 1), Vec2(1, -1), Vec2(-1, -1), Vec2(-1, 1))
val translate = listOf(Vec2(1, 0), Vec2(0, -1), Vec2(-1, 0), Vec2(0, 1))

fun drawFrontBackground(xStart: Double, xEnd: Double, red: Int, green: Int, blue: Int, alpha: Int, entity: Entity) {
    drawBackground(xStart, xEnd, red, green, blue, alpha, entity)
}

fun drawFrontBackground(xStart: Double, xEnd: Double, entity: Entity) {
    drawBackground(xStart, xEnd, PolyNametagConfig.backgroundColor.r, PolyNametagConfig.backgroundColor.g, PolyNametagConfig.backgroundColor.b, PolyNametagConfig.backgroundColor.a, entity)
}

fun drawBackground(xStart: Double, xEnd: Double, red: Int, green: Int, blue: Int, alpha: Int, entity: Entity) {
    if (!PolyNametagConfig.enabled) return
    if (!shouldDrawBackground()) return
    val realStart = xStart - if (NametagRenderer.isDrawingIndicator) 10 else 0
    GL11.glEnable(GL11.GL_LINE_SMOOTH)
    GlStateManager.disableTexture2D()
    GL11.glPushMatrix()
    GL11.glTranslated((realStart + xEnd) / 2f, 3.5, 0.01)
    GL11.glBegin(GL11.GL_TRIANGLE_FAN)
    val a = alpha.coerceAtMost(63)
    val realAlpha = if (drawingWithDepth) (alpha - a) / (255 - a).toFloat() else alpha / 255f
    GL11.glColor4f(red / 255f, green / 255f, blue / 255f, realAlpha)
    drawingWithDepth = false

    val halfWidth = (xEnd - realStart) / 2f + PolyNametagConfig.paddingX

    val radius = if (PolyNametagConfig.rounded) PolyNametagConfig.cornerRadius.coerceAtMost(4.5f + PolyNametagConfig.paddingY).coerceAtMost(halfWidth.toFloat()) else 0f

    val width = halfWidth - radius

    val distanceFromPlayer = entity.getDistanceToEntity(mc.thePlayer)
    val quality = ((distanceFromPlayer * 4 + 10).coerceAtMost(350f) / 4).toInt()

    for (a in 0..3) {
        val (transX, transY) = translate[a]
        val x = points[a].x * width
        val y = points[a].y * (4.5 + PolyNametagConfig.paddingY - radius)
        if (PolyNametagConfig.rounded) {
            for (b in 0 until 90 / quality) {
                val radian = Math.toRadians((a * 90 + b * quality).toDouble())
                GL11.glVertex2d(x + sin(radian) * radius, y + cos(radian) * radius)
            }
            GL11.glVertex2d(x + transX * radius, y + transY * radius)
        } else {
            GL11.glVertex2d(x, y)
        }
    }

    GL11.glEnd()
    GL11.glPopMatrix()
    GlStateManager.enableTexture2D();
    GL11.glColor4f(1f, 1f, 1f, 1f)
    GL11.glDisable(GL11.GL_LINE_SMOOTH)
}

internal fun FontRenderer.drawStringWithoutZFighting(text: String, x: Float, y: Float, color: Int): Int {
    if (this !is FontRendererAccessor) return 0
    GlStateManager.pushMatrix()
    GlStateManager.translate(0f, 0f, -0.01f)
    drawingText = true
    return when (PolyNametagConfig.textType) {
        0 -> drawString(text, x, y, color, false)
        1 -> drawString(text, x, y, color, true)
       // 2 -> TextRenderer.drawBorderedText(text, x, y, color, color.getAlpha())
        // fixme
        else -> 0
    }.apply {
        drawingText = false
        GlStateManager.popMatrix()
    }
}
