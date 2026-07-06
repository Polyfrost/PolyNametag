package org.polyfrost.polynametag.client

import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.Color
import org.polyfrost.oneconfig.api.config.v1.annotations.Dropdown
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.polynametag.PolyNametagConstants

object PolyNametagConfig :
    Config("nametag.json", "/assets/polynametag/polynametag_dark.svg", PolyNametagConstants.NAME, Category.QOL) {
    @JvmStatic
    @Switch(title = "Enabled")
    var isEnabled = true

    @JvmStatic
    @Switch(title = "Remove nametags", description = "Removes all nametags.")
    var isRemoveNametags = false

    @JvmStatic
    @Slider(title = "Height offset", min = -10F, max = 10F, step = 0.25F, description = "How much to offset the nametag vertically")
    var heightOffset = 0.0F
        get() = field.coerceIn(-10F, 10F)

    @JvmStatic
    @Slider(title = "Scale", min = 0.0F, max = 1.0F, step = 0.05F, description = "How much to scale the nametag")
    var scale = 1.0F
        get() = field.coerceIn(0.0F, 1.0F)

    @Dropdown(
        title = "Text Type",
        options = ["No Shadow", "Shadow"],
        description = "The type of shadow to render"
    )
    var textType = 0
        get() = field.coerceIn(0, 1)

    @JvmStatic
    @Switch(title = "Show own nametag", description = "Whether to show your own nametag")
    var isShowOwnNametag = true

    @JvmStatic
    @Switch(title = "Show in inventory")
    var isShowInInventory = false

    @JvmStatic
    @Switch(title = "Hide entity nametags when HUD hidden", description = "Hide non-player, non-armor stand nametags while the HUD is hidden (F1)")
    var isHideEntityNametagsInHiddenHud = true

    @JvmStatic
    @Switch(title = "Hide player nametags when HUD hidden", description = "Hide player nametags while the HUD is hidden (F1)")
    var isHidePlayerNametagsInHiddenHud = true

    @JvmStatic
    @Switch(title = "Hide armor stand nametags when HUD hidden", description = "Hide armor stand nametags while the HUD is hidden (F1)")
    var isHideArmorStandNametagsInHiddenHud = true

    @Switch(title = "Background", description = "Whether to render a background behind the nametag")
    var background = true

    @Color(title = "Background color", description = "The color of the background")
    var backgroundColor = PolyColor(0x3F000000)

    @Color(title = "Text color", description = "The color of the text")
    var textColor = PolyColor(0xFFFFFFFF.toInt())

    @Switch(title = "Override text color", description = "Force the text color above, ignoring color codes and team/rank colors in the name")
    var overrideTextColor = false

    @Switch(title = "Rounded Corners", description = "Round the corners of the background")
    var rounded = false

    @Slider(title = "Corner Radius", min = 0.0F, max = 10.0F, description = "The radius of the rounded corners", step = 1f)
    var cornerRadius = 3.0F
        get() = field.coerceIn(0.0F, 10.0F)

    @Slider(title = "Padding X", min = 0.0F, max = 10.0F, description = "Horizontal padding around the text", step = 1f)
    var paddingX = 0.0F
        get() = field.coerceIn(0.0F, 10.0F)

    @Slider(title = "Padding Y", min = 0.0F, max = 10.0F, description = "Vertical padding around the text", step = 1f)
    var paddingY = 0.0F
        get() = field.coerceIn(0.0F, 10.0F)

//    private var hasMigratedPatcher = false

    init {
        addDependency("backgroundColor", "background")
//        addDependency("background", "Patcher's Disable Nametag Boxes. Please turn it off to use this feature.") {
//            if (PolyNametagClient.isPatcher && PatcherConfig.disableNametagBoxes) {
//                Property.Display.DISABLED
//            } else {
//                Property.Display.SHOWN
//            }
//        }
//        addDependency("isShowOwnNametag", "Patcher's Show Own Nametag. Please turn it off to use this feature.") {
//            if (PolyNametagClient.isPatcher && PatcherConfig.showOwnNametag) {
//                Property.Display.DISABLED
//            } else {
//                Property.Display.SHOWN
//            }
//        }
        addDependency("isShowInInventory", "isShowOwnNametag")

        addDependency("rounded", "background")
        addDependency("cornerRadius", "rounded")
        addDependency("paddingX", "background")
        addDependency("paddingY", "background")

//        if (!hasMigratedPatcher) {
//            try {
//                Class.forName("club.sk1er.patcher.config.OldPatcherConfig")
//                var didAnything = false
//                if (OldPatcherConfig.shadowedNametagText) {
//                    textType = 1
//                    didAnything = true
//                }
//                if (OldPatcherConfig.disableNametagBoxes) {
//                    background = false
//                    didAnything = true
//                }
//                if (OldPatcherConfig.showOwnNametag) {
//                    isShowOwnNametag = true
//                    didAnything = true
//                }
//
//                hasMigratedPatcher = true
//                save()
//
//                if (didAnything) {
//                    Notifications.enqueue(
//                        Notifications.Type.Info,
//                        "PolyNametag",
//                        "Migrated Patcher settings replaced by PolyNametag. Please check PolyNametag's settings to make sure they are correct."
//                    )
//                }
//            } catch (_: ClassNotFoundException) {
//
//            }
//        }
    }
}
