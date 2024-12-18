package org.polyfrost.polynametag

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RendererLivingEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import org.polyfrost.oneconfig.api.platform.v1.Platform
import org.polyfrost.polynametag.mixin.MinecraftAccessor
import org.polyfrost.polynametag.mixin.RenderAccessor

//#if FORGE
@Mod(modid = PolyNametag.ID, version = PolyNametag.VERSION, name = PolyNametag.NAME, modLanguageAdapter = "org.polyfrost.oneconfig.utils.v1.forge.KotlinLanguageAdapter")
//#endif
object PolyNametag
    //#if FABRIC
    //$$ : ClientModInitializer
    //#endif
{

    const val ID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    var isPatcher = false
        private set

    var isEssential = false

    var drawingIndicator = false

    fun initialize() {
        PolyNametagConfig
    }

    fun postInitialize() {
        val loaderPlatform = Platform.loader()
        isEssential = loaderPlatform.isModLoaded("essential") && !loaderPlatform.isModLoaded("notsoessential")
        isPatcher = loaderPlatform.isModLoaded("patcher")
    }

    //#if FORGE
    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        initialize()
    }

    @Mod.EventHandler
    fun onPostInit(event: FMLPostInitializationEvent) {
        postInitialize()
    }
    //#else
    //$$ override fun onInitializeClient() {
    //$$     initialize()
    //$$     postInitialize()
    //$$ }
    //#endif

}
