package org.polyfrost.polynametag.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
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

import java.awt.*;

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
        if (!ModConfig.INSTANCE.enabled) return y;
        return y + ModConfig.INSTANCE.getHeightOffset();
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
        return (!PolyNametag.INSTANCE.isPatcher() && ModConfig.INSTANCE.enabled && Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) ? -x : x;
    }

    @ModifyArgs(
            method = "renderLivingLabel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V"
            )
    )
    private void polyNametag$modifyScale(Args args) {
        if (!ModConfig.INSTANCE.enabled) return;
        float scale = ModConfig.INSTANCE.getScale();
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
        if (!ModConfig.INSTANCE.enabled) return;
        NametagRenderingKt.drawFrontBackground(str, entity);
    }

    @Inject(method = "renderLivingLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()V"))
    private void cancel(Entity entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enabled) return;
        Tessellator.getInstance().getWorldRenderer().reset();
    }

    @Inject(method = "renderLivingLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()V", shift = At.Shift.AFTER))
    private void drawBG(Entity entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enabled) return;
        float[] color = NametagRenderingKt.getBackBackgroundGLColorOrEmpty();
        if (PolyNametag.INSTANCE.getDrawEssential() && ModConfig.INSTANCE.getEssentialOffset()) GlStateManager.translate(5f, 0f, 0f);
        NametagRenderingKt.drawFrontBackground(str, new Color(color[0], color[1], color[2], color[3]), entityIn);
    }

    @Inject(method = "renderLivingLabel", at = @At("HEAD"), cancellable = true)
    private void move(Entity entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enabled) return;
        if (!PolyNametag.INSTANCE.getDrawing() && !PolyNametag.INSTANCE.getDrawingInGUI()) {
            PolyNametag.INSTANCE.getLabels().add(new PolyNametag.LabelInfo((Render<Entity>) (Object) this, entityIn, str, x, y, z, maxDistance));
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
        if (!ModConfig.INSTANCE.enabled) return fontRenderer.drawString(text, x, y, color);
        return NametagRenderingKt.drawStringWithoutZFighting(fontRenderer, text, x, y, color);
    }

    @Inject(method = "renderLivingLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;enableLighting()V"))
    private void essential(Entity entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enabled) return;
        PolyNametag instance = PolyNametag.INSTANCE;
        if (instance.isEssential() && instance.getDrawEssential()) {
            PolyNametag.INSTANCE.setDrawingEssential(true);
            NametagRenderingKt.drawIndicator(entityIn, str);
            PolyNametag.INSTANCE.setDrawingEssential(false);
        }
    }

}
