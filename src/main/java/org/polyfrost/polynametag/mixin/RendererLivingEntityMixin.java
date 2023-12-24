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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

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

    @Inject(
            method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;draw()V"
            )
    )
    private void cancel(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
        Tessellator.getInstance().getWorldRenderer().reset();
    }

    @Inject(
        method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GlStateManager;depthMask(Z)V",
            ordinal = 1,
            shift = At.Shift.AFTER
        )
    )
    private void polyNametag$drawBackground(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
        NametagRenderingKt.drawBackground(entity.getDisplayName().getFormattedText(), 0x20);
    }


    @ModifyArgs(
        method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/WorldRenderer;color(FFFF)Lnet/minecraft/client/renderer/WorldRenderer;"
        )
    )
    private void polyNametag$modifyBackgroundColorBehindWalls(Args args) {
        if (!ModConfig.INSTANCE.enabled) return;
        float[] color = NametagRenderingKt.getBackBackgroundGLColorOrEmpty();
        args.set(0, color[0]);
        args.set(1, color[1]);
        args.set(2, color[2]);
        args.set(3, color[3]);
    }

    @ModifyArg(
        method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/WorldRenderer;pos(DDD)Lnet/minecraft/client/renderer/WorldRenderer;"
        ),
        index = 2
    )
    private double polyNametag$modifyBackgroundZ(double z) {
        if (!NametagRenderingKt.shouldDrawBackground()) return z;
        return z + 0.01;
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
}
