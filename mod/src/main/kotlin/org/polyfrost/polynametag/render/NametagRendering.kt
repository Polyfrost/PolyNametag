package org.polyfrost.polynametag.render

import cc.polyfrost.oneconfig.utils.dsl.mc
import club.sk1er.patcher.config.PatcherConfig
import gg.essential.handlers.OnlineIndicator
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11
import org.polyfrost.polynametag.PolyNametag
import org.polyfrost.polynametag.config.ModConfig
import org.polyfrost.polynametag.mixin.FontRendererAccessor
import kotlin.math.max
import net.minecraft.client.renderer.GlStateManager as GL

private val isEssential by lazy {
    try {
        Class.forName("gg.essential.Essential")
        true
    } catch (e: ClassNotFoundException) {
        false
    }
}

private const val NAMETAG_SCALE = 0.02666667f

fun initNametagRendering() {
    isEssential
}

fun overrideNametag(entity: Entity, displayName: String, x: Double, y: Double, z: Double, range: Int): Boolean {
    if (!ModConfig.enabled) return false
    if (!entity.isWithinRange(mc.renderManager.livingPlayer, range)) return false
    renderNametag(entity, displayName, x, y, z)
    return true
}

fun renderNametag(entity: Entity, displayName: String, x: Double, y: Double, z: Double) {
    val fontRenderer = mc.fontRendererObj
    val textHalfWidth = fontRenderer.getStringWidth(displayName) / 2
    val sneaking = entity.isSneaking
    var yAboveHead = y + entity.height + 0.5 + ModConfig.heightOffset
    if (sneaking) yAboveHead -= 0.25
    val scale = NAMETAG_SCALE * ModConfig.scale
    val checkPerspective = if (mc.gameSettings.thirdPersonView == 2) -1 else 1

    GL11.glNormal3f(0f, 1f, 0f)
    GL.pushMatrix()
    GL.translate(x, yAboveHead, z)
    GL.rotate(-mc.renderManager.playerViewY, 0f, 1f, 0f)
    GL.rotate(mc.renderManager.playerViewX * checkPerspective, 1f, 0f, 0f)
    GL.scale(-scale, -scale, scale)
    GL.disableLighting()

    if (!sneaking) {
        GL.disableDepth()
    }

    GL.enableBlend()
    GL.tryBlendFuncSeparate(770, 771, 1, 0)
    if (sneaking) {
        drawBackground(textHalfWidth, maxAlpha = 0x20)
        fontRenderer.drawStringWithoutZFighting(displayName, -textHalfWidth, 0f, 0x20FFFFFF)
    } else {
        GL.enableDepth()
        drawBackground(textHalfWidth)
        fontRenderer.drawStringWithoutZFighting(displayName, -textHalfWidth, 0f, 0xFFFFFFFF.toInt())
    }

    if (isEssential) {
        if (OnlineIndicator.nametagEntity != null) {
            OnlineIndicator.drawNametagIndicator(gg.essential.universal.UMatrixStack(), entity, displayName, 0)
        }
    }

    GL.enableLighting()
    GL.disableBlend()
    GL.color(1f, 1f, 1f, 1f)
    GL.popMatrix()

    if (isEssential) {
        OnlineIndicator.nametagEntity = null
    }
}

internal fun shouldDrawBackground() = ModConfig.background && (!PolyNametag.isPatcher || !PatcherConfig.disableNametagBoxes)

private fun drawBackground(textHalfWidth: Int, maxAlpha: Int = 255) {
    if (!shouldDrawBackground()) return

    GL.disableTexture2D()

    with(ModConfig.backgroundColor) {
        GL.color(red / 255f, green / 255f, blue / 255f, alpha.coerceAtMost(maxAlpha) / 255f)
    }

    val halfWidth = textHalfWidth + 1.0
    val tessellator = Tessellator.getInstance()

    with(tessellator.worldRenderer) {
        begin(7, DefaultVertexFormats.POSITION)
        pos(-halfWidth, -1.0, 0.01).endVertex()
        pos(-halfWidth, 8.0, 0.01).endVertex()
        pos(halfWidth, 8.0, 0.01).endVertex()
        pos(halfWidth, -1.0, 0.01).endVertex()
    }

    tessellator.draw()

    GL.enableTexture2D()
}

internal fun FontRenderer.drawStringWithoutZFighting(text: String, x: Int, y: Float, color: Int): Int {
    if (this !is FontRendererAccessor) return -1
    if (!ModConfig.textShadow) return drawString(text, x.toFloat(), y, color, false)

    GL.enableAlpha()
    invokeResetStyles()

    GL.pushMatrix()
    val shadowX = invokeRenderString(text, x + 1f, y + 1f, color, true)
    GL.translate(0f, 0f, -0.01f)
    val normalX = drawString(text, x.toFloat(), y, color, false)
    GL.popMatrix()

    return max(shadowX, normalX)
}

private fun Entity.isWithinRange(entity: Entity, range: Int) = getDistanceSqToEntity(entity) < range * range