package org.polyfrost.polynametag.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.polyfrost.polynametag.PolyNametag;
import org.polyfrost.polynametag.config.ModConfig;
import org.polyfrost.polynametag.render.NametagRenderingKt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RendererLivingEntity.class, priority = 1001)
public abstract class RendererLivingEntityMixin  {

    @Unique
    private boolean shouldShowOwnNametag;

    @Redirect(
            method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isGuiEnabled()Z")
    )
    private boolean gui() {
        shouldShowOwnNametag = ((ModConfig.INSTANCE.enabled && ModConfig.INSTANCE.getShowOwnNametag() && (ModConfig.INSTANCE.getShowInInventory() || !PolyNametag.INSTANCE.getDrawingInventory()) && (!PolyNametag.INSTANCE.getDrawingWorld() || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0)) || ModConfig.INSTANCE.getNametagPreview().getDrawing());
        return shouldShowOwnNametag || Minecraft.isGuiEnabled();
    }

    @Redirect(
        method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/entity/RenderManager;livingPlayer:Lnet/minecraft/entity/Entity;"
        )
    )
    private Entity polyNametag$cancelSelfCheck(RenderManager renderManager) {
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
        if (!ModConfig.INSTANCE.enabled) return;
        Tessellator.getInstance().getWorldRenderer().reset();
    }

    @Inject(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()V", shift = At.Shift.AFTER))
    private void drawBG(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enabled) return;
        if (PolyNametag.INSTANCE.getShouldDrawIndicator() && ModConfig.INSTANCE.getEssentialOffset()) GlStateManager.translate(5f, 0f, 0f);
        NametagRenderingKt.drawFrontBackground(entity.getDisplayName().getFormattedText(), ModConfig.INSTANCE.getBackgroundColor().getRed(), ModConfig.INSTANCE.getBackgroundColor().getGreen(), ModConfig.INSTANCE.getBackgroundColor().getBlue(), NametagRenderingKt.getBackBackgroundAlpha(), entity);
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
        if (!ModConfig.INSTANCE.enabled) return;
        PolyNametag.INSTANCE.setShouldDrawIndicator(NametagRenderingKt.canDrawIndicator(entity));
        if (!PolyNametag.INSTANCE.getDrawingTags() && PolyNametag.INSTANCE.getDrawingWorld()) {
            PolyNametag.INSTANCE.getNametags().add(new PolyNametag.NameInfo((RendererLivingEntity<EntityLivingBase>) (Object) this, entity, x, y, z));
            ci.cancel();
        }
    }

    @Inject(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;enableLighting()V"))
    private void essential(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enabled) return;
        PolyNametag instance = PolyNametag.INSTANCE;
        if (instance.isEssential() && instance.getShouldDrawIndicator()) {
            NametagRenderingKt.drawIndicator(entity, entity.getDisplayName().getFormattedText(), (((int) OpenGlHelper.lastBrightnessY) << 16) + (int) OpenGlHelper.lastBrightnessX);
            instance.setShouldDrawIndicator(false);
        }
    }
}
