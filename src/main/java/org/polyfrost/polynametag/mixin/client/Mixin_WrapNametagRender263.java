package org.polyfrost.polynametag.mixin.client;

//? if >= 26.3 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.renderer.feature.TextFeatureRenderer;

@Mixin(targets = "net.minecraft.client.renderer.SubmitNodeCollection")
public abstract class Mixin_WrapNametagRender263 {
    @WrapOperation(
        method = "nameTag",
        at = @At(
            value = "NEW",
            target = "(FFLnet/minecraft/util/FormattedCharSequence;ZIII)Lnet/minecraft/client/renderer/feature/TextFeatureRenderer$Content$Text;"
        )
    )
    private static TextFeatureRenderer.Content.Text wrapNametagRender(float x, float y, FormattedCharSequence string, boolean dropShadow, int color, int backgroundColor, int outlineColor, Operation<TextFeatureRenderer.Content.Text> original, @Local(argsOnly = true) Font.DisplayMode displayMode) {
        boolean shadow = displayMode == Font.DisplayMode.SEE_THROUGH ? dropShadow : NametagRenderer.textShadow(dropShadow);
        return original.call(
            x,
            NametagRenderer.translateY(y),
            NametagRenderer.markNametagText(string),
            shadow,
            NametagRenderer.textColor(color),
            NametagRenderer.backgroundColor(backgroundColor),
            outlineColor
        );
    }
}
//?}
