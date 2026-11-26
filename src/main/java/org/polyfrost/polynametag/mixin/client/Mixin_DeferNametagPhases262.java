package org.polyfrost.polynametag.mixin.client;

//? if = 26.2 {
/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.phase.SimpleFeatureRenderPhase;
import net.minecraft.client.renderer.feature.phase.TranslucentFeatureRenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FeatureRenderDispatcher.PreparedFrame.class)
public abstract class Mixin_DeferNametagPhases262 {
    @Unique
    private static final TranslucentFeatureRenderPhase polynametag$EMPTY_SEE_THROUGH = new TranslucentFeatureRenderPhase();

    @Unique
    private static final SimpleFeatureRenderPhase polynametag$EMPTY_NAMETAGS = new SimpleFeatureRenderPhase();

    @ModifyExpressionValue(method = "executeTranslucent", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/SubmitNodeCollection;seeThroughNameTags:Lnet/minecraft/client/renderer/feature/phase/TranslucentFeatureRenderPhase;"))
    private TranslucentFeatureRenderPhase polynametag$skipSeeThroughNametags(TranslucentFeatureRenderPhase original) {
        return polynametag$EMPTY_SEE_THROUGH;
    }

    @ModifyExpressionValue(method = "executeTranslucent", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/SubmitNodeCollection;nameTags:Lnet/minecraft/client/renderer/feature/phase/SimpleFeatureRenderPhase;"))
    private SimpleFeatureRenderPhase polynametag$skipNametags(SimpleFeatureRenderPhase original) {
        return polynametag$EMPTY_NAMETAGS;
    }
}
*///?} else {
public abstract class Mixin_DeferNametagPhases262 {
}
//?}
