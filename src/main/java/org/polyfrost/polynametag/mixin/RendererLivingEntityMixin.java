package org.polyfrost.polynametag.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.polyfrost.polynametag.PolyNametag;
import org.polyfrost.polynametag.config.ModConfig;
import org.polyfrost.polynametag.render.NametagRenderingKt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static org.polyfrost.polynametag.render.NametagRenderingKt.drawFrontBackground;

@Mixin(value = RendererLivingEntity.class, priority = 1001)
public abstract class RendererLivingEntityMixin  {

    @Redirect(
        method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/entity/RenderManager;livingPlayer:Lnet/minecraft/entity/Entity;"
        )
    )
    private Entity polyNametag$cancelSelfCheck(RenderManager renderManager) {
        boolean shouldShowOwnNametag = (ModConfig.INSTANCE.enabled && ModConfig.INSTANCE.getShowOwnNametag());
        return shouldShowOwnNametag ? null : renderManager.livingPlayer;
    }

    @ModifyArg(
        method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V",
            ordinal = 0
        ),
        index = 1
    )
    private float polyNametag$overrideY(float y) {
        if (!ModConfig.INSTANCE.enabled) return y;
        return y + ModConfig.INSTANCE.getHeightOffset();
    }

    @ModifyArg(
        method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V",
            ordinal = 1
        ),
        index = 0
    )
    private float polyNametag$fixPerspectiveRotation(float x) {
        return (!PolyNametag.INSTANCE.isPatcher() && ModConfig.INSTANCE.enabled && Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) ? -x : x;
    }

    @Inject(
        method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GlStateManager;disableLighting()V"
        )
    )
    private void polyNametag$modifyScale(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enabled) return;
        float scale = ModConfig.INSTANCE.getScale();
        GlStateManager.scale(scale, scale, scale);
    }

    @Inject(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()V"))
    private void cancel(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
        Tessellator.getInstance().getWorldRenderer().reset();
    }

    @Inject(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()V", shift = At.Shift.AFTER))
    private void drawBG(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
        float[] color = NametagRenderingKt.getBackBackgroundGLColorOrEmpty();
        drawFrontBackground(entity.getDisplayName().getFormattedText(), new Color(color[0], color[1], color[2], color[3]), entity);
    }

    @Redirect(
        method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"
        )
    )
    private int polyNametag$renderDrawString(FontRenderer fontRenderer, String text, int x, int y, int color) {
        if (!ModConfig.INSTANCE.enabled) return fontRenderer.drawString(text, x, y, color);
        return NametagRenderingKt.drawStringWithoutZFighting(fontRenderer, text, x, y, color);
    }

    @Inject(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At("HEAD"), cancellable = true)
    private void move(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
        if (!PolyNametag.INSTANCE.getDrawing() && !ModConfig.INSTANCE.getNametagPreview().getDrawing()) {
            PolyNametag.INSTANCE.getNames().add(new PolyNametag.NameInfo((RendererLivingEntity<EntityLivingBase>) (Object) this, entity, x, y, z));
            ci.cancel();
        }
    }
}
