package org.polyfrost.polynametag.mixin.client;

//? if > 1.8.9 && < 26.2 {
/*import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.LevelRenderer;
import org.polyfrost.polynametag.client.NametagDeferral;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >= 1.21.2 {
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.LevelTargetBundle;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
//?}
//? if >= 1.21.8 {
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import org.spongepowered.asm.mixin.injection.Slice;
//?} elif >= 1.21.2 {
/^import net.minecraft.client.renderer.FogParameters;
^///?}
//? if < 1.21.8
//import net.minecraft.client.renderer.RenderType;

@Mixin(LevelRenderer.class)
public abstract class Mixin_DeferNametags {
    //? if >= 1.21.2 {
    @Shadow @Final private LevelTargetBundle targets;
    //?}

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void polynametag$beginLevel(CallbackInfo ci) {
        NametagDeferral.beginLevel();
    }

    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void polynametag$endLevel(CallbackInfo ci) {
        NametagDeferral.endLevel();
    }

    //? if >= 1.21.8 {
    @Inject(
        //~ if < 26.1 'lambda$addMainPass$0' -> 'method_62214'
        method = "lambda$addMainPass$0",
        //? if >= 1.21.11 {
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;renderGroup(Lnet/minecraft/client/renderer/chunk/ChunkSectionLayerGroup;Lcom/mojang/blaze3d/textures/GpuSampler;)V"),
        //?} else
        //at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;renderGroup(Lnet/minecraft/client/renderer/chunk/ChunkSectionLayerGroup;)V"),
        slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/chunk/ChunkSectionLayerGroup;TRANSLUCENT:Lnet/minecraft/client/renderer/chunk/ChunkSectionLayerGroup;"))
    )
    private void polynametag$captureOpaqueDepth(CallbackInfo ci) {
        NametagDeferral.captureOpaqueDepth();
    }
    //?} else {
    /^@Inject(method = "renderSectionLayer", at = @At("HEAD"))
    private void polynametag$captureOpaqueDepth(CallbackInfo ci, @Local(argsOnly = true) RenderType renderType) {
        if (renderType == RenderType.translucent()) {
            NametagDeferral.captureOpaqueDepth();
        }
    }
    ^///?}

    //? if >= 1.21.2 {
    @Inject(method = "addLateDebugPass", at = @At("HEAD"))
    //~ if < 1.21.8 'GpuBufferSlice' -> 'FogParameters'
    private void polynametag$addNametagPass(CallbackInfo ci, @Local(argsOnly = true) FrameGraphBuilder frame, @Local(argsOnly = true) GpuBufferSlice fog) {
        if (!NametagDeferral.isActive()) {
            return;
        }

        FramePass pass = frame.addPass("polynametag_nametags");
        targets.main = pass.readsAndWrites(targets.main);
        pass.executes(() -> {
            RenderSystem.setShaderFog(fog);
            NametagDeferral.flush();
        });
    }
    //?} else {
    /^@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderDebug(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/Camera;)V"))
    private void polynametag$drawNametags(CallbackInfo ci) {
        NametagDeferral.flush();
    }
    ^///?}
}
*///?} elif = 1.8.9 {
/*import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.platform.GlStateManager;
import org.polyfrost.polynametag.client.NametagDeferral;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class Mixin_DeferNametags {
    @Shadow
    public abstract void enableLightMap();

    @Shadow
    public abstract void disableLightMap();

    @Inject(method = "render(IFJ)V", at = @At("HEAD"))
    private void polynametag$beginLevel(CallbackInfo ci) {
        NametagDeferral.beginLevel();
    }

    @Inject(method = "render(IFJ)V", at = @At("RETURN"))
    private void polynametag$endLevel(CallbackInfo ci) {
        NametagDeferral.endLevel();
    }

    @Inject(
        method = "render(IFJ)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;disableFog()V", ordinal = 0),
        slice = @Slice(from = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=translucent"))
    )
    private void polynametag$drawNametags(CallbackInfo ci) {
        if (!NametagDeferral.hasPending()) {
            return;
        }

        enableLightMap();
        NametagDeferral.replay();
        disableLightMap();
        GlStateManager.disableLighting();
    }
}
*///?} else {
public abstract class Mixin_DeferNametags {
}
//?}
