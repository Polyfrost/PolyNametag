package org.polyfrost.polynametag.mixin;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.polyfrost.polynametag.render.NametagRenderingKt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Render.class, priority = 1001)
public abstract class RenderMixin {
    @Inject(method = "renderLivingLabel", at = @At("HEAD"), cancellable = true)
    private void polyNametag$overrideNametag(Entity entity, String displayName, double x, double y, double z, int range, CallbackInfo callbackInfo) {
        if (NametagRenderingKt.overrideNametag(entity, displayName, x, y, z, range)) {
            callbackInfo.cancel();
        }
    }
}
