package org.polyfrost.polynametag.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.polyfrost.polynametag.NametagRenderer;
import org.polyfrost.polynametag.PolyNametag;
import org.polyfrost.polynametag.PolyNametagConfig;
import org.polyfrost.polynametag.render.NametagRenderingKt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = Render.class, priority = 1001)
public abstract class RenderMixin {

    @ModifyArg(
            method = "renderLivingLabel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V"
            ),
            index = 1
    )
    private float polyNametag$overrideY(float y) {
        if (!PolyNametagConfig.INSTANCE.getEnabled()) {
            return y;
        }

        return y + PolyNametagConfig.INSTANCE.getHeightOffset();
    }

    @ModifyArg(
            method = "renderLivingLabel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V",
                    ordinal = 1
            ),
            index = 0
    )
    private float polyNametag$fixPerspectiveRotation(float x) {
        return (!PolyNametag.INSTANCE.isPatcher() && PolyNametagConfig.INSTANCE.getEnabled() && Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) ? -x : x;
    }

    @ModifyArgs(
            method = "renderLivingLabel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V"
            )
    )
    private void polyNametag$modifyScale(Args args) {
        if (!PolyNametagConfig.INSTANCE.getEnabled()) {
            return;
        }

        float scale = PolyNametagConfig.INSTANCE.getScale();
        args.set(0, ((float) args.get(0)) * scale);
        args.set(1, ((float) args.get(1)) * scale);
        args.set(2, ((float) args.get(2)) * scale);
    }

    @Inject(
            method = "renderLivingLabel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;enableDepth()V",
                    shift = At.Shift.AFTER
            )
    )
    private void polyNametag$drawBackground(Entity entity, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        if (!PolyNametagConfig.INSTANCE.getEnabled()) {
            return;
        }

        NametagRenderingKt.setDrawingWithDepth(true);
        NametagRenderingKt.drawFrontBackground(str, entity);
    }

    @Inject(method = "renderLivingLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()V"))
    private void cancel(Entity entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        if (!PolyNametagConfig.INSTANCE.getEnabled()) {
            return;
        }

        Tessellator.getInstance().getWorldRenderer().reset();
    }

    @Inject(method = "renderLivingLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()V", shift = At.Shift.AFTER))
    private void drawBG(Entity entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        if (!PolyNametagConfig.INSTANCE.getEnabled()) {
            return;
        }

        if (NametagRenderer.isDrawingIndicator() && PolyNametagConfig.INSTANCE.getEssentialOffset()) GlStateManager.translate(5f, 0f, 0f);
        NametagRenderingKt.drawFrontBackground(str, PolyNametagConfig.INSTANCE.getBackgroundColor().red(), PolyNametagConfig.INSTANCE.getBackgroundColor().green(), PolyNametagConfig.INSTANCE.getBackgroundColor().blue(), NametagRenderingKt.getBackBackgroundAlpha(), entityIn);
    }

    @Inject(method = "renderLivingLabel", at = @At("HEAD"), cancellable = true)
    private void move(Entity entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        if (!PolyNametagConfig.INSTANCE.getEnabled()) {
            return;
        }

        NametagRenderer.setDrawingIndicator(NametagRenderer.isCurrentlyDrawingPlayerName() && NametagRenderer.canDrawEssentialIndicator(entityIn));
        NametagRenderer.setCurrentlyDrawingPlayerName(false);
        if (!NametagRenderer.isCurrentlyDrawingTags() && NametagRenderer.isCurrentlyDrawingWorld()) {
            //noinspection unchecked
            NametagRenderer.getNametags().add(new NametagRenderer.LabelItem((Render<Entity>) (Object) this, entityIn, str, x, y, z, maxDistance));
            ci.cancel();
        }
    }

    @Redirect(
            method = "renderLivingLabel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"
            )
    )
    private int polyNametag$renderString(FontRenderer fontRenderer, String text, int x, int y, int color) {
        if (!PolyNametagConfig.INSTANCE.getEnabled()) {
            return fontRenderer.drawString(text, x, y, color);
        }

        return NametagRenderingKt.drawStringWithoutZFighting(fontRenderer, text, x, y, color);
    }

    @Inject(method = "renderLivingLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;enableLighting()V"))
    private void essential(Entity entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        if (!PolyNametagConfig.INSTANCE.getEnabled()) return;
        PolyNametag instance = PolyNametag.INSTANCE;
        if (instance.isEssential() && NametagRenderer.isDrawingIndicator()) {
            NametagRenderer.drawEssentialIndicator(entityIn, str);
            NametagRenderer.setDrawingIndicator(false);
        }
    }

}
