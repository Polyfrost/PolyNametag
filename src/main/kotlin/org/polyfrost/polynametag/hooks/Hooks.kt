package org.polyfrost.polynametag.hooks

import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11
import org.polyfrost.polynametag.config.ModConfig
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import kotlin.math.max


private const val NAMETAG_SCALE = 0.02666667f

fun overrideNametag(renderer: Any, entity: Entity, displayName: String, x: Double, y: Double, z: Double, range: Int, callbackInfo: CallbackInfo) {
    if (!ModConfig.enabled) return
    if (renderer !is Render<*>) return

    if (!entity.isWithinRange(renderer.renderManager.livingPlayer, range)) return

    renderNametag(renderer, entity, displayName, x, y, z)
    callbackInfo.cancel()
}

private fun Entity.isWithinRange(entity: Entity, range: Int) = getDistanceSqToEntity(entity) < range * range

private fun renderNametag(renderer: Render<*>, entity: Entity, displayName: String, x: Double, y: Double, z: Double) {
    val fontRenderer = renderer.fontRendererFromRenderManager
    val textHalfWidth = fontRenderer.getStringWidth(displayName) / 2f
    val sneaking = entity.isSneaking
    var yAboveHead = y + entity.height + 0.5 + ModConfig.heightOffset
    if (sneaking) yAboveHead -= 0.25
    val scale = NAMETAG_SCALE * ModConfig.scale
    val checkPerspective = if (mc.gameSettings.thirdPersonView == 2) -1 else 1
    GL11.glNormal3f(0f, 1f, 0f)
    GlStateManager.pushMatrix()
    GlStateManager.translate(x, yAboveHead, z)
    GlStateManager.rotate(-renderer.renderManager.playerViewY, 0f, 1f, 0f)
    GlStateManager.rotate(renderer.renderManager.playerViewX * checkPerspective, 1f, 0f, 0f)
    GlStateManager.scale(-scale, -scale, scale)
    GlStateManager.disableLighting()

    if (!sneaking) {
        GlStateManager.disableDepth()
    }

    GlStateManager.enableBlend()
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
    drawBackground(textHalfWidth)
    fontRenderer.drawStringWithoutZFighting(displayName, -textHalfWidth, 0f, 0x20FFFFFF)

    if (!sneaking) {
        GlStateManager.enableDepth()
        fontRenderer.drawStringWithoutZFighting(displayName, -textHalfWidth, 0f, 0xFFFFFFFF.toInt())
    }

    GlStateManager.enableLighting()
    GlStateManager.disableBlend()
    GlStateManager.color(1f, 1f, 1f, 1f)
    GlStateManager.popMatrix()
}

private fun drawBackground(textHalfWidth: Float) {
    if (!ModConfig.background) return

    GlStateManager.disableTexture2D()

    with(ModConfig.backgroundColor) {
        GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f)
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

    GlStateManager.enableTexture2D()
}

private fun FontRenderer.drawStringWithoutZFighting(text: String, x: Float, y: Float, color: Int): Int {
    if (!ModConfig.textShadow) return drawString(text, x, y, color, false)
    GlStateManager.pushMatrix()
    val shadowX = drawString(text, x + 1f, y + 1f, shadowColor(color), false)
    GlStateManager.translate(0f, 0f, -0.01f)
    val normalX = drawString(text, x, y, color, false)
    GlStateManager.popMatrix()
    return max(shadowX, normalX)
}

private fun shadowColor(color: Int): Int {
    val shiftedAlpha = color and 0xFF000000.toInt()
    val rgbCapped = color and 0xFCFCFC
    val quarterRGB = rgbCapped shr 2
    return quarterRGB or shiftedAlpha
}
