package org.polyfrost.polynametag.mixin.client;

//? if > 1.8.9 && < 26.2 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
//~ if < 1.21.11 'rendertype.RenderType' -> 'RenderType'
import net.minecraft.client.renderer.rendertype.RenderType;
//? if >= 1.21.11
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.network.chat.Component;
//~ if < 26.1 'Matrix4fc' -> 'Matrix4f'
import org.joml.Matrix4fc;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//~ if < 1.21.10 'feature.NameTagFeatureRenderer' -> 'entity.EntityRenderer'
@Mixin(targets = "net.minecraft.client.renderer.feature.NameTagFeatureRenderer")
public abstract class Mixin_RenderBackgroundShape {
    @WrapOperation(
        //? if >= 26.1 {
        method = "renderTranslucent",
        //?} elif >= 1.21.10 {
        /^method = "render",
        ^///?} else
        //method = "renderNameTag",
        at = @At(
            value = "INVOKE",
			//~ if < 26.1 'Matrix4fc' -> 'Matrix4f'
			//~ if < 1.21.8 ')V' -> ')I'
            target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4fc;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V"
        )
    )
    //~ if < 1.21.8 'void' -> 'int'
    private void polynametag$drawShapedBackground(
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
        int packedLightCoords,
        //~ if < 1.21.8 'Void' -> 'Integer'
        Operation<Void> original
    ) {
        if (NametagRenderer.useCustomBackground() && (backgroundColor >>> 24) != 0) {
            boolean seeThrough = displayMode == Font.DisplayMode.SEE_THROUGH;
            RenderType type;
            //~ if < 1.21.11 'RenderTypes' -> 'RenderType'
            type = seeThrough ? RenderTypes.textBackgroundSeeThrough() : RenderTypes.textBackground();
            VertexConsumer consumer = bufferSource.getBuffer(type);

            int argb = NametagRenderer.backgroundArgb();
            float[] vertices = NametagRenderer.backgroundQuadBuffer();
            int count = NametagRenderer.backgroundQuads(x, y, NametagRenderer.textWidth(font, text));
            for (int i = 0; i < count; i += 2) {
                consumer.addVertex(matrix, vertices[i], vertices[i + 1], NametagRenderer.BACKGROUND_DEPTH)
                    .setColor(argb)
                    .setLight(packedLightCoords);
            }

            backgroundColor = 0;
        }

        //~ if < 1.21.8 'original.call(' -> 'return original.call('
        original.call(font, text, x, y, color, shadow, matrix, bufferSource, displayMode, backgroundColor, packedLightCoords);
    }
}
*///?} elif = 1.8.9 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.vertex.BufferBuilder;
import net.minecraft.client.render.vertex.DefaultVertexFormat;
import net.minecraft.client.render.vertex.Tesselator;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.renderer.entity.EntityRenderer")
public abstract class Mixin_RenderBackgroundShape {
    @WrapOperation(
        method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Ljava/lang/String;DDDI)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/vertex/Tesselator;end()V"
        )
    )
    private void polynametag$drawShapedBackground(
        Tesselator tesselator,
        Operation<Void> original,
        @Local(argsOnly = true) String name,
        @Local TextRenderer font
    ) {
        if (PolyNametagConfig.isEnabled()) {
            BufferBuilder buffer = tesselator.getBuffer();
            buffer.end();
            buffer.begin(7, DefaultVertexFormat.POSITION_COLOR);

            int argb = NametagRenderer.backgroundColor(0x40000000);
            if ((argb >>> 24) != 0) {
                int width = font.getWidth(name);
                float[] vertices = NametagRenderer.backgroundQuadBuffer();
                int count = NametagRenderer.backgroundQuads(
                    -width / 2,
                    NametagRenderer.translateY(name.equals("deadmau5") ? -10 : 0),
                    width
                );
                for (int i = 0; i < count; i += 2) {
                    buffer.vertex(vertices[i], vertices[i + 1], NametagRenderer.BACKGROUND_DEPTH)
                        .color(argb >> 16 & 255, argb >> 8 & 255, argb & 255, argb >>> 24)
                        .nextVertex();
                }
            }
        }

        original.call(tesselator);
    }
}
*///?} else {
public abstract class Mixin_RenderBackgroundShape {
}
//?}
