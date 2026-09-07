package org.polyfrost.polynametag.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4fc;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import net.minecraft.client.renderer.feature.TextFeatureRenderer;

//? if >= 26.3 {
@Mixin(targets = "net.minecraft.client.renderer.SubmitNodeCollection")
public abstract class Mixin_WrapNametagRender263 {
    @WrapOperation(
        method = "nameTag",
        at = @At(
            value = "NEW",
            target = "(FFLnet/minecraft/util/FormattedCharSequence;ZIII)Lnet/minecraft/client/renderer/feature/TextFeatureRenderer$Content$Text;"
        )
    )
    private static TextFeatureRenderer.Content.Text wrapNametagRender(float x, float y, FormattedCharSequence string, boolean dropShadow, int color, int backgroundColor, int outlineColor, Operation<TextFeatureRenderer.Content.Text> original) {
        return original.call(
            x,
            NametagRenderer.translateY(y),
            string,
            NametagRenderer.textShadow(dropShadow),
            NametagRenderer.textColor(color),
            NametagRenderer.backgroundColor(backgroundColor),
            outlineColor
        );
    }
}
//?} else {
/*public abstract class Mixin_WrapNametagRender263 {
}
*///?}
