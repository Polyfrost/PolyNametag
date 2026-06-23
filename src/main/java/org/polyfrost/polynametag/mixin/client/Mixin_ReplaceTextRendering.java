package org.polyfrost.polynametag.mixin.client;

//? if <= 1.21.8
//import net.minecraft.client.renderer.entity.EntityRenderer;
//? if >= 1.21.10
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

//? if >= 1.21.10 {
@Mixin(NameTagFeatureRenderer.Storage.class)
//?} else {
/*@Mixin(EntityRenderer.class)
*///?}
public abstract class Mixin_ReplaceTextRendering {
    @ModifyArg(method = /*? if >= 1.21.10 {*/ "add" /*?} else {*/ /*"renderNameTag" *//*?}*/, at = @At(value = "INVOKE", target = /*? if >= 26.1 {*/ /*"Lnet/minecraft/client/renderer/SubmitNodeStorage$NameTagSubmit;<init>(Lorg/joml/Matrix4fc;FFLnet/minecraft/network/chat/Component;IIID)V" *//*?} elif >= 1.21.10 {*/ "Lnet/minecraft/client/renderer/SubmitNodeStorage$NameTagSubmit;<init>(Lorg/joml/Matrix4f;FFLnet/minecraft/network/chat/Component;IIID)V" /*?} elif >=1.21.8 {*/ /*"Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V" *//*?} else {*/ /*"Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I" *//*?}*/), index = /*? if >= 1.21.10 {*/ 5 /*?} else {*/ /*3 *//*?}*/)
    private int replaceTextColor(int original) {
        return NametagRenderer.textColor(original);
    }
}
