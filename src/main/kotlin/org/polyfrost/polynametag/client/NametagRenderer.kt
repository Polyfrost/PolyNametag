package org.polyfrost.polynametag.client

import dev.deftu.omnicore.api.client.render.OmniTextRenderer
import dev.deftu.omnicore.api.client.render.TextShadowType
import dev.deftu.omnicore.api.client.render.pipeline.OmniRenderPipeline
import dev.deftu.omnicore.api.client.render.pipeline.OmniRenderPipelines
import dev.deftu.omnicore.api.client.render.stack.OmniPoseStack
import dev.deftu.omnicore.api.client.render.vertex.roundedQuad
import dev.deftu.omnicore.api.color.OmniColor
import dev.deftu.omnicore.api.color.OmniColors
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity

object NametagRenderer {
    private val PIPELINE by lazy {
        OmniRenderPipelines.POSITION_COLOR_TRIANGLES
            .newBuilder()
            .setDepthTest(OmniRenderPipeline.DepthTest.LESS)
            .setDepthMask(false)
            .build()
    }

    @JvmStatic
    fun drawBackground(
        matrices: OmniPoseStack,
        x1: Double, x2: Double,
        leftPad: Float = 0.0F,
    ) {
        if (!PolyNametagConfig.background) {
            return
        }

        matrices.with {
            val baselineY = 3.5F
            val realX1 = (x1.toFloat() - leftPad)
            val realX2 = x2.toFloat()
            val span = (realX2 - realX1).coerceAtLeast(0.0F)
            if (span <= 0.0F) {
                return@with
            }

            val centerX = (realX1 + realX2) / 2.0F
            matrices.translate(centerX, baselineY, 0.01F)
            val color = PolyNametagConfig.backgroundColor.let { c -> OmniColor(c.r, c.g, c.b, c.a) }
            val halfWidth = (span / 2.0F) + PolyNametagConfig.paddingX.coerceIn(0.0F, 10.0F)
            val halfHeight = 4.5F + PolyNametagConfig.paddingY.coerceIn(0.0F, 10.0F)
            val radius = if (PolyNametagConfig.rounded) {
                PolyNametagConfig.cornerRadius
                    .coerceIn(0.0F, 10.0F)
                    .coerceAtMost(halfWidth)
                    .coerceAtMost(halfHeight)
            } else 0.0F

            val buffer = PIPELINE.createBufferBuilder()
            buffer.roundedQuad(
                pose = matrices,
                x = (-halfWidth).toDouble(),
                y = (-halfHeight).toDouble() - PolyNametagConfig.heightOffset,
                width = (halfWidth * 2.0F).toDouble(),
                height = (halfHeight * 2.0F).toDouble(),
                color = color,
                radius = radius,
                segmentScale = 1.5
            )
            buffer.buildOrNull()?.drawAndClose(PIPELINE)
        }
    }

    @JvmStatic
    fun drawBackground(
        matrices: OmniPoseStack,
        entity: Entity,
    ) {
        val displayName = entity.displayName ?: return
        val halfWidth = OmniTextRenderer.width(displayName.string) / 2 + 1.0
        val leftPad = 0.0F
        drawBackground(matrices, -halfWidth, halfWidth, leftPad)
    }

    //? if >= 1.21.2 {
    @JvmStatic
    fun drawBackground(
        matrices: OmniPoseStack,
    //? if >= 1.21.4 {
        displayName: Component?,
    //?} else {
        /*displayName: net.minecraft.text.Text?,
    *///?}
    ) {
        val displayName = displayName ?: return
        val halfWidth = OmniTextRenderer.width(displayName.string) / 2 + 1.0
        drawBackground(matrices, -halfWidth, halfWidth)
    }
    //?}

    @JvmStatic
    fun drawNametagString(
        matrices: OmniPoseStack,
        text: Component,
        x: Float, y: Float,
        color: OmniColor,
    ): Int {
        return matrices.with {
            matrices.translate(0.0F, 0.0F, -0.01F)
            val render = OmniTextRenderer.render(
                matrices, text/*? if !=1.21.8 {*/.string /*?}*/, x, y, color, // TODO: make it not a string!
                when (PolyNametagConfig.textType) {
                    0 -> TextShadowType.None
                    1 -> TextShadowType.Drop
                    2 -> TextShadowType.Outline(OmniColors.BLACK.withAlpha(color.alpha))
                    else -> throw IllegalStateException("Unexpected value: ${PolyNametagConfig.textType}")
                }
            )
            render
        }
    }
}
