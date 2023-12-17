package org.polyfrost.polynametag.mixin.essential;

import org.polyfrost.polynametag.config.ModConfig;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Pseudo
@Mixin(targets = "gg.essential.handlers.OnlineIndicator")
public class OnlineIndicatorMixin {

    @Dynamic("Essential")
    @ModifyArgs(
            remap = true,
            method = "drawNametagIndicator(Lgg/essential/universal/UMatrixStack;Lnet/minecraft/entity/Entity;Ljava/lang/String;I)V",
            at = @At(
                    remap = false,
                    value = "INVOKE",
                    target = "Lgg/essential/render/TextRenderTypeVertexConsumer;color(IIII)Lgg/essential/render/TextRenderTypeVertexConsumer;"
            )
    )
    private static void polyNametag$modifyNametagColor(Args args) {
        if (!ModConfig.INSTANCE.enabled) return;
        args.set(0, ModConfig.INSTANCE.getBackgroundColor().getRed());
        args.set(1, ModConfig.INSTANCE.getBackgroundColor().getGreen());
        args.set(2, ModConfig.INSTANCE.getBackgroundColor().getBlue());
        args.set(3, ModConfig.INSTANCE.getBackground() ? ModConfig.INSTANCE.getBackgroundColor().getAlpha() : 0);
    }
}
