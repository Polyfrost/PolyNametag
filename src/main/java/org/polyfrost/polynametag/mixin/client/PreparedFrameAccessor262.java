package org.polyfrost.polynametag.mixin.client;

//? if = 26.2 {
/*import java.util.Map;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.phase.FeatureRenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FeatureRenderDispatcher.PreparedFrame.class)
public interface PreparedFrameAccessor262 {
    @Accessor("context")
    FeatureFrameContext polynametag$getContext();

    @Accessor("submitNodeStorage")
    SubmitNodeStorage polynametag$getSubmitNodeStorage();

    @Accessor("groupsByPhase")
    Map<FeatureRenderPhase<?>, Object> polynametag$getGroupsByPhase();

    @Invoker("executePhase")
    void polynametag$executePhase(FeatureRenderPhase<?> phase, FeatureFrameContext context);
}
*///?} else {
public interface PreparedFrameAccessor262 {
}
//?}
