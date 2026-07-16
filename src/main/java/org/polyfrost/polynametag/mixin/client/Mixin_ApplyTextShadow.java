package org.polyfrost.polynametag.mixin.client;

//? if <= 1.21.8
//import net.minecraft.client.renderer.entity.EntityRenderer;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = /*? if >= 1.21.10 {*/ "net.minecraft.client.renderer.feature.NameTagFeatureRenderer" /*?} else {*/ /*"net.minecraft.client.renderer.entity.EntityRenderer" *//*?}*/)
public abstract class Mixin_ApplyTextShadow {
    @ModifyArg(method = /*? if >= 26.2 {*/ "prepareText" /*?} elif >= 26.1 {*/ /*"renderTranslucent" *//*?} elif >= 1.21.10 {*/ /*"render" *//*?} else {*/ /*"renderNameTag" *//*?}*/, at = @At(value = "INVOKE", target = /*? if >= 26.2 {*/ "Lnet/minecraft/client/gui/Font;prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;" /*?} elif >= 26.1 {*/ /*"Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4fc;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V" *//*?} elif >=1.21.8 {*/ /*"Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V" *//*?} else {*/ /*"Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I" *//*?}*/), index = 4)
    private /*? if >= 26.2 {*/ static /*?}*/ boolean applyTextShadow(boolean original) {
        return NametagRenderer.textShadow(original);
    }
}
