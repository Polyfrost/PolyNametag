package org.polyfrost.polynametag.mixin.client;

//? if < 26.2 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//~ if < 1.21.10 'net.minecraft.client.renderer.feature.NameTagFeatureRenderer' -> 'net.minecraft.client.renderer.entity.EntityRenderer'
@Mixin(targets = "net.minecraft.client.renderer.feature.NameTagFeatureRenderer")
public abstract class Mixin_RenderBackgroundShape {
    @WrapOperation(
		//~ if < 26.1 'renderTranslucent' -> 'render'
		//~ if < 1.21.10 'render' -> 'renderNameTag'
        method = "render",
        at = @At(
            value = "INVOKE",
			//~ if < 26.1 'Matrix4fc' -> 'Matrix4f'
			//~ if < 1.21.8 ')V' -> ')I'
            target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4fc;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V"
        )
    )
    private int polynametag$drawShapedBackground(
        Font font,
        Component text,
        float x,
        float y,
        int color,
        boolean shadow,
		//~ if < 26.1 'Matrix4fc' -> 'Matrix4f'
        Matrix4fc matrix,
        MultiBufferSource bufferSource,
        Font.DisplayMode displayMode,
        int backgroundColor,
        int light,
        Operation<Integer> original
    ) {
        if (NametagRenderer.useCustomBackground() && (backgroundColor >>> 24) != 0) {
            boolean seeThrough = displayMode == Font.DisplayMode.SEE_THROUGH;
            RenderType type;
            type = seeThrough ? RenderType.textBackgroundSeeThrough() : RenderType.textBackground();
            VertexConsumer consumer = bufferSource.getBuffer(type);

            int argb = NametagRenderer.backgroundArgb();
            float[] vertices = NametagRenderer.backgroundQuadBuffer();
            int count = NametagRenderer.backgroundQuads(x, y, NametagRenderer.textWidth(font, text));
            for (int i = 0; i < count; i += 2) {
                consumer.addVertex(matrix, vertices[i], vertices[i + 1], NametagRenderer.BACKGROUND_DEPTH)
                    .setColor(argb)
                    .setLight(light);
            }

            backgroundColor = 0;
        }

        return original.call(font, text, x, y, color, shadow, matrix, bufferSource, displayMode, backgroundColor, light);
    }
}
*///?} else {
public abstract class Mixin_RenderBackgroundShape {
}
//?}
