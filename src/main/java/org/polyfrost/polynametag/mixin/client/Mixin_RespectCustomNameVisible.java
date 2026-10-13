package org.polyfrost.polynametag.mixin.client;

//? if = 1.8.9 {
/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.entity.living.mob.MobEntity;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobRenderer.class)
public abstract class Mixin_RespectCustomNameVisible {
    @ModifyExpressionValue(method = "shouldRenderNameTag(Lnet/minecraft/entity/living/mob/MobEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/mob/MobEntity;shouldShowNameTag()Z"))
    private boolean respectCustomNameVisible(boolean original, @Local(argsOnly = true) MobEntity mob) {
        return original || (PolyNametagConfig.isEnabled() && mob.isCustomNameVisible());
    }
}
*///?} else {
public abstract class Mixin_RespectCustomNameVisible {
}
//?}
