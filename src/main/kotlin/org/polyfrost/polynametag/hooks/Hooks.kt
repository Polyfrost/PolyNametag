package org.polyfrost.polynametag.hooks

import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RendererLivingEntity
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import org.lwjgl.opengl.GL11
import org.polyfrost.polynametag.config.ModConfig
import org.polyfrost.polynametag.mixin.RenderAccessor
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

fun nametagTransformation() {
    GlStateManager.translate(0f, ModConfig.heightOffset, 0f)
    GlStateManager.scale(ModConfig.scale, ModConfig.scale, ModConfig.scale)
}

private const val NAME_TAG_RANGE = 64.0
private const val NAME_TAG_RANGE_SNEAK = 32.0

fun callback(renderer: RenderAccessor, entity: Entity, x: Double, y: Double, z: Double, callbackInfo: CallbackInfo) {
    if (!ModConfig.enabled) return

    callbackInfo.cancel()
}

fun renderNametag(renderer: RendererLivingEntity<EntityLiving>, entity: EntityLiving, x: Double, y: Double, z: Double) {
    if (!canRenderName(renderer, entity)) return

    val displayName: String = entity.displayName.formattedText
    GlStateManager.alphaFunc(516, 0.1f)
    if (entity.isSneaking) {
        renderSneaking(renderer, entity, x, y, z, displayName)
    } else {
        renderNormal(renderer, entity, x, y - if (entity.isChild) (entity.height / 2.0f).toDouble() else 0.0, z, displayName)
    }
}

fun canRenderName(renderer: Render<out Entity>, entity: Entity) =
    (renderer as RenderAccessor).invokeCanRenderName(entity) && entity.isWithinDistance(
        entity = renderer.renderManager.livingPlayer,
        distance = if (entity.isSneaking) NAME_TAG_RANGE_SNEAK else NAME_TAG_RANGE
    )

fun Entity.isWithinDistance(entity: Entity, distance: Double) = getDistanceSqToEntity(entity) < distance * distance

fun renderSneaking(renderer: RendererLivingEntity<EntityLiving>, entity: EntityLiving, x: Double, y: Double, z: Double, displayName: String) {
    val fontrenderer: FontRenderer = renderer.fontRendererFromRenderManager
    GlStateManager.pushMatrix()
    GlStateManager.translate(x.toFloat(), y.toFloat() + entity.height + 0.5f - if (entity.isChild) entity.height / 2.0f else 0.0f, z.toFloat())
    GL11.glNormal3f(0.0f, 1.0f, 0.0f)
    GlStateManager.rotate(-renderer.renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
    GlStateManager.rotate(renderer.renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
    GlStateManager.scale(-0.02666667f, -0.02666667f, 0.02666667f)
    GlStateManager.translate(0.0f, 9.374999f, 0.0f)
    GlStateManager.disableLighting()
    GlStateManager.depthMask(false)
    GlStateManager.enableBlend()
    GlStateManager.disableTexture2D()
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
    drawBackground(fontrenderer.getStringWidth(displayName))
    GlStateManager.enableTexture2D()
    GlStateManager.depthMask(true)
    fontrenderer.drawString(displayName, -fontrenderer.getStringWidth(displayName) / 2, 0, 553648127)
    GlStateManager.enableLighting()
    GlStateManager.disableBlend()
    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    GlStateManager.popMatrix()
}

fun renderNormal(renderer: RendererLivingEntity<EntityLiving>, entity: EntityLiving, x: Double, y: Double, z: Double, displayName: String) {
    val distance: Double = entity.getDistanceSqToEntity(renderer.renderManager.livingPlayer)
    if (distance > 64 * 64) return // redundant check

    val fontRenderer: FontRenderer = renderer.fontRendererFromRenderManager
    val f = 1.6f
    val g = 0.016666668f * f
    GlStateManager.pushMatrix()
    GlStateManager.translate(x.toFloat() + 0.0f, y.toFloat() + entity.height + 0.5f, z.toFloat())
    GL11.glNormal3f(0.0f, 1.0f, 0.0f)
    GlStateManager.rotate(-renderer.renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
    GlStateManager.rotate(renderer.renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
    GlStateManager.scale(-g, -g, g)
    GlStateManager.disableLighting()
    GlStateManager.depthMask(false)
    GlStateManager.disableDepth()
    GlStateManager.enableBlend()
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
    val tessellator = Tessellator.getInstance()
    val worldRenderer = tessellator.worldRenderer
    var i = 0
    if (displayName == "deadmau5") {
        i = -10
    }
    val j = fontRenderer.getStringWidth(displayName) / 2
    GlStateManager.disableTexture2D()
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR)
    worldRenderer.pos((-j - 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
    worldRenderer.pos((-j - 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
    worldRenderer.pos((j + 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
    worldRenderer.pos((j + 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
    tessellator.draw()
    GlStateManager.enableTexture2D()
    fontRenderer.drawString(displayName, -fontRenderer.getStringWidth(displayName) / 2, i, 553648127)
    GlStateManager.enableDepth()
    GlStateManager.depthMask(true)
    fontRenderer.drawString(displayName, -fontRenderer.getStringWidth(displayName) / 2, i, -1)
    GlStateManager.enableLighting()
    GlStateManager.disableBlend()
    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    GlStateManager.popMatrix()
}

fun drawBackground(textWidth: Int) {
    val halfWidth = textWidth / 2.0 + 1.0
    val tessellator = Tessellator.getInstance()

    with(tessellator.worldRenderer) {
        begin(7, DefaultVertexFormats.POSITION_COLOR)
        pos(-halfWidth, -1.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        pos(-halfWidth, 8.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        pos(halfWidth, 8.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        pos(halfWidth, -1.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
    }

    tessellator.draw()
}
