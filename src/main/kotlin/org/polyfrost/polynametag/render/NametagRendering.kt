package org.polyfrost.polynametag.render

import cc.polyfrost.oneconfig.utils.dsl.mc
import club.sk1er.patcher.config.PatcherConfig
import gg.essential.Essential
import gg.essential.config.EssentialConfig
import gg.essential.connectionmanager.common.enums.ProfileStatus
import gg.essential.data.OnboardingData
import gg.essential.handlers.OnlineIndicator
import gg.essential.universal.UMatrixStack
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import org.polyfrost.polynametag.PolyNametag
import org.polyfrost.polynametag.config.ModConfig
import org.polyfrost.polynametag.mixin.FontRendererAccessor
import java.awt.Color
import java.util.regex.Pattern
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import net.minecraft.client.renderer.GlStateManager as GL

internal fun shouldDrawBackground() =
    ModConfig.background && (!PolyNametag.isPatcher || !PatcherConfig.disableNametagBoxes)

val NO_COLOR_FLOAT = floatArrayOf(0f, 0f, 0f, 0f)
fun getBackBackgroundGLColorOrEmpty(): FloatArray =
    if (shouldDrawBackground()) with(ModConfig.backgroundColor) {
        floatArrayOf(red / 255f, green / 255f, blue / 255f, alpha.coerceAtMost(0x3F) / 255f)
    } else {
        NO_COLOR_FLOAT
    }

fun drawFrontBackground(text: String, entity: Entity) {
    drawFrontBackground(text, ModConfig.backgroundColor.toJavaColor(), entity)
}

fun drawFrontBackground(text: String, color: Color, entity: Entity) {
    val halfWidth = mc.fontRendererObj.getStringWidth(text) / 2 + 1.0
    drawBackground(-halfWidth, halfWidth, color, entity)
}

data class Vec2(val x: Int, val y: Int)

val points = listOf(Vec2(1, 1), Vec2(1, -1), Vec2(-1, -1), Vec2(-1, 1))
val translate = listOf(Vec2(1, 0), Vec2(0, -1), Vec2(-1, 0), Vec2(0, 1))

fun drawFrontBackground(xStart: Double, xEnd: Double, color: Color, entity: Entity) {
    drawBackground(xStart, xEnd, color, entity)
}

fun drawFrontBackground(xStart: Double, xEnd: Double, entity: Entity) {
    drawBackground(xStart, xEnd, ModConfig.backgroundColor.toJavaColor(), entity)
}

fun drawBackground(xStart: Double, xEnd: Double, color: Color, entity: Entity) {
    if (!ModConfig.enabled) return
    if (!shouldDrawBackground()) return
    val realStart = xStart - if (PolyNametag.drawEssential) 10 else 0
    GL11.glEnable(GL11.GL_LINE_SMOOTH)
    GlStateManager.disableTexture2D()
    GL11.glPushMatrix()
    GL11.glTranslated((realStart + xEnd) / 2f, 3.5, 0.01)
    GL11.glBegin(GL11.GL_TRIANGLE_FAN)
    with(color) {
        GL11.glColor4f(red / 255f, green / 255f, blue / 255f, alpha / 255f)
    }

    val radius = if (ModConfig.rounded) ModConfig.cornerRadius.coerceAtMost(4.5f + ModConfig.paddingY) else 0f

    val width = (xEnd - realStart) / 2f + ModConfig.paddingX - radius

    val distanceFromPlayer = entity.getDistanceToEntity(mc.thePlayer)
    val quality = ((distanceFromPlayer * 4 + 10).coerceAtMost(350f) / 4).toInt()

    for (a in 0..3) {
        val (transX, transY) = translate[a]
        val x = points[a].x * width
        val y = points[a].y * (4.5 + ModConfig.paddingY - radius)
        if (ModConfig.rounded) {
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

fun drawIndicator(entity: Entity, string: String) {
    OnlineIndicator.drawNametagIndicator(UMatrixStack(), entity, string, 0)
}

fun Entity.canDrawIndicator(): Boolean {
    if (!PolyNametag.isEssential) return false
    if (OnboardingData.hasAcceptedTos() && EssentialConfig.showEssentialIndicatorOnNametag && this is EntityPlayer) {
        if (Essential.getInstance().connectionManager.profileManager.getStatus(this.gameProfile.id) != ProfileStatus.OFFLINE) {
            return true
        }
    }
    return false
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
