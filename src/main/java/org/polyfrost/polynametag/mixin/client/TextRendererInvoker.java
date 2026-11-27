package org.polyfrost.polynametag.mixin.client;

//? if = 1.8.9 {
/*import net.minecraft.client.render.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TextRenderer.class)
public interface TextRendererInvoker {
    @Invoker("reset")
    void polynametag$reset();

    @Invoker("drawLayer")
    int polynametag$drawLayer(String text, float x, float y, int color, boolean shadow);
}
*///?} else {
public interface TextRendererInvoker {
}
//?}
