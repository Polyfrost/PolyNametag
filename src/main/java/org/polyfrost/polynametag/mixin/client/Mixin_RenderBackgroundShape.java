package org.polyfrost.polynametag.mixin.client;

//? if >= 26.3 {
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.feature.TextFeatureRenderer;
import org.polyfrost.polynametag.api.NametagStyle;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "net.minecraft.client.renderer.feature.TextFeatureRenderer")
public abstract class Mixin_RenderBackgroundShape
    extends RenderTypeFeatureRenderer<TextFeatureRenderer.Submit> {

    // draws the shape before glyphs are built and extends the feature renderer superclass so inherited getVertexBuilder is callable.
    // TextFeatureRenderer is shared with signs, text displays and map labels, so only text marked by the nametag submit path is restyled.
    @Inject(method = "buildGroup", at = @At("HEAD"))
    private void polynametag$drawShapedBackground(
        FeatureFrameContext context,
        List<TextFeatureRenderer.Submit> submits,
        CallbackInfo ci
    ) {
        for (TextFeatureRenderer.Submit submit : submits) {
            if (!(submit.content() instanceof TextFeatureRenderer.Content.Text text)) {
                continue;
            }
            NametagStyle style = NametagRenderer.nametagStyle(text.string());
            if (style == null || !style.getOwnBackground()) {
                continue;
            }

            int argb = style.getBackgroundColor();
            float[] rects = NametagRenderer.backgroundRectBuffer();
            int count = NametagRenderer.backgroundRects(style);
            for (int i = 0; i < count; i += 4) {
                TextRenderable background = context.font().prepareBackground(
                    rects[i],
                    rects[i + 1],
                    rects[i + 2],
                    rects[i + 3],
                    argb
                );
                background.render(
                    submit.pose(),
                    getVertexBuilder(background.renderType(submit.displayMode())),
                    submit.lightCoords(),
                    false
                );
            }
        }
    }
}
//?} elif = 26.2 {
/*import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.polyfrost.polynametag.api.NametagStyle;
import org.polyfrost.polynametag.client.BackgroundYieldHolder;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "net.minecraft.client.renderer.feature.NameTagFeatureRenderer")
public abstract class Mixin_RenderBackgroundShape
    extends RenderTypeFeatureRenderer<NameTagFeatureRenderer.Submit> {

    // draws the shape before glyphs are built and extends the feature renderer superclass so inherited getVertexBuilder is callable
    @Inject(method = "buildGroup", at = @At("HEAD"))
    private void polynametag$drawShapedBackground(
        FeatureFrameContext context,
        List<NameTagFeatureRenderer.Submit> submits,
        CallbackInfo ci
    ) {
        float[] vertices = NametagRenderer.backgroundQuadBuffer();
        VertexConsumer consumer = null;
        Font.DisplayMode consumerMode = null;
        for (NameTagFeatureRenderer.Submit submit : submits) {
            if (((BackgroundYieldHolder) (Object) submit).polynametag$backgroundYielded()) {
                continue;
            }
            boolean seeThrough = submit.displayMode() == Font.DisplayMode.SEE_THROUGH;
            NametagStyle style = NametagRenderer.resolve(
                submit.color(),
                false,
                submit.backgroundColor(),
                seeThrough,
                submit.x(),
                submit.y(),
                -2.0F * submit.x()
            );
            if (!style.getOwnBackground()) {
                continue;
            }

            if (consumer == null || submit.displayMode() != consumerMode) {
                consumerMode = submit.displayMode();
                RenderType type = seeThrough ? RenderTypes.textBackgroundSeeThrough() : RenderTypes.textBackground();
                consumer = getVertexBuilder(type);
            }
            int argb = style.getBackgroundColor();
            int count = NametagRenderer.backgroundQuads(style);
            for (int i = 0; i < count; i += 2) {
                consumer.addVertex(submit.pose(), vertices[i], vertices[i + 1], NametagRenderer.BACKGROUND_DEPTH)
                    .setColor(argb)
                    .setLight(submit.lightCoords());
            }
        }
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
import org.polyfrost.polynametag.api.NametagStyle;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.renderer.entity.EntityRenderer")
public abstract class Mixin_RenderBackgroundShape {
    @Shadow
    public abstract TextRenderer getTextRenderer();

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
        @Local(argsOnly = true) String name
    ) {
        if (PolyNametagConfig.isEnabled() && !NametagRenderer.getBackgroundYielded()) {
            BufferBuilder buffer = tesselator.getBuffer();
            buffer.end();
            buffer.begin(7, DefaultVertexFormat.POSITION_COLOR);

            int width = getTextRenderer().getWidth(name);
            NametagStyle style = NametagRenderer.resolve(
                NametagRenderer.LEGACY_SEE_THROUGH_TEXT,
                false,
                NametagRenderer.LEGACY_BACKGROUND,
                true,
                -width / 2,
                name.equals("deadmau5") ? -10 : 0,
                width
            );
            if (style.getOwnBackground()) {
                int argb = style.getBackgroundColor();
                float[] vertices = NametagRenderer.backgroundQuadBuffer();
                int count = NametagRenderer.backgroundQuads(style);
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
/*public abstract class Mixin_RenderBackgroundShape {
}
*///?}
