package org.polyfrost.polynametag.mixin.client;

//? if = 26.2 {
/*import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class Mixin_NametagPass262 {
    @Shadow @Final private LevelTargetBundle targets;

    @Inject(method = "addAlwaysOnTopPass", at = @At("HEAD"))
    private void polynametag$addNametagPass(FrameGraphBuilder frame, FeatureRenderDispatcher.PreparedFrame featureFrame, GpuBufferSlice fog, CallbackInfo ci) {
        PreparedFrameAccessor262 accessor = (PreparedFrameAccessor262) (Object) featureFrame;
        if (!polynametag$hasNametags(accessor)) {
            return;
        }

        FramePass pass = frame.addPass("polynametag_nametags");
        targets.main = pass.readsAndWrites(targets.main);
        ResourceHandle<RenderTarget> mainTarget = targets.main;
        pass.executes(() -> {
            RenderSystem.setShaderFog(fog);
            RenderTarget main = mainTarget.get();
            RenderSystem.outputColorTextureOverride = main.getColorTextureView();
            RenderSystem.outputDepthTextureOverride = main.getDepthTextureView();
            FeatureFrameContext context = accessor.polynametag$getContext();
            for (SubmitNodeCollection collection : accessor.polynametag$getSubmitNodeStorage().getSubmitsPerOrder().values()) {
                accessor.polynametag$executePhase(collection.seeThroughNameTags, context);
                accessor.polynametag$executePhase(collection.nameTags, context);
            }
            RenderSystem.outputColorTextureOverride = null;
            RenderSystem.outputDepthTextureOverride = null;
        });
    }

    @Unique
    private static boolean polynametag$hasNametags(PreparedFrameAccessor262 accessor) {
        Map<?, ?> groups = accessor.polynametag$getGroupsByPhase();
        for (SubmitNodeCollection collection : accessor.polynametag$getSubmitNodeStorage().getSubmitsPerOrder().values()) {
            if (groups.containsKey(collection.seeThroughNameTags) || groups.containsKey(collection.nameTags)) {
                return true;
            }
        }
        return false;
    }
}
*///?} else {
public abstract class Mixin_NametagPass262 {
}
//?}
