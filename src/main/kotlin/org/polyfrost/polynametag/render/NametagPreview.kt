package org.polyfrost.polynametag.render

import cc.polyfrost.oneconfig.config.elements.BasicOption
import cc.polyfrost.oneconfig.gui.OneConfigGui
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.platform.Platform
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import kotlin.math.atan
import net.minecraft.client.renderer.GlStateManager as GL

object NametagPreview : BasicOption(null, null, "Nametag Preview", "", "", "", 2) {
    var renderPreview = false
    var x = 0
    var y = 0

    override fun getHeight() = 696 - (30 + 4 * (32 + 16))

    override fun draw(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        this.x = x
        this.y = y
        renderPreview = true
    }

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun renderPreview(event: GuiScreenEvent.DrawScreenEvent.Post) {
        if (!renderPreview) return
        if (mc.currentScreen !is OneConfigGui) return
        val player = mc.thePlayer ?: return
        val unscaleMC = 1 / UResolution.scaleFactor
        val scale = OneConfigGui.getScaleFactor() * (mc.currentScreen as OneConfigGui).animationScaleFactor
        val x = ((UResolution.windowWidth - 800 * scale) / 2f).toInt()
        val y = ((UResolution.windowHeight - 768 * scale) / 2f).toInt()
        val mouseX = (Platform.getMousePlatform().mouseX.toFloat()) / scale
        val mouseY = (Platform.getMousePlatform().mouseY.toFloat()) / scale

        GL.pushMatrix()
        GL.scale(unscaleMC, unscaleMC, 1.0)
        GL.scale(scale, scale, 1f)
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        GL11.glScissor(x, y, (1024 * scale).toInt(), (696 * scale).toInt())
        drawEntityPointingMouse(
            entity = player,
            x = this.x - 16 + 512,
            y = this.y + 450,
            scale = 150f,
            mouseX = mouseX,
            mouseY = mouseY
        )
        GL.popMatrix()
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        renderPreview = false
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
        renderManager.doRenderEntity(entity, 0.0, 0.0, 0.0, 0f, 1f, true)
        renderManager.isRenderShadow = true

        entity.renderYawOffset = tempRYO
        entity.rotationYaw = tempRY
        entity.rotationPitch = tempRP
        entity.prevRotationYawHead = tempPRYH
        entity.rotationYawHead = tempRYN

        GL.popMatrix()
        RenderHelper.disableStandardItemLighting()
        GL.disableRescaleNormal()
        GL.setActiveTexture(OpenGlHelper.lightmapTexUnit)
        GL.disableTexture2D()
        GL.setActiveTexture(OpenGlHelper.defaultTexUnit)
    }
}

