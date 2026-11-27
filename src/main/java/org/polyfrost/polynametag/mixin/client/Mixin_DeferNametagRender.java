package org.polyfrost.polynametag.mixin.client;

//? if = 1.8.9 {
/*import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.render.platform.GLX;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.world.entity.Entity;
import org.polyfrost.polynametag.client.NametagDeferral;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderer.class)
public abstract class Mixin_DeferNametagRender {
    @Unique
    private static final int POLYNAMETAG$FULL_BRIGHT = 15728880;

    @WrapMethod(method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Ljava/lang/String;DDDI)V")
    private void polynametag$deferNametag(Entity entity, String name, double x, double y, double z, int maxDistance, Operation<Void> original) {
        if (!NametagDeferral.shouldDefer()) {
            original.call(entity, name, x, y, z, maxDistance);
            return;
        }

        int light = entity.isOnFire() ? POLYNAMETAG$FULL_BRIGHT : entity.getLightLevel(1.0F);
        NametagDeferral.defer(() -> {
            GLX.multiTexCoord2f(GLX.GL_TEXTURE1, light % 65536, light / 65536);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            original.call(entity, name, x, y, z, maxDistance);
        });
    }
}
*///?} else {
public abstract class Mixin_DeferNametagRender {
}
//?}
