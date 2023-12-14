package org.polyfrost.polynametag.mixin;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.polyfrost.polynametag.config.ModConfig;
import org.polyfrost.polynametag.render.NametagPreview;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RendererLivingEntity.class, priority = 1001)
public abstract class RendererLivingEntityMixin extends Render<EntityLivingBase> {
    private RendererLivingEntityMixin(RenderManager renderManager) {
        super(renderManager);
    }

    @Inject(
        method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/RendererLivingEntity;getFontRendererFromRenderManager()Lnet/minecraft/client/gui/FontRenderer;"
        ),
        cancellable = true
    )
    private void polyNametag$overrideSneakingNametag(EntityLivingBase entity, double x, double y, double z, CallbackInfo callbackInfo) {
        if (!ModConfig.INSTANCE.enabled) return;
        if (entity.isChild()) y -= entity.height / 2.0;
        renderLivingLabel(entity, entity.getDisplayName().getFormattedText(), x, y, z, 32);
        callbackInfo.cancel();
    }

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

    @Inject(method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z", at = @At("HEAD"), cancellable = true)
    private void CancelPreview(CallbackInfoReturnable<Boolean> cir) {
        if (NametagPreview.INSTANCE.getCancelName()) {
            cir.setReturnValue(false);
        }
    }
}
