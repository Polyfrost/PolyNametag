package org.polyfrost.polynametag.mixin.client;

//? if >= 1.21.10 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
//?} else {
/*import net.minecraft.client.renderer.entity.EntityRenderer;
*///?}
//? if >= 1.21.10
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if <= 1.21.8 {
/*import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
*///?}

//? if >= 1.21.10 {
@Mixin(NameTagFeatureRenderer.Storage.class)
//?} else {
/*@Mixin(EntityRenderer.class)
*///?}
public abstract class Mixin_ApplyScaling {
    //? if >= 1.21.10 {
    @WrapOperation(method = "add", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"))
    public void applyScaling(PoseStack instance, float x, float y, float z, Operation<Void> original) {
        float scale = PolyNametagConfig.isEnabled() ? PolyNametagConfig.getScale() : 1.0F;
        original.call(instance, x * scale, y * scale, z * scale);
    }
    //?} else {
    /*@ModifyArgs(method = "renderNameTag", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"))
    private void applyScaling(Args args) {
        if (PolyNametagConfig.isEnabled()) {
            final float scale = PolyNametagConfig.getScale();
            args.set(0, ((float) args.get(0)) * scale);
            args.set(1, ((float) args.get(1)) * scale);
            args.set(2, ((float) args.get(2)) * scale);
        }
    }
    *///?}
}
