package org.polyfrost.polynametag.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
//? if 1.21.1
//import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
//? if >= 1.21.10
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
//? if >= 1.21.4
import net.minecraft.client.renderer.entity.state.EntityRenderState;
//? if >= 1.21.4 && < 1.21.10
//import net.minecraft.client.renderer.entity.state.PlayerRenderState;
//? if 1.21.1
//import net.minecraft.world.entity.Entity;
//? if >= 1.21.4 {
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
//?}
import org.polyfrost.polynametag.client.NametagRenderer;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public abstract class Mixin_ShowInInventory /*? if >= 1.21.4 {*/ <S extends EntityRenderState> /*?}*/ {
    //? if >= 1.21.4 {
    @WrapOperation(method = /*? if >= 1.21.10 {*/ "submitNameTag" /*?} else {*/ /*"renderNameTag" *//*?}*/, at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/EntityRenderState;nameTagAttachment:Lnet/minecraft/world/phys/Vec3;", opcode = Opcodes.GETFIELD))
    private Vec3 hideNametagInInventory(S instance, Operation<Vec3> original) {
        if (PolyNametagConfig.isEnabled() &&
                !PolyNametagConfig.isShowInInventory() &&
                NametagRenderer.isInventoryScreenOpen() &&
                instance instanceof /*? if >= 1.21.10 {*/ AvatarRenderState /*?} else {*/ /*PlayerRenderState *//*?}*/) {
            return null;
        } else {
            return original.call(instance);
        }
    }
    //?} else {
    /*@WrapOperation(method = "renderNameTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;distanceToSqr(Lnet/minecraft/world/entity/Entity;)D"))
    private double hideNametagInInventory(EntityRenderDispatcher instance, Entity entity, Operation<Double> original) {
        if (PolyNametagConfig.isEnabled() &&
                !PolyNametagConfig.isShowInInventory() &&
                NametagRenderer.isInventoryScreenOpen() &&
                entity == NametagRenderer.currentPlayer()) {
            return Integer.MAX_VALUE;
        } else {
            return original.call(instance, entity);
        }
    }
    *///?}
}
