package org.polyfrost.polynametag.mixin.client;

//? if = 1.8.9 {
/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class Mixin_FixSneakingNametag {
    @ModifyExpressionValue(method = "renderNameTag(Lnet/minecraft/world/entity/LivingEntity;DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSneaking()Z", ordinal = 1))
    private boolean showCustomNametagWhilstSneaking(boolean original) {
        return original && !PolyNametagConfig.isEnabled();
    }

    @ModifyExpressionValue(method = "renderNameTag(Lnet/minecraft/world/entity/LivingEntity;DDD)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;cameraPitch:F"))
    private float polynametag$faceCamera(float pitch) {
        return NametagRenderer.cameraPitch(pitch);
    }
}
*///?} else {
public abstract class Mixin_FixSneakingNametag {
}
//?}
