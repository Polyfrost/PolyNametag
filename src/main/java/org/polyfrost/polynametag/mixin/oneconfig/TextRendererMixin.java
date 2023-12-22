package org.polyfrost.polynametag.mixin.oneconfig;

import org.polyfrost.polynametag.render.NametagRenderingKt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "cc.polyfrost.oneconfig.renderer.TextRenderer", remap = false)
public class TextRendererMixin {

    @Inject(method = "isDrawingTextBorder", at = @At("HEAD"), cancellable = true)
    private static void isDrawingTextBorder(CallbackInfoReturnable<Boolean> cir) {
        if (NametagRenderingKt.isDrawingBorder()) {
            cir.setReturnValue(true);
        }
    }
}
