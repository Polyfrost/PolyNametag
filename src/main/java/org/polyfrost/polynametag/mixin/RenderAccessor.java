package org.polyfrost.polynametag.mixin;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Render.class)
public interface RenderAccessor {
    @Invoker
    boolean invokeCanRenderName(Entity entity);
}
