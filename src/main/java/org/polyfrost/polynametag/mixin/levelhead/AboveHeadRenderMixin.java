package org.polyfrost.polynametag.mixin.levelhead;

import club.sk1er.mods.levelhead.display.LevelheadTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.polyfrost.polynametag.PolyNametag;
import org.polyfrost.polynametag.config.ModConfig;
import org.polyfrost.polynametag.render.NametagRenderingKt;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

/**
 * Taken from Patcher under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * https://github.com/Sk1erLLC/Patcher/blob/master/LICENSE.md
 */
@Pseudo
@Mixin(targets = "club.sk1er.mods.levelhead.render.AboveHeadRender", priority = 1001, remap = false)
public abstract class AboveHeadRenderMixin {

    @Dynamic("LevelHead")
    @Inject(method = "renderName", at = @At("HEAD"))
    private void move(LevelheadTag tag, EntityPlayer entityIn, double x, double y, double z, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enabled) return;
        PolyNametag.INSTANCE.setDrawEssential(false);
    }

    //@Dynamic("LevelHead")
    //@ModifyVariable(method = "renderName", at = @At("STORE"), name = "xMultiplier")
    //private int stupid(int value) {
    //    return PolyNametag.INSTANCE.isPatcher() ? 1 : value;
    //}

    @Dynamic("LevelHead")
    @Redirect(
        method = "render(Lnet/minecraftforge/client/event/RenderLivingEvent$Specials$Post;)V",
        at = @At(
            value = "INVOKE",
            target = "Lclub/sk1er/mods/levelhead/render/AboveHeadRender;isSelf(Lnet/minecraft/entity/player/EntityPlayer;)Z",
            ordinal = 1
        )
    )
    private boolean polyNametag$screwYouLevelhead(@Coerce Object instance, EntityPlayer player) {
        return !(ModConfig.INSTANCE.enabled && ModConfig.INSTANCE.getShowOwnNametag()) && Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.getUniqueID().equals(player.getUniqueID());
    }

    @Dynamic("LevelHead")
    @Redirect(
        method = "render(Lnet/minecraft/client/gui/FontRenderer;Lclub/sk1er/mods/levelhead/display/LevelheadTag$LevelheadComponent;I)V",
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
    @Inject(method = "renderName", at = @At(value = "INVOKE", target = "Lgg/essential/universal/UGraphics;drawDirect()V", shift = At.Shift.AFTER))
    private void drawBG(LevelheadTag tag, EntityPlayer entityIn, double x, double y, double z, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enabled) return;
        int stringWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(tag.getString()) / 2;
        float[] color = NametagRenderingKt.getBackBackgroundGLColorOrEmpty();
        NametagRenderingKt.drawFrontBackground(-stringWidth - 2, stringWidth + 1, new Color(color[0], color[1], color[2], color[3]), entityIn);
        GlStateManager.enableDepth();
        NametagRenderingKt.drawFrontBackground(-stringWidth - 2, stringWidth + 1, entityIn);
        GlStateManager.depthMask(true);
    }
}
