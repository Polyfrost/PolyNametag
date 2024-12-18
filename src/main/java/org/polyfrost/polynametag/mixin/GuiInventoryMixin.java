package org.polyfrost.polynametag.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.EntityLivingBase;
import org.polyfrost.polynametag.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiInventory.class)
public class GuiInventoryMixin {

    @Inject(method = "drawEntityOnScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderEntityWithPosYaw(Lnet/minecraft/entity/Entity;DDDFF)Z"))
    private static void start(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent, CallbackInfo ci) {
        Minecraft.getMinecraft().getRenderManager().playerViewX = 0f;
        int brightness = 15728880;
        int x = brightness % 65536;
        int y = brightness / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) x, (float) y);
        NametagRenderer.setCurrentlyDrawingInventory(true);
    }

    @Inject(method = "drawEntityOnScreen", at = @At("TAIL"))
    private static void end(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent, CallbackInfo ci) {
        NametagRenderer.setCurrentlyDrawingInventory(false);
    }

}

