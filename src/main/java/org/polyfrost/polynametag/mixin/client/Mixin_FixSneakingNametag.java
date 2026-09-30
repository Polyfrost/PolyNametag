package org.polyfrost.polynametag.mixin.client;

//? if = 1.8.9 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class Mixin_FixSneakingNametag {
    @WrapOperation(method = "renderNameTag(Lnet/minecraft/world/entity/LivingEntity;DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSneaking()Z", ordinal = 1))
    private boolean showCustomNametagWhilstSneaking(LivingEntity instance, Operation<Boolean> original) {
        return !PolyNametagConfig.isEnabled() && original.call(instance);
    }
}
*///?} else {
public abstract class Mixin_FixSneakingNametag {
}
//?}
