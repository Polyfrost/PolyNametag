package org.polyfrost.polynametag.mixin.levelhead;

import club.sk1er.mods.levelhead.display.LevelheadTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.polyfrost.polynametag.config.ModConfig;
import org.polyfrost.polynametag.render.NametagRenderingKt;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Taken from Patcher under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * https://github.com/Sk1erLLC/Patcher/blob/master/LICENSE.md
 */
@Pseudo
@Mixin(targets = "club.sk1er.mods.levelhead.render.AboveHeadRender", priority = 1001)
public abstract class AboveHeadRenderMixin {

    @Dynamic("LevelHead")
    @Redirect(
        method = "render(Lnet/minecraftforge/client/event/RenderLivingEvent$Specials$Post;)V",
        at = @At(
            value = "INVOKE",
            target = "Lclub/sk1er/mods/levelhead/render/AboveHeadRender;isSelf(Lnet/minecraft/entity/player/EntityPlayer;)Z",
            ordinal = 1
        ),
        remap = false
    )
    private boolean polyNametag$screwYouLevelhead(@Coerce Object instance, EntityPlayer player) {
        return !(ModConfig.INSTANCE.enabled && ModConfig.INSTANCE.getShowOwnNametag()) && Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.getUniqueID().equals(player.getUniqueID());
    }

    @Dynamic("LevelHead")
    @Redirect(
        method = "render(Lnet/minecraft/client/gui/FontRenderer;Lclub/sk1er/mods/levelhead/display/LevelheadTag$LevelheadComponent;I)V",
        remap = false,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I",
            remap = true
        )
    )
    private int polyNametag$modifyStringRendering(FontRenderer fontRenderer, String text, int x, int y, int color) {
        if (!ModConfig.INSTANCE.enabled) return fontRenderer.drawString(text, x, y, color);
        return NametagRenderingKt.drawStringWithoutZFighting(fontRenderer, text, x, y, color);
    }

    @Dynamic("LevelHead")
    @Inject(
        method = "renderName",
        remap = false,
        at = @At(
            value = "INVOKE",
            target = "Lgg/essential/universal/UGraphics;enableLighting()V"
        )
    )
    private void polyNametag$drawFrontBackground(LevelheadTag tag, EntityPlayer player, double x, double y, double z, CallbackInfo ci) {
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        int stringWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(tag.getString()) / 2;
        NametagRenderingKt.drawBackground(-stringWidth - 2, stringWidth + 1, 0xFF);
    }
}
