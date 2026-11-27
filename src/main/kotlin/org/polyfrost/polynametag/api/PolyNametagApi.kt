package org.polyfrost.polynametag.api

import net.minecraft.world.entity.Entity
import org.polyfrost.polynametag.client.NametagRenderer

object PolyNametagApi {
    @JvmField
    val VERSION: Int = 2

    @JvmStatic
    fun style(
        textColor: Int,
        textShadow: Boolean,
        backgroundColor: Int,
        seeThrough: Boolean,
        x: Float,
        y: Float,
        width: Float,
    ): NametagStyle = NametagRenderer.resolve(textColor, textShadow, backgroundColor, seeThrough, x, y, width)

    @JvmStatic
    fun showsOwnNametag(): Boolean = NametagRenderer.showsOwnNametag()

    @JvmStatic
    fun lowersSneakingNametag(entity: Entity): Boolean = NametagRenderer.lowersSneakingNametag(entity)

    @JvmStatic
    fun yieldBackground(yielded: Boolean) {
        NametagRenderer.backgroundYielded = yielded
    }
}
