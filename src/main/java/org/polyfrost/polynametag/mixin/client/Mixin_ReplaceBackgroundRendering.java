package org.polyfrost.polynametag.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//? if <= 1.21.8
/*import com.llamalad7.mixinextras.sugar.Local;*/
import com.mojang.blaze3d.vertex.PoseStack;
import dev.deftu.omnicore.api.client.render.stack.OmniPoseStacks;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
//? if <= 1.21.8
/*import net.minecraft.client.renderer.entity.EntityRenderer;*/
//? if >= 1.21.10
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import net.minecraft.network.chat.Component;
//? if 1.21.1
/*import net.minecraft.world.entity.Entity;*/
import org.joml.Matrix4f;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if >= 1.21.10 {
@Mixin(NameTagFeatureRenderer.class)
//?} else {
/*@Mixin(EntityRenderer.class)
*///?}
public abstract class Mixin_ReplaceBackgroundRendering/*? if 1.21.1 {*//*<T extends Entity>*//*?}*/ {
    @WrapOperation(method = /*? if >= 1.21.10 {*/ "render" /*?} else {*/ /*"renderNameTag" *//*?}*/, at = @At(value = "INVOKE", target = /*? if >=1.21.8 {*/ "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V" /*?} else {*/ /*"Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I" *//*?}*/, ordinal = 0))
    private /*? if >= 1.21.8 {*/ void /*?} else {*/ /*int *//*?}*/ renderCustomBackground(Font instance, Component component, float x, float y, int color, boolean shadow, Matrix4f matrix4f, MultiBufferSource multiBufferSource, Font.DisplayMode displayMode, int backgroundColor, int packedLight, Operation</*? if >= 1.21.8 {*/ Void/*?} else {*//*Integer*//*?}*/> original /*? if <= 1.21.8 {*//*, @Local(argsOnly = true) PoseStack pose *//*?}*/ /*? if 1.21.1 {*//*, @Local(argsOnly = true) T entity *//*?}*/) {
        if (PolyNametagConfig.isEnabled()) {
            //? if >= 1.21.10 {
            PoseStack pose = new PoseStack();
            pose.pushPose();
            pose.mulPose(matrix4f);
            //?}
            NametagRenderer.drawBackground(OmniPoseStacks.vanilla(pose), /*? if >= 1.21.8 {*/ component /*?} else {*/ /*entity *//*?}*/);
            //? if >= 1.21.10
            pose.popPose();
            //? if 1.21.1
            /*return 0;*/
        } else {
            //? if 1.21.1
            /*return*/
            original.call(instance, component, x, y, color, shadow, matrix4f, multiBufferSource, displayMode, backgroundColor, packedLight);
        }
    }
}