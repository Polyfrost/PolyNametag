package org.polyfrost.polynametag

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RendererLivingEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.client.event.RenderWorldEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.polyfrost.polynametag.config.ModConfig
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

    var labels: MutableList<LabelInfo> = ArrayList()
    var names: MutableList<NameInfo> = ArrayList()

    var drawing = false
    var drawingInGUI = false
    var drawEssential = false

    @SubscribeEvent
    fun startWorld(event: RenderWorldEvent.Pre) {
        drawingInGUI = false
    }

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        drawingInGUI = true
        if (!ModConfig.enabled) return
        if (names.isEmpty() && labels.isEmpty()) return
        GlStateManager.pushMatrix()
        drawing = true
        for (i in names) {
            drawEssential = i.entity.canDrawIndicator()
            i.instance.renderName(i.entity, i.x, i.y, i.z)
            drawEssential = false
        }
        for (i in labels) {
            (i.instance as RenderAccessor<Entity>).renderNametag(i.entity, i.str, i.x, i.y, i.z, i.maxDistance)
        }
        drawing = false
        names.clear()
        labels.clear()
        GlStateManager.popMatrix()
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.disableBlend()
    }

}