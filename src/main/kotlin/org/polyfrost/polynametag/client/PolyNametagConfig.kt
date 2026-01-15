package org.polyfrost.polynametag.client

import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.Color
import org.polyfrost.oneconfig.api.config.v1.annotations.Dropdown
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.polynametag.PolyNametagConstants
import org.polyfrost.polyui.color.rgba

object PolyNametagConfig :
    Config("nametag.json", "/assets/polynametag/polynametag_dark.svg", PolyNametagConstants.NAME, Category.QOL) {
    @JvmStatic
    @Switch(title = "Enabled")
    var isEnabled = true

    @JvmStatic
    @Slider(title = "Height offset", min = -10F, max = 10F, description = "How much to offset the nametag vertically")
    var heightOffset = 0.0F
        get() = field.coerceIn(-10F, 10F)

    @JvmStatic
    @Slider(title = "Scale", min = 0.0F, max = 1.0F, description = "How much to scale the nametag")
    var scale = 1.0F
        get() = field.coerceIn(0.0F, 1.0F)

    @Switch(title = "Rounded Corners")
    var rounded = false

    @Slider(title = "Corner Radius", min = 0.0F, max = 10.0F)
    var cornerRadius = 0.0F

    @Slider(title = "Padding X", min = 0.0F, max = 10.0F)
    var paddingX = 0.0F

    @Slider(title = "Padding Y", min = 0.0F, max = 10.0F)
    var paddingY = 0.0F

    @Dropdown(
        title = "Text Type",
        options = ["No Shadow", "Shadow", "Full Shadow"],
        description = "The type of shadow to render"
    )
    var textType = 0

    @JvmStatic
    @Switch(title = "Show own nametag", description = "Whether to show your own nametag")
    var isShowOwnNametag = true

    @JvmStatic
    @Switch(title = "Show in inventory")
    var isShowInInventory = false

    @Switch(title = "Background", description = "Whether to render a background behind the nametag")
    var background = true

    @Color(title = "Background color", description = "The color of the background")
    var backgroundColor = rgba(0, 0, 0, 0.247F) // 0,0,0,63

    @Color(title = "Text color", description = "The color of the text")
    var textColor = rgba(1, 1, 1, 1.0F)

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
        addDependency("cornerRadius", "rounded")
        addDependency("isShowInInventory", "isShowOwnNametag")

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