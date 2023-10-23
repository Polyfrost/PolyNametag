package org.polyfrost.polynametag

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.polyfrost.polynametag.config.ModConfig

@Mod(modid = PolyNametag.MODID, name = PolyNametag.NAME, version = PolyNametag.VERSION, modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter")
object PolyNametag {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    @Mod.EventHandler
    fun onInit(e: FMLInitializationEvent) {
        ModConfig
    }
}