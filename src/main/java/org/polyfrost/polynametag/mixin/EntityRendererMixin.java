package org.polyfrost.polynametag.mixin;

import net.minecraft.client.renderer.EntityRenderer;
import org.polyfrost.polynametag.PolyNametag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void start(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        PolyNametag.INSTANCE.setDrawingWorld(false);
    }

    @Inject(method = "renderWorld", at = @At("TAIL"))
    private void end(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        PolyNametag.INSTANCE.setDrawingWorld(true);
    }
}