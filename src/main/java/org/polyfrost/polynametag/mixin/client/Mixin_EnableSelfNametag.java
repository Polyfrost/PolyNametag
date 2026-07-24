package org.polyfrost.polynametag.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class Mixin_EnableSelfNametag<T extends LivingEntity> {
    @ModifyReturnValue(method = /*? if >=1.21.4 {*/ "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z" /*?} else {*/ /*"shouldShowName(Lnet/minecraft/world/entity/LivingEntity;)Z" *//*?}*/, at = @At("RETURN"))
    private boolean enableSelfNametag(boolean original, T livingEntity) {
        if (PolyNametagConfig.isEnabled() && PolyNametagConfig.isShowOwnNametag() && livingEntity == NametagRenderer.currentPlayer()) {
            return !NametagRenderer.hasServerNametag(livingEntity);
        } else {
            return original;
        }
    }

    //? if < 26.2 {
    /*@WrapOperation(method = /^? if >=1.21.4 {^/ "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z" /^?} else {^/ /^"shouldShowName(Lnet/minecraft/world/entity/LivingEntity;)Z" ^//^?}^/, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;renderNames()Z"))
    private boolean hiddenHudVisibility(Operation<Boolean> original, @Local(argsOnly = true) T livingEntity) {
        boolean renderNames = original.call();
        if (renderNames || !PolyNametagConfig.isEnabled()) return renderNames;
        if (livingEntity instanceof ArmorStand) return !PolyNametagConfig.isHideArmorStandNametagsInHiddenHud();
        if (livingEntity instanceof Player) return !PolyNametagConfig.isHidePlayerNametagsInHiddenHud();
        return !PolyNametagConfig.isHideEntityNametagsInHiddenHud();
    }
    *///?}
}
