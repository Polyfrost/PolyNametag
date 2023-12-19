package org.polyfrost.polynametag.render

import cc.polyfrost.oneconfig.config.elements.BasicOption
import cc.polyfrost.oneconfig.gui.OneConfigGui
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.entity.RendererLivingEntity
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import kotlin.math.atan
import net.minecraft.client.renderer.GlStateManager as GL

class NametagPreview(
    description: String = "",
    category: String = "",
    subcategory: String = "",
) : BasicOption(null, null, "Nametag Preview", description, category, subcategory, 2) {
    private data class DrawContext(val x: Int, val y: Int, val mouseX: Float, val mouseY: Float)
    private var drawContext: DrawContext? = null

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    override fun getHeight() = 696 - (30 + 4 * (32 + 16))

    override fun draw(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        drawContext = DrawContext(x, y, inputHandler.mouseX(), inputHandler.mouseY())
    }

    @SubscribeEvent
    fun renderPreview(event: GuiScreenEvent.DrawScreenEvent.Post) {
        val (oneUIX, oneUIY, mouseX, mouseY) = drawContext ?: return
        drawContext = null
        val oneConfigGui = mc.currentScreen as? OneConfigGui ?: return
        val player = mc.thePlayer ?: return
        val unscaleMC = 1 / UResolution.scaleFactor
        val oneUIScale = OneConfigGui.getScaleFactor() * oneConfigGui.animationScaleFactor
        val rawX = ((UResolution.windowWidth - 800 * oneUIScale) / 2f).toInt()
        val rawY = ((UResolution.windowHeight - 768 * oneUIScale) / 2f).toInt()

        GL.pushMatrix()
        GL.scale(unscaleMC * oneUIScale, unscaleMC * oneUIScale, 1.0)
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        GL11.glScissor(rawX, rawY, (1024 * oneUIScale).toInt(), (696 * oneUIScale).toInt())
        drawEntityPointingMouse(
            entity = player,
            x = oneUIX - 16 + 512,
            y = oneUIY + 450,
            scale = 150f,
            mouseX = mouseX,
            mouseY = mouseY
        )
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        GL.popMatrix()
    }

    fun drawEntityPointingMouse(
        entity: EntityLivingBase,
        x: Int,
        y: Int,
        scale: Float,
        mouseX: Float,
        mouseY: Float,
    ) {
        val dx = x - mouseX
        val dy = y - entity.eyeHeight * scale - mouseY

        GL.enableDepth()
        GL.color(1f, 1f, 1f, 1f)
        GL.enableColorMaterial()
        GL.pushMatrix()
        GL.translate(x.toFloat(), y.toFloat(), 50f)
        GL.scale(-scale, scale, scale)
        GL.rotate(180f, 0f, 0f, 1f)

        val tempRYO = entity.renderYawOffset
        val tempRY = entity.rotationYaw
        val tempRP = entity.rotationPitch
        val tempPRYH = entity.prevRotationYawHead
        val tempRYN = entity.rotationYawHead
        val tempRBE = entity.riddenByEntity

        GL.rotate(135f, 0f, 1f, 0f)
        RenderHelper.enableStandardItemLighting()
        GL.rotate(-135f, 0f, 1f, 0f)

        entity.rotationYaw = atan(dx / 40f) * 40f
        entity.renderYawOffset = entity.rotationYaw
        entity.rotationYawHead = entity.rotationYaw
        entity.prevRotationYawHead = entity.rotationYaw
        entity.rotationPitch = -atan(dy / 40f) * 20f

        val renderManager = mc.renderManager
        renderManager.playerViewX = 0f
        renderManager.playerViewY = 180f
        renderManager.isRenderShadow = false
        val renderer = renderManager.getEntityRenderObject<EntityLivingBase>(entity) as RendererLivingEntity
        renderer.renderName(entity, 0.0, 0.0, 0.0)
        entity.riddenByEntity = entity // cancel original nametag
        renderManager.doRenderEntity(entity, 0.0, 0.0, 0.0, 0f, 1f, true)
        renderManager.isRenderShadow = true

        entity.renderYawOffset = tempRYO
        entity.rotationYaw = tempRY
        entity.rotationPitch = tempRP
        entity.prevRotationYawHead = tempPRYH
        entity.rotationYawHead = tempRYN
        entity.riddenByEntity = tempRBE

        GL.popMatrix()
        RenderHelper.disableStandardItemLighting()
        GL.disableRescaleNormal()
        GL.setActiveTexture(OpenGlHelper.lightmapTexUnit)
        GL.disableTexture2D()
        GL.setActiveTexture(OpenGlHelper.defaultTexUnit)
    }
}
