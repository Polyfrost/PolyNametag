package org.polyfrost.polynametag.mixin.client;

//? if = 26.2 {
/*import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "net.minecraft.client.renderer.feature.NameTagFeatureRenderer")
public abstract class Mixin_RenderBackgroundShape262
    extends RenderTypeFeatureRenderer<NameTagFeatureRenderer.Submit> {

    // draws the shape before glyphs are built and extends the feature renderer superclass so inherited getVertexBuilder is callable
    @Inject(method = "buildGroup", at = @At("HEAD"))
    private void polynametag$drawShapedBackground(
        FeatureFrameContext context,
        List<NameTagFeatureRenderer.Submit> submits,
        CallbackInfo ci
    ) {
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
            float[] vertices = NametagRenderer.backgroundQuadBuffer();
            int count = NametagRenderer.backgroundQuads(
                submit.x(),
                submit.y(),
                NametagRenderer.textWidth(context.font(), submit.text())
            );
            for (int i = 0; i < count; i += 2) {
                consumer.addVertex(submit.pose(), vertices[i], vertices[i + 1], NametagRenderer.BACKGROUND_DEPTH)
                    .setColor(argb)
                    .setLight(submit.lightCoords());
            }
        }
    }

    @ModifyArg(
        method = "prepareText",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Font;prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;"
        ),
        index = 6
    )
    private static int polynametag$suppressDefaultBackground(int backgroundColor) {
        return NametagRenderer.useCustomBackground() ? 0 : backgroundColor;
    }
}
*///?} else {
public abstract class Mixin_RenderBackgroundShape262 {
}
//?}
