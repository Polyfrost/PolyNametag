package org.polyfrost.polynametag.mixin;

import net.minecraft.client.renderer.EntityRenderer;
import org.polyfrost.polynametag.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void start(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        NametagRenderer.setCurrentlyDrawingWorld(true);
    }

    @Inject(method = "renderWorld", at = @At("TAIL"))
    private void end(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        NametagRenderer.setCurrentlyDrawingWorld(false);
    }

    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V", shift = At.Shift.AFTER))
    private void draw(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        NametagRenderer.renderAll();
    }

}
