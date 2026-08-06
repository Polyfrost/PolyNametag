package org.polyfrost.polynametag.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.TextColor
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.phys.AABB
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

object NametagRenderer {
    private const val CORNER_SEGMENTS = 8

    private const val ARC_POINTS = CORNER_SEGMENTS + 1

    private const val PERIMETER_POINTS = ARC_POINTS * 4

    private const val MAX_QUAD_FLOATS = PERIMETER_POINTS * 8

    private const val HEAD_SEARCH_RADIUS = 0.75
    private const val HEAD_SEARCH_TOP = 1.25

    const val BACKGROUND_DEPTH = 0.01F

    private val ARC_COS = FloatArray(PERIMETER_POINTS)
    private val ARC_SIN = FloatArray(PERIMETER_POINTS)

    private val perimeterScratch = FloatArray(PERIMETER_POINTS * 2)
    private val quadScratch = FloatArray(MAX_QUAD_FLOATS)

    private var lastWidthText: Component? = null
    private var lastWidth = 0

    init {
        val starts = doubleArrayOf(Math.PI, Math.PI * 1.5, 0.0, Math.PI * 0.5)
        val ends = doubleArrayOf(Math.PI * 1.5, Math.PI * 2.0, Math.PI * 0.5, Math.PI)
        var i = 0
        for (corner in 0 until 4) {
            val start = starts[corner]
            val end = ends[corner]
            for (s in 0..CORNER_SEGMENTS) {
                val angle = start + (end - start) * s / CORNER_SEGMENTS
                ARC_COS[i] = cos(angle).toFloat()
                ARC_SIN[i] = sin(angle).toFloat()
                i++
            }
        }
    }

    @JvmStatic
    fun useCustomBackground(): Boolean {
        if (!PolyNametagConfig.isEnabled || !PolyNametagConfig.background) {
            return false
        }
        return PolyNametagConfig.rounded || PolyNametagConfig.paddingX > 0.0F || PolyNametagConfig.paddingY > 0.0F
    }

    @JvmStatic
    fun backgroundArgb(): Int = PolyNametagConfig.backgroundColor.argb

    @JvmStatic
    fun textWidth(font: Font, text: Component): Int {
        if (lastWidthText === text) {
            return lastWidth
        }
        val width = font.width(text)
        lastWidthText = text
        lastWidth = width
        return width
    }

    @JvmStatic
    fun backgroundQuadBuffer(): FloatArray = quadScratch

    @JvmStatic
    fun backgroundQuads(x: Float, y: Float, width: Float): Int {
        val x0 = x - 1.0F - PolyNametagConfig.paddingX
        val x1 = x + width + PolyNametagConfig.paddingX
        val y0 = y - 1.0F - PolyNametagConfig.paddingY
        val y1 = y + 9.0F + PolyNametagConfig.paddingY

        val radius = if (PolyNametagConfig.rounded) {
            min(PolyNametagConfig.cornerRadius, min((x1 - x0) / 2.0F, (y1 - y0) / 2.0F))
        } else {
            0.0F
        }

        val out = quadScratch

        if (radius <= 0.0F) {
            out[0] = x0; out[1] = y1
            out[2] = x1; out[3] = y1
            out[4] = x1; out[5] = y0
            out[6] = x0; out[7] = y0
            return 8
        }

        val perimeter = perimeterScratch
        var p = 0
        var t = 0
        for (corner in 0 until 4) {
            val ccx = if (corner == 0 || corner == 3) x0 + radius else x1 - radius
            val ccy = if (corner == 0 || corner == 1) y0 + radius else y1 - radius
            for (s in 0 until ARC_POINTS) {
                perimeter[p++] = ccx + radius * ARC_COS[t]
                perimeter[p++] = ccy + radius * ARC_SIN[t]
                t++
            }
        }

        val cx = (x0 + x1) / 2.0F
        val cy = (y0 + y1) / 2.0F
        var o = 0
        for (i in 0 until PERIMETER_POINTS) {
            val ax = perimeter[i * 2]
            val ay = perimeter[i * 2 + 1]
            val n = (i + 1) % PERIMETER_POINTS
            val bx = perimeter[n * 2]
            val by = perimeter[n * 2 + 1]
            out[o++] = cx; out[o++] = cy
            out[o++] = bx; out[o++] = by
            out[o++] = ax; out[o++] = ay
            out[o++] = ax; out[o++] = ay
        }
        return o
    }

    @JvmStatic
    fun textColor(original: Int): Int {
        if (!PolyNametagConfig.isEnabled) {
            return original
        }

        val color = PolyNametagConfig.textColor.argb
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

        val color = PolyNametagConfig.backgroundColor.argb
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
        val screen = /*? if >= 26.2 {*/ Minecraft.getInstance().gui.screen() /*?} else {*/ /*Minecraft.getInstance().screen *//*?}*/
        return screen is net.minecraft.client.gui.screens.inventory.InventoryScreen ||
            screen is net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
    }

    private fun Int.withAlpha(alpha: Int): Int = (this and 0x00FFFFFF) or (alpha.coerceIn(0, 255) shl 24)
}
