package org.polyfrost.polynametag

import gg.essential.Essential
import gg.essential.config.EssentialConfig
import gg.essential.connectionmanager.common.enums.ProfileStatus
import gg.essential.data.OnboardingData
import gg.essential.handlers.OnlineIndicator
import gg.essential.universal.UMatrixStack
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RendererLivingEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polynametag.PolyNametag.drawingIndicator
import org.polyfrost.polynametag.mixin.MinecraftAccessor
import org.polyfrost.polynametag.mixin.RenderAccessor

object NametagRenderer {

    interface NametagItem {

        val entity: Entity

    }

    data class LabelItem(val instance: Render<Entity>, override val entity: Entity, val str: String, val x: Double, val y: Double, val z: Double, val maxDistance: Int): NametagItem

    data class NameItem(val instance: RendererLivingEntity<EntityLivingBase>, override val entity: EntityLivingBase, val x: Double, val y: Double, val z: Double): NametagItem

    @JvmStatic
    val nametags = mutableListOf<NametagItem>()

    @JvmStatic
    var isCurrentlyDrawingWorld = false

    @JvmStatic
    var isCurrentlyDrawingInventory = false

    @JvmStatic
    var isCurrentlyDrawingPlayerName = false

    @JvmStatic
    var isDrawingIndicator = false

    @JvmStatic
    var isCurrentlyDrawingTags = false
        private set

    @JvmStatic
    fun renderAll() {
//        if (!PolyNametagConfig.enabled) {
//            println("Nametags are disabled")
//            return
//        }

        if (nametags.isEmpty()) {
            println("No nametags to render")
            return
        }

        println("Rendering ${nametags.size} nametags:\n${nametags.joinToString("\n")}")

        GlStateManager.pushMatrix()
        isCurrentlyDrawingTags = true
        mc.entityRenderer.enableLightmap()
        val partialTicks = (mc as MinecraftAccessor).timer.renderPartialTicks
        for (nametag in nametags) {
            val brightness = if (nametag.entity.isBurning) 15728880 else nametag.entity.getBrightnessForRender(partialTicks)
            val x = brightness % 65536
            val y = brightness / 65536

            OpenGlHelper.setLightmapTextureCoords(
                OpenGlHelper.lightmapTexUnit,
                x.toFloat() / 1.0f,
                y.toFloat() / 1.0f
            )

            when (nametag) {
                is LabelItem -> {
                    @Suppress("UNCHECKED_CAST")
                    (nametag.instance as RenderAccessor<Entity>).renderNametag(
                        nametag.entity,
                        nametag.str,
                        nametag.x,
                        nametag.y,
                        nametag.z,
                        nametag.maxDistance
                    )
                }

                is NameItem -> {
                    @Suppress("UNCHECKED_CAST")
                    nametag.instance.renderName(nametag.entity, nametag.x, nametag.y, nametag.z)
                }
            }
        }

        nametags.clear()
        isCurrentlyDrawingTags = false
        mc.entityRenderer.disableLightmap()
        GlStateManager.popMatrix()
    }

    @JvmStatic
    fun drawEssentialIndicator(entity: Entity, string: String) {
        if (entity !is EntityPlayer) {
            return
        }

        drawingIndicator = true
        OnlineIndicator.drawNametagIndicator(UMatrixStack(), entity, string, 0)
        drawingIndicator = false
    }

    @JvmStatic
    fun canDrawEssentialIndicator(entity: Entity): Boolean {
        if (!PolyNametag.isEssential) {
            return false
        }

        if (OnboardingData.hasAcceptedTos() && EssentialConfig.showEssentialIndicatorOnNametag && entity is EntityPlayer) {
            if (Essential.getInstance().connectionManager.profileManager.getStatus(entity.gameProfile.id) != ProfileStatus.OFFLINE) {
                return true
            }
        }

        return false
    }

}
