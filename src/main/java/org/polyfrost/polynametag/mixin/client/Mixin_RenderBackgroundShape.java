package org.polyfrost.polynametag.mixin.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
//? if < 26.2 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
*///?}
//? if >= 26.2 {
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
//?}
//? if >= 1.21.11 {
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
//?} else {
/*import net.minecraft.client.renderer.rendertype.RenderType;
*///?}
//? if >= 26.1 {
import org.joml.Matrix4fc;
//?} else {
/*import org.joml.Matrix4f;
*///?}
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = /*? if >= 1.21.10 {*/ "net.minecraft.client.renderer.feature.NameTagFeatureRenderer" /*?} else {*/ /*"net.minecraft.client.renderer.entity.EntityRenderer" *//*?}*/)
public abstract class Mixin_RenderBackgroundShape /*? if >= 26.2 {*/ extends RenderTypeFeatureRenderer<NameTagFeatureRenderer.Submit> /*?}*/ {
    //? if >= 26.2 {
    // 26.2 replaced Font.drawInBatch + MultiBufferSource with the NameTagFeatureRenderer.Submit +
    // GlyphRenderer pipeline. Draw the custom background shape for each submit before the glyphs are
    // built. Suppression of the default rectangular background is handled by the background-colour
    // ModifyArg below. The mixin extends the feature-renderer superclass so the inherited
    // getVertexBuilder is callable (it isn't declared on the target class).
    @Inject(method = "buildGroup", at = @At("HEAD"))
    private void polynametag$drawShapedBackground(FeatureFrameContext context, List<NameTagFeatureRenderer.Submit> submits, CallbackInfo ci) {
        if (!NametagRenderer.useCustomBackground()) {
            return;
        }
        for (NameTagFeatureRenderer.Submit submit : submits) {
            if ((submit.backgroundColor() >>> 24) == 0) {
                continue;
            }
            boolean seeThrough = submit.displayMode() == Font.DisplayMode.SEE_THROUGH;
            RenderType type = seeThrough ? RenderTypes.textBackgroundSeeThrough() : RenderTypes.textBackground();
            VertexConsumer consumer = getVertexBuilder(type);
            int argb = NametagRenderer.backgroundArgb();
            float[] vertices = NametagRenderer.backgroundQuads(submit.x(), submit.y(), context.font().width(submit.text()));
            for (int i = 0; i < vertices.length; i += 2) {
                consumer.addVertex(submit.pose(), vertices[i], vertices[i + 1], NametagRenderer.BACKGROUND_DEPTH)
                    .setColor(argb)
                    .setLight(submit.lightCoords());
            }
        }
    }

    // Suppress the default rectangular text background when a custom shape is drawn above.
    @ModifyArg(method = "prepareText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;"), index = 6)
    private static int polynametag$suppressDefaultBackground(int backgroundColor) {
        return NametagRenderer.useCustomBackground() ? 0 : backgroundColor;
    }
    //?} else {
    /*@WrapOperation(
        //? if >= 26.1
        method = "renderTranslucent",
        //? if >= 1.21.10 && < 26.1
        //method = "render",
        //? if < 1.21.10
        //method = "renderNameTag",
        at = @At(value = "INVOKE", target =
            //? if >= 26.1
            "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4fc;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V"
            //? if >= 1.21.8 && < 26.1
            //"Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V"
            //? if < 1.21.8
            //"Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I"
        )
    )
    //? if >= 1.21.8
    private void polynametag$drawShapedBackground(
    //? if < 1.21.8
    //private int polynametag$drawShapedBackground(
        Font font,
        Component text,
        float x,
        float y,
        int color,
        boolean shadow,
        //? if >= 26.1
        Matrix4fc matrix,
        //? if < 26.1
        //Matrix4f matrix,
        MultiBufferSource bufferSource,
        Font.DisplayMode displayMode,
        int backgroundColor,
        int light,
        //? if >= 1.21.8
        Operation<Void> original
        //? if < 1.21.8
        //Operation<Integer> original
    ) {
        if (NametagRenderer.useCustomBackground() && (backgroundColor >>> 24) != 0) {
            boolean seeThrough = displayMode == Font.DisplayMode.SEE_THROUGH;
            RenderType type;
            //? if >= 1.21.11
            type = seeThrough ? RenderTypes.textBackgroundSeeThrough() : RenderTypes.textBackground();
            //? if < 1.21.11
            //type = seeThrough ? RenderType.textBackgroundSeeThrough() : RenderType.textBackground();
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

        //? if < 1.21.8
        //return original.call(font, text, x, y, color, shadow, matrix, bufferSource, displayMode, backgroundColor, light);
        //? if >= 1.21.8
        original.call(font, text, x, y, color, shadow, matrix, bufferSource, displayMode, backgroundColor, light);
    }
    *///?}
}
