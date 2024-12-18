package org.polyfrost.polynametag.mixin;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.polyfrost.polynametag.NametagRenderer;
import org.polyfrost.polynametag.PolyNametag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class RenderPlayerMixin {

    @Inject(method = "renderOffsetLivingLabel(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDLjava/lang/String;FD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RendererLivingEntity;renderOffsetLivingLabel(Lnet/minecraft/entity/Entity;DDDLjava/lang/String;FD)V"))
    private void preEssential(AbstractClientPlayer entityIn, double x, double y, double z, String str, float p_177069_9_, double p_177069_10_, CallbackInfo ci) {
        NametagRenderer.setCurrentlyDrawingPlayerName(true);
    }

}
