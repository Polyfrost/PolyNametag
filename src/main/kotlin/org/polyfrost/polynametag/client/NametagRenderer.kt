package org.polyfrost.polynametag.client

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.TextColor
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.phys.AABB
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

object NametagRenderer {
    private const val CORNER_SEGMENTS = 8

    private const val HEAD_SEARCH_RADIUS = 0.75
    private const val HEAD_SEARCH_TOP = 1.25

    const val BACKGROUND_DEPTH = 0.01F


    @JvmStatic
    fun useCustomBackground(): Boolean {
        if (!PolyNametagConfig.isEnabled || !PolyNametagConfig.background) {
            return false
        }
        return PolyNametagConfig.rounded || PolyNametagConfig.paddingX > 0.0F || PolyNametagConfig.paddingY > 0.0F
    }

    @JvmStatic
    fun backgroundArgb(): Int = colorToArgb(
        PolyNametagConfig.backgroundColor.redF,
        PolyNametagConfig.backgroundColor.greenF,
        PolyNametagConfig.backgroundColor.blueF,
        PolyNametagConfig.backgroundColor.alphaF
    )

    @JvmStatic
    fun backgroundQuads(x: Float, y: Float, width: Float): FloatArray {
        val x0 = x - 1.0F - PolyNametagConfig.paddingX
        val x1 = x + width + PolyNametagConfig.paddingX
        val y0 = y - 1.0F - PolyNametagConfig.paddingY
        val y1 = y + 9.0F + PolyNametagConfig.paddingY

        val radius = if (PolyNametagConfig.rounded) {
            min(PolyNametagConfig.cornerRadius, min((x1 - x0) / 2.0F, (y1 - y0) / 2.0F))
        } else {
            0.0F
        }

        if (radius <= 0.0F) {
            return floatArrayOf(
                x0, y1,
                x1, y1,
                x1, y0,
                x0, y0
            )
        }

        val perimeter = ArrayList<Float>((CORNER_SEGMENTS + 1) * 4 * 2)
        addArc(perimeter, x0 + radius, y0 + radius, radius, Math.PI, Math.PI * 1.5)          // top-left
        addArc(perimeter, x1 - radius, y0 + radius, radius, Math.PI * 1.5, Math.PI * 2.0)    // top-right
        addArc(perimeter, x1 - radius, y1 - radius, radius, 0.0, Math.PI * 0.5)              // bottom-right
        addArc(perimeter, x0 + radius, y1 - radius, radius, Math.PI * 0.5, Math.PI)          // bottom-left

        val cx = (x0 + x1) / 2.0F
        val cy = (y0 + y1) / 2.0F
        val pointCount = perimeter.size / 2
        val out = FloatArray(pointCount * 8)
        var o = 0
        for (i in 0 until pointCount) {
            val ax = perimeter[i * 2]
            val ay = perimeter[i * 2 + 1]
            val n = (i + 1) % pointCount
            val bx = perimeter[n * 2]
            val by = perimeter[n * 2 + 1]
            out[o++] = cx; out[o++] = cy
            out[o++] = bx; out[o++] = by
            out[o++] = ax; out[o++] = ay
            out[o++] = ax; out[o++] = ay
        }
        return out
    }

    private fun addArc(into: ArrayList<Float>, cx: Float, cy: Float, radius: Float, start: Double, end: Double) {
        for (s in 0..CORNER_SEGMENTS) {
            val angle = start + (end - start) * s / CORNER_SEGMENTS
            into.add(cx + radius * cos(angle).toFloat())
            into.add(cy + radius * sin(angle).toFloat())
        }
    }

    @JvmStatic
    fun textColor(original: Int): Int {
        if (!PolyNametagConfig.isEnabled) {
            return original
        }

        val color = colorToArgb(
            PolyNametagConfig.textColor.redF,
            PolyNametagConfig.textColor.greenF,
            PolyNametagConfig.textColor.blueF,
            PolyNametagConfig.textColor.alphaF
        )
        val originalAlpha = original ushr 24
        return if (originalAlpha in 1..254) {
            color.withAlpha((color ushr 24).coerceAtMost(originalAlpha))
        } else {
            color
        }
    }

    @JvmStatic
    fun overrideTextComponent(original: Component): Component {
        if (!PolyNametagConfig.isEnabled || !PolyNametagConfig.overrideTextColor) {
            return original
        }
        return stripColor(original)
    }

    private fun stripColor(component: Component): Component {
        val result: MutableComponent = MutableComponent.create(component.contents)
            .setStyle(component.style.withColor(null as TextColor?))
        for (sibling in component.siblings) {
            result.append(stripColor(sibling))
        }
        return result
    }

    @JvmStatic
    fun backgroundColor(original: Int): Int {
        if (!PolyNametagConfig.isEnabled) {
            return original
        }

        if (!PolyNametagConfig.background || original == 0) {
            return 0
        }

        val color = colorToArgb(
            PolyNametagConfig.backgroundColor.redF,
            PolyNametagConfig.backgroundColor.greenF,
            PolyNametagConfig.backgroundColor.blueF,
            PolyNametagConfig.backgroundColor.alphaF
        )
        val originalAlpha = original ushr 24
        return if (originalAlpha in 1..32) {
            color.withAlpha((color ushr 24).coerceAtMost(originalAlpha))
        } else {
            color
        }
    }

    @JvmStatic
    fun textShadow(original: Boolean): Boolean {
        if (!PolyNametagConfig.isEnabled) {
            return original
        }

        return PolyNametagConfig.textType != 0
    }

    @JvmStatic
    fun currentPlayer(): Entity? = Minecraft.getInstance().player

    @JvmStatic
    fun hasServerNametag(entity: Entity): Boolean {
        val level = entity.level()
        val box = AABB(
            entity.x - HEAD_SEARCH_RADIUS,
            entity.y + entity.bbHeight * 0.5,
            entity.z - HEAD_SEARCH_RADIUS,
            entity.x + HEAD_SEARCH_RADIUS,
            entity.y + entity.bbHeight + HEAD_SEARCH_TOP,
            entity.z + HEAD_SEARCH_RADIUS
        )
        return level.getEntities(entity, box) { it !== entity && isNametagEntity(it) }.isNotEmpty()
    }

    private fun isNametagEntity(entity: Entity): Boolean = when (entity) {
        is Display.TextDisplay -> true
        is ArmorStand -> entity.isCustomNameVisible && entity.customName != null
        else -> false
    }

    @JvmStatic
    fun isInventoryScreenOpen(): Boolean {
        val screen = Minecraft.getInstance().screen
        return screen is net.minecraft.client.gui.screens.inventory.InventoryScreen ||
            screen is net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
    }

    private fun colorToArgb(r: Float, g: Float, b: Float, a: Float): Int {
        val alpha = channel(a)
        val red = channel(r)
        val green = channel(g)
        val blue = channel(b)
        return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
    }

    private fun channel(value: Float): Int = (value.coerceIn(0.0F, 1.0F) * 255.0F).roundToInt()

    private fun Int.withAlpha(alpha: Int): Int = (this and 0x00FFFFFF) or (alpha.coerceIn(0, 255) shl 24)
}
