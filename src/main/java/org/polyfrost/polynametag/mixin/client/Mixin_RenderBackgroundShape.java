package org.polyfrost.polynametag.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
//? if >= 1.21.11 {
/*import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
*///?} else {
import net.minecraft.client.renderer.RenderType;
//?}
//? if >= 26.1 {
/*import org.joml.Matrix4fc;
*///?} else {
import org.joml.Matrix4f;
//?}
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = /*? if >= 1.21.10 {*/ "net.minecraft.client.renderer.feature.NameTagFeatureRenderer" /*?} else {*/ /*"net.minecraft.client.renderer.entity.EntityRenderer" *//*?}*/)
public abstract class Mixin_RenderBackgroundShape {
    @WrapOperation(
        method = /*? if >= 26.1 {*/ /*"renderTranslucent" *//*?} elif >= 1.21.10 {*/ "render" /*?} else {*/ /*"renderNameTag" *//*?}*/,
        at = @At(value = "INVOKE", target = /*? if >= 26.1 {*/ /*"Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4fc;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V" *//*?} elif >=1.21.8 {*/ "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V" /*?} else {*/ /*"Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I" *//*?}*/)
    )
    private /*? if <1.21.8 {*/ /*int *//*?} else {*/ void /*?}*/ polynametag$drawShapedBackground(
        Font font,
        Component text,
        float x,
        float y,
        int color,
        boolean shadow,
        /*? if >= 26.1 {*/ /*Matrix4fc *//*?} else {*/ Matrix4f /*?}*/ matrix,
        MultiBufferSource bufferSource,
        Font.DisplayMode displayMode,
        int backgroundColor,
        int light,
        Operation</*? if <1.21.8 {*/ /*Integer *//*?} else {*/ Void /*?}*/> original
    ) {
        if (NametagRenderer.useCustomBackground() && (backgroundColor >>> 24) != 0) {
            boolean seeThrough = displayMode == Font.DisplayMode.SEE_THROUGH;
            RenderType type = seeThrough
                ? /*? if >= 1.21.11 {*/ /*RenderTypes.textBackgroundSeeThrough() *//*?} else {*/ RenderType.textBackgroundSeeThrough() /*?}*/
                : /*? if >= 1.21.11 {*/ /*RenderTypes.textBackground() *//*?} else {*/ RenderType.textBackground() /*?}*/;
            VertexConsumer consumer = bufferSource.getBuffer(type);

            int argb = NametagRenderer.backgroundArgb();
            float[] vertices = NametagRenderer.backgroundQuads(x, y, font.width(text));
            for (int i = 0; i < vertices.length; i += 2) {
                consumer.addVertex(matrix, vertices[i], vertices[i + 1], NametagRenderer.BACKGROUND_DEPTH)
                    .setColor(argb)
                    .setLight(light);
            }

            backgroundColor = 0;
        }

        /*? if <1.21.8 {*/
        /*return original.call(font, text, x, y, color, shadow, matrix, bufferSource, displayMode, backgroundColor, light);
        *///?} else {
        original.call(font, text, x, y, color, shadow, matrix, bufferSource, displayMode, backgroundColor, light);
        //?}
    }
}
