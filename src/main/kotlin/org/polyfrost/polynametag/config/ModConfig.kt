package org.polyfrost.polynametag.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.ConfigUtils
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.config.elements.BasicOption
import cc.polyfrost.oneconfig.config.elements.OptionPage
import club.sk1er.patcher.config.PatcherConfig
import org.polyfrost.polynametag.PolyNametag
import org.polyfrost.polynametag.render.NametagPreview
import java.lang.reflect.Field
import kotlin.math.min

object ModConfig : Config(Mod("Nametags", ModType.UTIL_QOL, "/polynametag.svg"), "${PolyNametag.MODID}.json") {

    @Slider(name = "Height offset", min = -0.5f, max = 0.5f, description = "How much to offset the nametag vertically")
    var heightOffset = 0f
        get() = field.coerceIn(-0.5f, 0.5f)

    @Slider(name = "Scale", min = 0f, max = 1f, description = "How much to scale the nametag")
    var scale = 1f
        get() = field.coerceIn(0f, 1f)

    @Slider(name = "X-Padding", min = 0f, max = 10f, description = "The horizontal padding of the background")
    var paddingX = 0f

    @Slider(name = "Y-Padding", min = 0f, max = 10f, description = "The vertical padding of the background")
    var paddingY = 0f

    @Slider(name = "Corner Radius", min = 0f, max = 10f, description = "The corner radius of the background")
    var radius = 0f
        get() = field.coerceIn(0f, 4.5f + min(paddingX, paddingY))

    @Dropdown(name = "Text Type", options = ["No Shadow", "Shadow", "Full Shadow"], description = "The type of shadow to render")
    var textType = 0

    @Info(
        type = InfoType.WARNING,
        text = "Using Full Shadow may cause performance issues on low-end devices"
    )
    var info1 = 0

    @Switch(name = "Show own nametag", description = "Whether to show your own nametag")
    var showOwnNametag = true

    @Switch(name = "Background", description = "Whether to render a background behind the nametag")
    var background = true

    @Color(name = "Background color", description = "The color of the background")
    var backgroundColor = OneColor(0, 0, 0, 63)

    @CustomOption
    @Transient
    private val nametagPreview = NametagPreview(category = "General")

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

    override fun getCustomOption(
        field: Field,
        annotation: CustomOption,
        page: OptionPage,
        mod: Mod,
        migrate: Boolean,
    ): BasicOption = nametagPreview.also {
        ConfigUtils.getSubCategory(page, it.category, it.subcategory).options.add(it)
    }
}