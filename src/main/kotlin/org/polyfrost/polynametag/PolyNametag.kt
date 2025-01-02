package org.polyfrost.polynametag

//#if FORGE
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
//#else
//$$ import net.fabricmc.api.ClientModInitializer
//#endif

import org.polyfrost.oneconfig.api.platform.v1.Platform

//#if FORGE
@Mod(modid = PolyNametag.ID, version = PolyNametag.VERSION, name = PolyNametag.NAME, modLanguageAdapter = "org.polyfrost.oneconfig.utils.v1.forge.KotlinLanguageAdapter")
//#endif
object PolyNametag
    //#if FABRIC
    //$$ : ClientModInitializer
    //#endif
{

    const val ID = "@MOD_ID@"
    const val NAME = "@MOD_NAME@"
    const val VERSION = "@MOD_VERSION@"

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
