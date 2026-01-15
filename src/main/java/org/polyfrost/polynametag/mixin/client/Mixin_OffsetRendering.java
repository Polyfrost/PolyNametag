package org.polyfrost.polynametag.mixin.client;

//? if <= 1.21.8
/*import net.minecraft.client.renderer.entity.EntityRenderer;*/
//? if >= 1.21.10
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

//? if >= 1.21.10 {
@Mixin(NameTagFeatureRenderer.class)
//?} else {
/*@Mixin(EntityRenderer.class)
*///?}
public abstract class Mixin_OffsetRendering {
    @ModifyArg(method = /*? if >= 1.21.10 {*/ "render" /*?} else {*/ /*"renderNameTag" *//*?}*/, at = @At(value = "INVOKE", target = /*? if >=1.21.8 {*/ "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V" /*?} else {*/ /*"Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I" *//*?}*/), index = 2)
    private float modifyTranslateY(float original) {
        if (PolyNametagConfig.isEnabled()) {
            original -= PolyNametagConfig.getHeightOffset();
            // TODO: is this needed?
//            if (renderState.isDiscrete) {
//                original += 0.5F;
//            }
        }

        return original;
    }
}
