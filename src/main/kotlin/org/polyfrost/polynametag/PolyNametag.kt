package org.polyfrost.polynametag

import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RendererLivingEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import org.polyfrost.polynametag.config.ModConfig
import org.polyfrost.polynametag.mixin.MinecraftAccessor
import org.polyfrost.polynametag.mixin.RenderAccessor
import org.polyfrost.polynametag.render.canDrawIndicator

@Mod(modid = PolyNametag.MODID, name = PolyNametag.NAME, version = PolyNametag.VERSION, modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter")
object PolyNametag {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"
    var isPatcher = false
        private set
    var isEssential = false
    var drawingEssential = false

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        ModConfig
        MinecraftForge.EVENT_BUS.register(this)
    }

    @Mod.EventHandler
    fun onPostInit(event: FMLPostInitializationEvent) {
        isEssential = Loader.isModLoaded("essential")
        isPatcher = Loader.isModLoaded("patcher")
    }

    data class LabelInfo(val instance: Render<Entity>, val entity: Entity, val str: String, val x: Double, val y: Double, val z: Double, val maxDistance: Int)
    data class NameInfo(val instance: RendererLivingEntity<EntityLivingBase>, val entity: EntityLivingBase, val x: Double, val y: Double, val z: Double)

    var nametags = ArrayList<Any>()

    var drawing = false
    var drawingWorld = false
    var drawingInventory = false
    var drawEssential = false

    fun onRender() {
        if (!ModConfig.enabled) return
        if (nametags.isEmpty()) return
        GlStateManager.pushMatrix()
        drawing = true
        mc.entityRenderer.enableLightmap()
        val partialTicks = (mc as MinecraftAccessor).timer.renderPartialTicks
        for (name in nametags) {
            with(name) {
                if (this is LabelInfo) {
                    val i = if (entity.isBurning) 15728880 else entity.getBrightnessForRender(partialTicks)
                    val j = i % 65536
                    val k = i / 65536
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j.toFloat() / 1.0f, k.toFloat() / 1.0f)
                    (instance as RenderAccessor<Entity>).renderNametag(entity, str, x, y, z, maxDistance)
                } else if (this is NameInfo) {
                    val i = if (entity.isBurning) 15728880 else entity.getBrightnessForRender(partialTicks)
                    val j = i % 65536
                    val k = i / 65536
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j.toFloat() / 1.0f, k.toFloat() / 1.0f)
                    drawEssential = entity.canDrawIndicator()
                    instance.renderName(entity, x, y, z)
                    drawEssential = false
                }
            }

        }
        nametags.clear()
        drawing = false
        mc.entityRenderer.disableLightmap()
        GlStateManager.popMatrix()
    }

}