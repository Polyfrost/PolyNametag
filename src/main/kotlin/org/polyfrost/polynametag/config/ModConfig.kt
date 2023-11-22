package org.polyfrost.polynametag.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import org.polyfrost.polynametag.PolyNametag

object ModConfig : Config(Mod(PolyNametag.NAME, ModType.UTIL_QOL, "/polynametag.svg"), "${PolyNametag.MODID}.json") {

    @Slider(name = "Height offset", min = -0.5f, max = 0.5f)
    var heightOffset = 0f

    @Switch(name = "Text shadow")
    var textShadow = false

    @Switch(name = "Show own nametag")
    var showOwnNametag = true

    @Switch(name = "Background")
    var background = true

    @Color(name = "Background color")
    var backgroundColor = OneColor(0, 0, 0, 63)

    init {
        initialize()
        addDependency("backgroundColor", "background")
    }
}