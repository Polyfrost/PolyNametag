package org.polyfrost.polynametag.mixin.essential;

import gg.essential.universal.UMatrixStack;
import net.minecraft.entity.Entity;
import org.polyfrost.polynametag.PolyNametag;
import org.polyfrost.polynametag.config.ModConfig;
import org.polyfrost.polynametag.render.NametagRenderingKt;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Pseudo
@Mixin(targets = "gg.essential.handlers.OnlineIndicator")
public class OnlineIndicatorMixin {

    @Dynamic("Essential")
    @ModifyArgs(
        method = "drawNametagIndicator(Lgg/essential/universal/UMatrixStack;Lnet/minecraft/entity/Entity;Ljava/lang/String;I)V",
        at = @At(
            remap = false,
            value = "INVOKE",
            target = "Lgg/essential/render/TextRenderTypeVertexConsumer;color(IIII)Lgg/essential/render/TextRenderTypeVertexConsumer;"
        )
    )
    private static void polyNametag$modifyNametagColor(Args args) {
        if (!ModConfig.INSTANCE.enabled) return;
        args.set(3, 0);
    }

    @Dynamic("Essential")
    @Inject(method = "drawNametagIndicator", at = @At("HEAD"), cancellable = true)
    private static void skip(UMatrixStack matrixStack, Entity entity, String str, int light, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enabled) return;
        if (!PolyNametag.INSTANCE.getDrawingEssential()) ci.cancel();
    }

    @Dynamic("Essential")
    @ModifyArg(
        method = "drawNametagIndicator(Lgg/essential/universal/UMatrixStack;Lnet/minecraft/entity/Entity;Ljava/lang/String;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lgg/essential/universal/UGraphics;pos(Lgg/essential/universal/UMatrixStack;DDD)V",
            remap = false
        ),
        index = 3
    )
    private static double polyNametag$modifyBackgroundZ(double z) {
        if (!ModConfig.INSTANCE.enabled) return z;
        if (!NametagRenderingKt.shouldDrawBackground()) return z;
        return z + 0.01;
    }
}
