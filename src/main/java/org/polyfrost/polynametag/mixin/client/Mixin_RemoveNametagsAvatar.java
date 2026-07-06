package org.polyfrost.polynametag.mixin.client;

//? if >= 1.21.10 && < 26.1 {
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
//?} else {
/*import net.minecraft.client.renderer.entity.EntityRenderer;
*///?}
import org.spongepowered.asm.mixin.Mixin;

//? if >= 1.21.10 && < 26.1 {
@Mixin(AvatarRenderer.class)
//?} else {
/*@Mixin(EntityRenderer.class)
*///?}
public abstract class Mixin_RemoveNametagsAvatar {
    //? if >= 1.21.10 && < 26.1 {
    @Unique
    private boolean polynametag$shouldRenderNametag() {
        if (!PolyNametagConfig.isEnabled()) return true;
        if (PolyNametagConfig.isRemoveNametags()) return false;
        if (!Minecraft.renderNames()) return !PolyNametagConfig.isHidePlayerNametagsInHiddenHud();
        return true;
    }

    @WrapWithCondition(method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitNameTag(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/CameraRenderState;)V"))
    private boolean polynametag$removeNametag(SubmitNodeCollector instance, PoseStack poseStack, Vec3 vec3, int yOffset, Component component, boolean seeThrough, int packedLight, double distanceToCameraSq, CameraRenderState cameraRenderState) {
        return polynametag$shouldRenderNametag();
    }
    //?}
}
