package org.polyfrost.polynametag.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class Mixin_FixSneakingNametag {
    // TODO: what is this for
    @WrapOperation(method = /*? if >=1.21.8 {*/ "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z" /*?} else {*/ /*"shouldShowName(Lnet/minecraft/world/entity/LivingEntity;)Z" *//*?}*/, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDiscrete()Z"))
    private boolean showCustomNametagWhilstSneaking(LivingEntity instance, Operation<Boolean> original) {
        if (PolyNametagConfig.isEnabled()) {
            return false;
        } else {
            return original.call(instance);
        }
    }
}