package org.polyfrost.polynametag

import club.sk1er.patcher.config.OldPatcherConfig
import club.sk1er.patcher.config.PatcherConfig
import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.Property
import org.polyfrost.oneconfig.api.config.v1.annotations.Color
import org.polyfrost.oneconfig.api.config.v1.annotations.Dropdown
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.ui.v1.Notifications
import org.polyfrost.polyui.color.rgba

object PolyNametagConfig : Config("nametag.json", "/polynametag.svg", PolyNametag.NAME, Category.QOL) {

    @Switch(title = "Enabled")
    var enabled = false

    @Slider(title = "Height offset", min = -0.5f, max = 0.5f, description = "How much to offset the nametag vertically")
    var heightOffset = 0f
        get() = field.coerceIn(-0.5f, 0.5f)

    @Slider(title = "Scale", min = 0f, max = 1f, description = "How much to scale the nametag")
    var scale = 1f
        get() = field.coerceIn(0f, 1f)

    @Switch(title = "Rounded Corners")
    var rounded = false

    @Slider(title = "Corner Radius", min = 0f, max = 10f)
    var cornerRadius = 0f
        get() = field.coerceIn(0f, 10f)

    @Slider(title = "Padding X", min = 0f, max = 10f)
    var paddingX = 0f
        get() = field.coerceIn(0f, 10f)

    @Slider(title = "Padding Y", min = 0f, max = 10f)
    var paddingY = 0f
        get() = field.coerceIn(0f, 10f)

    @Dropdown(title = "Text Type", options = ["No Shadow", "Shadow", "Full Shadow"], description = "The type of shadow to render")
    var textType = 0

    /*
    @Info(
        type = InfoType.WARNING,
        text = "Using Full Shadow may cause performance issues on low-end devices"
    )
    var info1 = 0
     */

    @Switch(title = "Show own nametag", description = "Whether to show your own nametag")
    var showOwnNametag = true

    @Switch(title = "Show in inventory")
    var showInInventory = false

    @Switch(title = "Background", description = "Whether to render a background behind the nametag")
    var background = true

    @Color(title = "Background color", description = "The color of the background")
    var backgroundColor = rgba(0, 0, 0, 0.247F) // 0,0,0,63

    @Switch(title = "Offset Essential Indicator", description = "Offset nametag to center if the player has essential indicator drawn")
    var essentialOffset = true

    /*
    @CustomOption
    @Transient
    val nametagPreview = NametagPreview(category = "General")
     */

    private var hasMigratedPatcher = false

    init {
        addDependency("backgroundColor", "background")
        addDependency("background", "Patcher's Disable Nametag Boxes. Please turn it off to use this feature.") {
            if (PolyNametag.isPatcher && PatcherConfig.disableNametagBoxes) {
                Property.Display.DISABLED
            } else {
                Property.Display.SHOWN
            }
        }
        addDependency("showOwnNametag", "Patcher's Show Own Nametag. Please turn it off to use this feature.") {
            if (PolyNametag.isPatcher && PatcherConfig.showOwnNametag) {
                Property.Display.DISABLED
            } else {
                Property.Display.SHOWN
            }
        }
        addDependency("cornerRadius", "rounded")
        addDependency("showInInventory", "showOwnNametag")

        // TODO
        //hideIf("essentialOffset") { !PolyNametag.isEssential }

        if (!hasMigratedPatcher) {
            try {
                Class.forName("club.sk1er.patcher.config.OldPatcherConfig")
                var didAnything = false
                if (OldPatcherConfig.shadowedNametagText) {
                    textType = 1
                    didAnything = true
                }
                if (OldPatcherConfig.disableNametagBoxes) {
                    background = false
                    didAnything = true
                }
                if (OldPatcherConfig.showOwnNametag) {
                    showOwnNametag = true
                    didAnything = true
                }

                hasMigratedPatcher = true
                save()

                if (didAnything) {
                    Notifications.enqueue(Notifications.Type.Info, "PolyNametag", "Migrated Patcher settings replaced by PolyNametag. Please check PolyNametag's settings to make sure they are correct.")
                }
            } catch (_: ClassNotFoundException) {

            }
        }
    }

    /*
    override fun getCustomOption(
        field: Field,
        annotation: CustomOption,
        page: OptionPage,
        mod: Mod,
        migrate: Boolean,
    ): BasicOption = nametagPreview.also {
        ConfigUtils.getSubCategory(page, it.category, it.subcategory).options.add(it)
    }
     */
}