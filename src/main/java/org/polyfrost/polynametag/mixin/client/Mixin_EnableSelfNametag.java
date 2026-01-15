package org.polyfrost.polynametag.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.deftu.omnicore.api.client.OmniClient;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class Mixin_EnableSelfNametag<T extends LivingEntity> {
    @ModifyReturnValue(method = /*? if >=1.21.8 {*/ "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z" /*?} else {*/ /*"shouldShowName(Lnet/minecraft/world/entity/LivingEntity;)Z" *//*?}*/, at = @At("RETURN"))
    private boolean enableSelfNametag(boolean original, T livingEntity) {
        if (PolyNametagConfig.isEnabled() && PolyNametagConfig.isShowOwnNametag() && livingEntity == OmniClient.getPlayer()) {
            return true;
        } else {
            return original;
        }
    }
}
