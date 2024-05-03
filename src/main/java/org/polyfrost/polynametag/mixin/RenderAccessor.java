package org.polyfrost.polynametag.mixin;

import net.minecraft.client.renderer.entity.Render;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Render.class)
public interface RenderAccessor<T extends net.minecraft.entity.Entity> {

    @Invoker("renderLivingLabel")
    void renderNametag(T entityIn, String str, double x, double y, double z, int maxDistance);

}