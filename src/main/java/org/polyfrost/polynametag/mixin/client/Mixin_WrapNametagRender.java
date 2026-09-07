package org.polyfrost.polynametag.mixin.client;

//? if < 1.21.10 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net.minecraft.client.renderer.entity.EntityRenderer")
public abstract class Mixin_WrapNametagRender {
    @WrapOperation(
        method = "renderNameTag",
        at = @At(
            value = "INVOKE",
			//~ if < 1.21.8 ')V' -> ')I'
            target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V"
        )
    )
    private void wrapNametagRender(Component str, float x, float y, int color, boolean dropShadow, Matrix4f pose, MultiBufferSource bufferSource, Font.DisplayMode displayMode, int backgroundColor, int packedLightCoords, Operation<Void> original) {
        original.call(
            str,
            x,
            NametagRenderer.translateY(y),
            NametagRenderer.textColor(color),
            NametagRenderer.textShadow(dropShadow),
            pose,
            bufferSource,
            displayMode,
            NametagRenderer.backgroundColor(backgroundColor),
            packedLightCoords
        );
    }
}
*///?} else {
public abstract class Mixin_WrapNametagRender {
}
//?}
