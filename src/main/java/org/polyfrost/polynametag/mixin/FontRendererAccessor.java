package org.polyfrost.polynametag.mixin;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FontRenderer.class)
public interface FontRendererAccessor {
    @Invoker
    void invokeResetStyles();

    @Invoker
    int invokeRenderString(String text, float x, float y, int color, boolean dropShadow);
}
