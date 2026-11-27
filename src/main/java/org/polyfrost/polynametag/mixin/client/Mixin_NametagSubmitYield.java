package org.polyfrost.polynametag.mixin.client;

//? if >= 1.21.10 && < 26.3 {
/*import org.polyfrost.polynametag.client.BackgroundYieldHolder;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >= 26.2
@Mixin(targets = "net.minecraft.client.renderer.feature.NameTagFeatureRenderer$Submit")
//? if < 26.2
//@Mixin(targets = "net.minecraft.client.renderer.SubmitNodeStorage$NameTagSubmit")
public abstract class Mixin_NametagSubmitYield implements BackgroundYieldHolder {
    @Unique
    private boolean polynametag$backgroundYielded;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void polynametag$captureYield(CallbackInfo ci) {
        polynametag$backgroundYielded = NametagRenderer.getBackgroundYielded();
    }

    @Override
    public boolean polynametag$backgroundYielded() {
        return polynametag$backgroundYielded;
    }
}
*///?} else {
public abstract class Mixin_NametagSubmitYield {
}
//?}
