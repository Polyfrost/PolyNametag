package org.polyfrost.polynametag.hooks

import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RendererLivingEntity
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11
import org.polyfrost.polynametag.config.ModConfig
import org.polyfrost.polynametag.mixin.RenderAccessor
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

fun nametagTransformation() {
    GlStateManager.translate(0f, ModConfig.heightOffset, 0f)
    GlStateManager.scale(ModConfig.scale, ModConfig.scale, ModConfig.scale)
}

private const val SCALE_VALUE = 0.02666667f
private const val NAME_TAG_RANGE = 64.0
private const val NAME_TAG_RANGE_SNEAK = 32.0

fun callback(renderer: Any, entity: Entity, x: Double, y: Double, z: Double, callbackInfo: CallbackInfo) {
    if (!ModConfig.enabled) return

    callbackInfo.cancel()
}

private val Entity.scaledHeight get() = if (this is EntityLivingBase && isChild) height * 0.5f else height

fun renderNametag(renderer: RendererLivingEntity<*>, entity: EntityLivingBase, x: Double, y: Double, z: Double) {
    if (!canRenderName(renderer, entity)) return

    val displayName: String = entity.displayName.formattedText

    var y2 = y + entity.scaledHeight + 0.5

    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f)

    if (entity.isSneaking) {
        renderSneaking(renderer, entity, x, y, z, displayName)
    } else {
        renderNormal(renderer, entity, x, y, z, displayName)
    }
}

fun canRenderName(renderer: Render<*>, entity: Entity) =
    (renderer as RenderAccessor).invokeCanRenderName(entity) && entity.isWithinDistance(
        entity = renderer.renderManager.livingPlayer,
        distance = if (entity.isSneaking) NAME_TAG_RANGE_SNEAK else NAME_TAG_RANGE
    )

fun Entity.isWithinDistance(entity: Entity, distance: Double) = getDistanceSqToEntity(entity) < distance * distance

fun renderSneaking(renderer: Render<*>, entity: EntityLivingBase, x: Double, y: Double, z: Double, displayName: String) {
    val fontRenderer: FontRenderer = renderer.fontRendererFromRenderManager
    GlStateManager.pushMatrix()
    GlStateManager.translate(x, y + entity.scaledHeight + 0.5, z)
    GlStateManager.translate(0.0, -0.25, 0.0)
    GL11.glNormal3f(0.0f, 1.0f, 0.0f)
    GlStateManager.rotate(-renderer.renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
    GlStateManager.rotate(renderer.renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
    GlStateManager.scale(-SCALE_VALUE, -SCALE_VALUE, SCALE_VALUE)
    GlStateManager.disableLighting()
    GlStateManager.depthMask(false)
    GlStateManager.enableBlend()
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
    GlStateManager.disableTexture2D()
    drawBackground(fontRenderer.getStringWidth(displayName))
    GlStateManager.enableTexture2D()
    GlStateManager.depthMask(true)
    fontRenderer.drawString(displayName, -fontRenderer.getStringWidth(displayName) / 2, 0, 0x20FFFFFF)
    GlStateManager.enableLighting()
    GlStateManager.disableBlend()
    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    GlStateManager.popMatrix()
}

fun renderNormal(renderer: Render<*>, entity: Entity, x: Double, y: Double, z: Double, displayName: String) {
    val distance: Double = entity.getDistanceSqToEntity(renderer.renderManager.livingPlayer)
    if (distance > 64 * 64) return // redundant check

    val fontRenderer: FontRenderer = renderer.fontRendererFromRenderManager
    GlStateManager.pushMatrix()
    GlStateManager.translate(x, y + entity.scaledHeight + 0.5, z)
    GL11.glNormal3f(0.0f, 1.0f, 0.0f)
    GlStateManager.rotate(-renderer.renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
    GlStateManager.rotate(renderer.renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
    GlStateManager.scale(-SCALE_VALUE, -SCALE_VALUE, SCALE_VALUE)
    GlStateManager.disableLighting()
    GlStateManager.depthMask(false)
    GlStateManager.disableDepth() // difference
    GlStateManager.enableBlend()
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
    GlStateManager.disableTexture2D()
    drawBackground(fontRenderer.getStringWidth(displayName)) // no depth as same in vanilla
    GlStateManager.enableTexture2D()
    fontRenderer.drawString(displayName, -fontRenderer.getStringWidth(displayName) / 2, 0, 0x20FFFFFF) // transparent in blocks
    GlStateManager.enableDepth() // difference
    GlStateManager.depthMask(true)
    fontRenderer.drawString(displayName, -fontRenderer.getStringWidth(displayName) / 2, 0, 0xFFFFFFFF.toInt())
    GlStateManager.enableLighting()
    GlStateManager.disableBlend()
    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    GlStateManager.popMatrix()
}

fun drawBackground(textWidth: Int) {
    GlStateManager.color(0.0f, 0.0f, 0.0f, 0.25f)

    val halfWidth = textWidth / 2.0 + 1.0
    val tessellator = Tessellator.getInstance()

    with(tessellator.worldRenderer) {
        begin(7, DefaultVertexFormats.POSITION)
        pos(-halfWidth, -1.0, 0.0).endVertex()
        pos(-halfWidth, 8.0, 0.0).endVertex()
        pos(halfWidth, 8.0, 0.0).endVertex()
        pos(halfWidth, -1.0, 0.0).endVertex()
    }

    tessellator.draw()
}
