package org.polyfrost.polynametag.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import club.sk1er.patcher.config.PatcherConfig
import org.polyfrost.polynametag.PolyNametag
import kotlin.reflect.jvm.javaField

object ModConfig : Config(Mod(PolyNametag.NAME, ModType.UTIL_QOL, "/polynametag.svg"), "${PolyNametag.MODID}.json") {

    @Slider(name = "Height offset", min = -0.5f, max = 0.5f, description = "How much to offset the nametag vertically")
    var heightOffset = 0f

    @Slider(name = "Scale", min = 0f, max = 1f, description = "How much to scale the nametag")
    var scale = 1f

    @Switch(name = "Text shadow", description = "Whether to render a shadow behind the nametag")
    var textShadow = false

    @Switch(name = "Show own nametag", description = "Whether to show your own nametag")
    var showOwnNametag = true

    @Switch(name = "Background", description = "Whether to render a background behind the nametag")
    var background = true

    @Color(name = "Background color", description = "The color of the background")
    var backgroundColor = OneColor(0, 0, 0, 63)

    init {
        initialize()
        addDependency("backgroundColor", "background")
        addDependency("background", "Patcher's Disable Nametag Boxes. Please turn it off to use this feature.") {
            !PolyNametag.isPatcher || !PatcherConfig.disableNametagBoxes
        }
        addDependency("showOwnNametag", "Patcher's Show Own Nametag. Please turn it off to use this feature.") {
            !PolyNametag.isPatcher || !PatcherConfig.showOwnNametag
        }
    }
}