package org.polyfrost.polynametag.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import org.polyfrost.polynametag.PolyNametag

object ModConfig : Config(Mod(PolyNametag.NAME, ModType.UTIL_QOL), "${PolyNametag.MODID}.json") {

    @Info(
        text = "Some features may have conflict with Patcher",
        type = InfoType.WARNING,
        size = 2
    )
    private var warning = false

    @Slider(name = "Height offset", min = -0.5f, max = 0.5f)
    var heightOffset = 0f

    @Slider(name = "Scale", min = 0.5f, max = 2f)
    var scale = 1f

    @Switch(name = "Text shadow")
    var textShadow = false

    @Switch(name = "Show self nametag")
    var showSelfNametag = true

    @Switch(name = "Background")
    var background = true

    @Color(name = "Background color")
    var backgroundColor = OneColor(0f, 0f, 0f, 0.25f)

    init {
        initialize()
        addDependency("backgroundColor", "background")
    }
}