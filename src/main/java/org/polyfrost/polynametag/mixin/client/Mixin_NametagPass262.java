package org.polyfrost.polynametag.mixin.client;

//? if = 26.2 {
/*import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.phase.FeatureRenderPhase;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class Mixin_NametagPass262 {
    @Shadow @Final private LevelTargetBundle targets;
    @Shadow @Final private GameRenderer gameRenderer;

    @Unique private TextureTarget polynametag$opaqueDepth;
    @Unique private boolean polynametag$captureDepth;
    @Unique private boolean polynametag$depthCaptured;

    @Inject(
        method = "lambda$addMainPass$0",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;renderGroup(Lnet/minecraft/client/renderer/chunk/ChunkSectionLayerGroup;Lcom/mojang/blaze3d/textures/GpuSampler;)V"),
        slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/chunk/ChunkSectionLayerGroup;TRANSLUCENT:Lnet/minecraft/client/renderer/chunk/ChunkSectionLayerGroup;"))
    )
    private void polynametag$captureOpaqueDepth(CallbackInfo ci) {
        if (!polynametag$captureDepth) {
            return;
        }

        RenderTarget main = gameRenderer.mainRenderTarget();
        if (polynametag$opaqueDepth == null) {
            polynametag$opaqueDepth = new TextureTarget("PolyNametag Opaque Depth", main.width, main.height, true, GpuFormat.RGBA8_UNORM);
        } else if (polynametag$opaqueDepth.width != main.width || polynametag$opaqueDepth.height != main.height) {
            polynametag$opaqueDepth.resize(main.width, main.height);
        }
        polynametag$opaqueDepth.copyDepthFrom(main);
        polynametag$depthCaptured = true;
    }

    @Inject(method = "close", at = @At("TAIL"))
    private void polynametag$closeOpaqueDepth(CallbackInfo ci) {
        if (polynametag$opaqueDepth != null) {
            polynametag$opaqueDepth.destroyBuffers();
            polynametag$opaqueDepth = null;
        }
    }

    @Inject(method = "addAlwaysOnTopPass", at = @At("HEAD"))
    private void polynametag$addNametagPass(FrameGraphBuilder frame, FeatureRenderDispatcher.PreparedFrame featureFrame, GpuBufferSlice fog, CallbackInfo ci) {
        polynametag$captureDepth = false;
        if (!PolyNametagConfig.isEnabled()) {
            return;
        }

        PreparedFrameAccessor262 accessor = (PreparedFrameAccessor262) (Object) featureFrame;
        Map<FeatureRenderPhase<?>, Object> groups = accessor.polynametag$getGroupsByPhase();
        Map<FeatureRenderPhase<?>, Object> deferred = new LinkedHashMap<>();
        for (SubmitNodeCollection collection : accessor.polynametag$getSubmitNodeStorage().getSubmitsPerOrder().values()) {
            polynametag$defer(groups, deferred, collection.seeThroughNameTags);
            polynametag$defer(groups, deferred, collection.nameTags);
        }
        if (deferred.isEmpty()) {
            return;
        }

        polynametag$captureDepth = true;
        FramePass pass = frame.addPass("polynametag_nametags");
        targets.main = pass.readsAndWrites(targets.main);
        ResourceHandle<RenderTarget> mainTarget = targets.main;
        pass.executes(() -> {
            groups.putAll(deferred);
            RenderSystem.setShaderFog(fog);
            RenderTarget main = mainTarget.get();
            RenderSystem.outputColorTextureOverride = main.getColorTextureView();
            RenderSystem.outputDepthTextureOverride = polynametag$depthCaptured ? polynametag$opaqueDepth.getDepthTextureView() : main.getDepthTextureView();
            polynametag$depthCaptured = false;
            FeatureFrameContext context = accessor.polynametag$getContext();
            for (FeatureRenderPhase<?> phase : deferred.keySet()) {
                accessor.polynametag$executePhase(phase, context);
            }
            RenderSystem.outputColorTextureOverride = null;
            RenderSystem.outputDepthTextureOverride = null;
        });
    }

    @Unique
    private static void polynametag$defer(Map<FeatureRenderPhase<?>, Object> groups, Map<FeatureRenderPhase<?>, Object> deferred, FeatureRenderPhase<?> phase) {
        Object group = groups.remove(phase);
        if (group != null) {
            deferred.put(phase, group);
        }
    }
}
*///?} else {
public abstract class Mixin_NametagPass262 {
}
//?}
