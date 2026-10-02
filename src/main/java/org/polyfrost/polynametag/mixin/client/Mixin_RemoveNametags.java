package org.polyfrost.polynametag.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
//? if <= 1.21.8 {
/*import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
*///?} else {
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
//? if >= 26.1
import net.minecraft.client.renderer.state.level.CameraRenderState;
//? if >= 1.21.10 && < 26.1
//import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
//?}
import net.minecraft.client.Minecraft;
//? if >= 1.21.10 {
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
//?} elif >= 1.21.4 {
/*import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
*///?} else {
/*import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
*///?}
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public abstract class Mixin_RemoveNametags {
    //? if >= 1.21.4 {
    @Unique
    private boolean polynametag$shouldRenderNametag(EntityRenderState state) {
        if (!PolyNametagConfig.isEnabled()) return true;
        if (PolyNametagConfig.isRemoveNametags()) return false;
        if (/*? if >= 26.2 {*/ Minecraft.getInstance().gui.hud.isHidden() /*?} else {*/ /*!Minecraft.renderNames() *//*?}*/) {
            if (state instanceof ArmorStandRenderState) return !PolyNametagConfig.isHideArmorStandNametagsInHiddenHud();
            if (state instanceof /*? if >= 1.21.10 {*/ AvatarRenderState /*?} else {*/ /*PlayerRenderState *//*?}*/) return !PolyNametagConfig.isHidePlayerNametagsInHiddenHud();
            return !PolyNametagConfig.isHideEntityNametagsInHiddenHud();
        }
        return true;
    }
    //?} else {
    /*@Unique
    private boolean polynametag$shouldRenderNametag(Entity entity) {
        if (!PolyNametagConfig.isEnabled()) return true;
        if (PolyNametagConfig.isRemoveNametags()) return false;
        if (!Minecraft.renderNames()) {
            if (entity instanceof ArmorStand) return !PolyNametagConfig.isHideArmorStandNametagsInHiddenHud();
            if (entity instanceof Player) return !PolyNametagConfig.isHidePlayerNametagsInHiddenHud();
            return !PolyNametagConfig.isHideEntityNametagsInHiddenHud();
        }
        return true;
    }
    *///?}

    //? if >= 26.2 {
    @WrapWithCondition(method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitNameTag(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZILnet/minecraft/client/renderer/state/level/CameraRenderState;)V"))
    private boolean removeNametag(SubmitNodeCollector instance, PoseStack poseStack, Vec3 vec3, int yOffset, Component component, boolean seeThrough, int packedLight, CameraRenderState cameraRenderState, @Local(argsOnly = true) EntityRenderState state) {
        return polynametag$shouldRenderNametag(state);
    }
    //?} elif >= 26.1 {
    /*@WrapWithCondition(method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitNameTag(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/level/CameraRenderState;)V"))
    private boolean removeNametag(SubmitNodeCollector instance, PoseStack poseStack, Vec3 vec3, int yOffset, Component component, boolean seeThrough, int packedLight, double distanceToCameraSq, CameraRenderState cameraRenderState, @Local(argsOnly = true) EntityRenderState state) {
        return polynametag$shouldRenderNametag(state);
    }
    *///?} elif >= 1.21.10 {
    /*@WrapWithCondition(method = "submitNameTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitNameTag(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/CameraRenderState;)V"))
    private boolean removeNametag(SubmitNodeCollector instance, PoseStack poseStack, Vec3 vec3, int yOffset, Component component, boolean seeThrough, int packedLight, double distanceToCameraSq, CameraRenderState cameraRenderState, @Local(argsOnly = true) EntityRenderState state) {
        return polynametag$shouldRenderNametag(state);
    }
    *///?} elif >= 1.21.8 {
    /*@WrapWithCondition(method = "renderNameTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V"))
    private boolean removeNametagVoid(Font instance, Component component, float x, float y, int color, boolean shadow, Matrix4f matrix4f, MultiBufferSource multiBufferSource, Font.DisplayMode displayMode, int backgroundColor, int packedLight, @Local(argsOnly = true) EntityRenderState state) {
        return polynametag$shouldRenderNametag(state);
    }
    *///?} elif >= 1.21.4 {
    /*@WrapWithCondition(method = "renderNameTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I"))
    private boolean removeNametagInt(Font instance, Component component, float x, float y, int color, boolean shadow, Matrix4f matrix4f, MultiBufferSource multiBufferSource, Font.DisplayMode displayMode, int backgroundColor, int packedLight, @Local(argsOnly = true) EntityRenderState state) {
        return polynametag$shouldRenderNametag(state);
    }
    *///?} else {
    /*@WrapWithCondition(method = "renderNameTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I"))
    private boolean removeNametagInt(Font instance, Component component, float x, float y, int color, boolean shadow, Matrix4f matrix4f, MultiBufferSource multiBufferSource, Font.DisplayMode displayMode, int backgroundColor, int packedLight, @Local(argsOnly = true) Entity entity) {
        return polynametag$shouldRenderNametag(entity);
    }
    *///?}
}
