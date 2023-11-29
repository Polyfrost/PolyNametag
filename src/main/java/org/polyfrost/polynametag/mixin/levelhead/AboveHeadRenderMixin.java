package org.polyfrost.polynametag.mixin.levelhead;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import org.polyfrost.polynametag.config.ModConfig;
import org.polyfrost.polynametag.hooks.HooksKt;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.*;

/**
 * Taken from Patcher under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * https://github.com/Sk1erLLC/Patcher/blob/master/LICENSE.md
 */
@Pseudo
@Mixin(targets = "club.sk1er.mods.levelhead.render.AboveHeadRender", priority = 1001)
public abstract class AboveHeadRenderMixin {

    @Dynamic("LevelHead")
    @Redirect(method = "render(Lnet/minecraftforge/client/event/RenderLivingEvent$Specials$Post;)V", at = @At(value = "INVOKE", target = "Lclub/sk1er/mods/levelhead/render/AboveHeadRender;isSelf(Lnet/minecraft/entity/player/EntityPlayer;)Z", ordinal = 1), remap = false)
    private boolean polyNametag$screwYouLevelhead(@Coerce Object instance, EntityPlayer player) {
        return !ModConfig.INSTANCE.getShowOwnNametag() && Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.getUniqueID().equals(player.getUniqueID());
    }

    @Dynamic("LevelHead")
    @Redirect(method = "render(Lnet/minecraft/client/gui/FontRenderer;Lclub/sk1er/mods/levelhead/display/LevelheadTag$LevelheadComponent;I)V", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I", remap = true))
    private int polyNametag$modifyStringRendering(FontRenderer fontRenderer, String text, int x, int y, int color) {
        return HooksKt.drawStringWithoutZFighting(fontRenderer, text, x, y, color);
    }
}
