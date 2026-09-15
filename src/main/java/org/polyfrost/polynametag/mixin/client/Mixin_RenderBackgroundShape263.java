package org.polyfrost.polynametag.mixin.client;

//? if >= 26.3 {
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.feature.TextFeatureRenderer;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "net.minecraft.client.renderer.feature.TextFeatureRenderer")
public abstract class Mixin_RenderBackgroundShape263
    extends RenderTypeFeatureRenderer<TextFeatureRenderer.Submit> {

    // draws the shape before glyphs are built and extends the feature renderer superclass so inherited getVertexBuilder is callable.
    // TextFeatureRenderer is shared with signs, text displays and map labels, so only text marked by the nametag submit path is restyled.
    @Inject(method = "buildGroup", at = @At("HEAD"))
    private void polynametag$drawShapedBackground(
        FeatureFrameContext context,
        List<TextFeatureRenderer.Submit> submits,
        CallbackInfo ci
    ) {
        if (!NametagRenderer.useCustomBackground()) {
            return;
        }

        for (TextFeatureRenderer.Submit submit : submits) {
            if (!(submit.content() instanceof TextFeatureRenderer.Content.Text text)
                || (text.backgroundColor() >>> 24) == 0
                || !NametagRenderer.isNametagText(text.string())) {
                continue;
            }

            int argb = NametagRenderer.backgroundArgb();
            float[] rects = NametagRenderer.backgroundRectBuffer();
            int count = NametagRenderer.backgroundRects(
                text.x(),
                text.y(),
                context.font().width(text.string())
            );
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

    @ModifyArg(
        method = "renderText",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Font;prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;"
        ),
        index = 6
    )
    private static int polynametag$suppressDefaultBackground(
        int backgroundColor,
        @Local(argsOnly = true) TextFeatureRenderer.Content.Text content
    ) {
        return NametagRenderer.useCustomBackground() && NametagRenderer.isNametagText(content.string())
            ? 0
            : backgroundColor;
    }
}
//?} else {
/*public abstract class Mixin_RenderBackgroundShape263 {
}
*///?}
