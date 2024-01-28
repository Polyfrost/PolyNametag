package org.polyfrost.polynametag.mixin.essential;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import org.polyfrost.polynametag.config.ModConfig;
import org.polyfrost.polynametag.render.NametagRenderingKt;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
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
        int[] color = NametagRenderingKt.getBackBackgroundColorOrEmpty();
        args.set(0, color[0]);
        args.set(1, color[1]);
        args.set(2, color[2]);
        args.set(3, color[3]);
    }

    @Dynamic("Essential")
    @Inject(
        method = "drawNametagIndicator(Lgg/essential/universal/UMatrixStack;Lnet/minecraft/entity/Entity;Ljava/lang/String;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lgg/essential/universal/UGraphics;drawDirect()V",
            remap = false,
            ordinal = 1,
            shift = At.Shift.AFTER
        )
    )
    private static void polyNametag$drawFrontBackground(@Coerce Object matrices, Entity entity, String str, int light, CallbackInfo ci) {
        int x = -Minecraft.getMinecraft().fontRendererObj.getStringWidth(str) / 2;
        NametagRenderingKt.drawFrontBackground(x - 11, x - 1);
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
        if (!NametagRenderingKt.shouldDrawBackground()) return z;
        return z + 0.01;
    }
}
