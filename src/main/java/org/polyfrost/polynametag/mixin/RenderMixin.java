package org.polyfrost.polynametag.mixin;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.polyfrost.polynametag.hooks.HooksKt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Render.class)
public class RenderMixin {
    @Inject(method = "renderName(Lnet/minecraft/entity/Entity;DDD)V", at = @At("HEAD"))
    private void polyNametag$renderName(Entity entity, double x, double y, double z, CallbackInfo callbackInfo) {
        HooksKt.callback((RenderAccessor) this, entity, x, y, z, callbackInfo);
    }
}
