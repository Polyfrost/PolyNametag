package org.polyfrost.polynametag.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
//? if >= 26.2
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Allows nametags of living entities to stay visible while the HUD is hidden.
 * {@code LivingEntityRenderer.shouldShowName} suppresses nametags of teamless
 * living entities before the {@code EntityRenderer} injection point in
 * {@link Mixin_RemoveNametags} is ever reached, so the hidden-HUD check inside
 * it must be wrapped here.
 */
@Mixin(LivingEntityRenderer.class)
public abstract class Mixin_RemoveNametagsLiving<T extends LivingEntity> {
    //? if >= 26.2 {
    @WrapOperation(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;isHidden()Z"))
    private boolean hiddenHudVisibility(Hud instance, Operation<Boolean> original, @Local(argsOnly = true) T livingEntity) {
        boolean hidden = original.call(instance);
        if (!hidden || !PolyNametagConfig.isEnabled()) return hidden;
        if (livingEntity instanceof ArmorStand) return PolyNametagConfig.isHideArmorStandNametagsInHiddenHud();
        if (livingEntity instanceof Player) return PolyNametagConfig.isHidePlayerNametagsInHiddenHud();
        return PolyNametagConfig.isHideEntityNametagsInHiddenHud();
    }
    //?} else {
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
