package org.polyfrost.polynametag.mixin.levelhead;

import cc.polyfrost.oneconfig.libs.universal.UGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import org.polyfrost.polynametag.config.ModConfig;
import org.polyfrost.polynametag.hooks.HooksKt;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * Taken from Patcher under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * https://github.com/Sk1erLLC/Patcher/blob/master/LICENSE.md
 */
@Pseudo
@Mixin(targets = "club.sk1er.mods.levelhead.render.AboveHeadRender", priority = 1001)
public class AboveHeadRenderMixin {

    @Dynamic("LevelHead")
    @Redirect(method = "render(Lnet/minecraftforge/client/event/RenderLivingEvent$Specials$Post;)V", at = @At(value = "INVOKE", target = "Lclub/sk1er/mods/levelhead/render/AboveHeadRender;isSelf(Lnet/minecraft/entity/player/EntityPlayer;)Z", ordinal = 1), remap = false)
    private boolean polyNametag$screwYouLevelhead(@Coerce Object instance, EntityPlayer player) {
        return !ModConfig.INSTANCE.getShowOwnNametag() && Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.getUniqueID().equals(player.getUniqueID());
    }

    @Dynamic("LevelHead")
    @Inject(method = "renderName", at = @At(value = "INVOKE", target = "Lgg/essential/universal/UGraphics$GL;translate(FFF)V"), remap = false)
    private void polyNametag$changeOffset(@Coerce Object tag, EntityPlayer player, double x, double y, double z, CallbackInfo ci) {
        UGraphics.GL.translate(0.0F, ModConfig.INSTANCE.getHeightOffset(), 0.0F);
    }

    @Dynamic("LevelHead")
    @ModifyArgs(method = "renderName", remap = false, at = @At(value = "INVOKE", target = "Lgg/essential/universal/UGraphics;color(FFFF)Lgg/essential/universal/UGraphics;"))
    private void polyNametag$changeBackgroundColor(Args args) {
        boolean background = ModConfig.INSTANCE.getBackground();
        args.set(0, background ? ModConfig.INSTANCE.getBackgroundColor().getRed() / 255.0F : 0.0F);
        args.set(1, background ? ModConfig.INSTANCE.getBackgroundColor().getGreen() / 255.0F : 0.0F);
        args.set(2, background ? ModConfig.INSTANCE.getBackgroundColor().getBlue() / 255.0F : 0.0F);
        args.set(3, background ? ModConfig.INSTANCE.getBackgroundColor().getAlpha() / 255.0F : 0.0F);
    }

    @Dynamic("LevelHead")
    @Redirect(method = "render(Lnet/minecraft/client/gui/FontRenderer;Lclub/sk1er/mods/levelhead/display/LevelheadTag$LevelheadComponent;I)V", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I", remap = true))
    private int polyNametag$modifyStringRendering(FontRenderer fontRenderer, String text, int x, int y, int color) {
        return HooksKt.drawStringWithoutZFighting(fontRenderer, text, x, y, color);
    }
}
