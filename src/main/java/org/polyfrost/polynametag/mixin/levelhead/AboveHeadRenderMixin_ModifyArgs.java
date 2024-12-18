package org.polyfrost.polynametag.mixin.levelhead;

import org.polyfrost.polynametag.PolyNametagConfig;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * Taken from Patcher under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * https://github.com/Sk1erLLC/Patcher/blob/master/LICENSE.md
 */
@Pseudo
@Mixin(targets = "club.sk1er.mods.levelhead.render.AboveHeadRender", priority = 999)
public abstract class AboveHeadRenderMixin_ModifyArgs {

    @Dynamic("LevelHead")
    @ModifyArg(method = "renderName", at = @At(value = "INVOKE", target = "Lgg/essential/universal/UGraphics$GL;translate(FFF)V"), index = 1, remap = false)
    private float polyNametag$changeOffset(float original) {
        if (!PolyNametagConfig.INSTANCE.getEnabled()) return original;
        return original + PolyNametagConfig.INSTANCE.getHeightOffset();
    }

    @Dynamic("LevelHead")
    @ModifyArgs(method = "renderName", at = @At(value = "INVOKE", target = "Lgg/essential/universal/UGraphics$GL;scale(DDD)V"), remap = false)
    private void polyNametag$changeScale(Args args) {
        if (!PolyNametagConfig.INSTANCE.getEnabled()) return;
        double scale = PolyNametagConfig.INSTANCE.getScale();
        args.set(0, ((double) args.get(0)) * scale);
        args.set(1, ((double) args.get(1)) * scale);
        args.set(2, ((double) args.get(2)) * scale);
    }

    @Dynamic("LevelHead")
    @ModifyArgs(method = "renderName", remap = false, at = @At(value = "INVOKE", target = "Lgg/essential/universal/UGraphics;color(FFFF)Lgg/essential/universal/UGraphics;"))
    private void polyNametag$changeBackgroundColor(Args args) {
        if (!PolyNametagConfig.INSTANCE.getEnabled()) return;
        args.set(3, 0f);
    }

}
