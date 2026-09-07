package org.polyfrost.polynametag.mixin.client;

//? if = 26.2 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4fc;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net.minecraft.client.renderer.feature.NameTagFeatureRenderer")
public abstract class Mixin_WrapNametagRender262 {
    @WrapOperation(
        method = "prepareText",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Font;prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;"
        )
    )
    private static Font.PreparedText wrapNametagRender(FormattedCharSequence text, float x, float y, int originalColor, boolean drawShadow, boolean includeEmpty, int backgroundColor, Operation<Font.PreparedText> original) {
        return original.call(
            text,
            x,
            NametagRenderer.translateY(y),
            NametagRenderer.textColor(originalColor),
            NametagRenderer.textShadow(drawShadow),
            includeEmpty,
            NametagRenderer.backgroundColor(backgroundColor)
        );
    }
}
*///?} else {
public abstract class Mixin_WrapNametagRender262 {
}
//?}
