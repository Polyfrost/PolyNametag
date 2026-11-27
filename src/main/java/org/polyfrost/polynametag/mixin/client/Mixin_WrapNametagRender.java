package org.polyfrost.polynametag.mixin.client;

//? if >= 26.3 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import org.polyfrost.polynametag.api.NametagStyle;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.renderer.feature.TextFeatureRenderer;

@Mixin(targets = "net.minecraft.client.renderer.SubmitNodeCollection")
public abstract class Mixin_WrapNametagRender {
    @WrapOperation(
        method = "nameTag",
        at = @At(
            value = "NEW",
            target = "(FFLnet/minecraft/util/FormattedCharSequence;ZIII)Lnet/minecraft/client/renderer/feature/TextFeatureRenderer$Content$Text;"
        )
    )
    private static TextFeatureRenderer.Content.Text wrapNametagRender(float x, float y, FormattedCharSequence string, boolean dropShadow, int color, int backgroundColor, int outlineColor, Operation<TextFeatureRenderer.Content.Text> original, @Local(argsOnly = true) Font.DisplayMode displayMode) {
        NametagStyle style = NametagRenderer.resolve(
            color,
            dropShadow,
            backgroundColor,
            displayMode == Font.DisplayMode.SEE_THROUGH,
            x,
            y,
            -2.0F * x
        );
        return original.call(
            x,
            style.getTextY(),
            NametagRenderer.getBackgroundYielded() ? string : NametagRenderer.markNametagText(string, style),
            style.getTextShadow(),
            style.getTextColor(),
            NametagRenderer.vanillaBackgroundColor(style),
            outlineColor
        );
    }
}
//?} elif = 26.2 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import net.minecraft.util.FormattedCharSequence;
import org.polyfrost.polynametag.api.NametagStyle;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.renderer.feature.NameTagFeatureRenderer")
public abstract class Mixin_WrapNametagRender {
    @WrapOperation(
        method = "prepareText",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Font;prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;"
        )
    )
    private static Font.PreparedText wrapNametagRender(Font font, FormattedCharSequence text, float x, float y, int originalColor, boolean drawShadow, boolean includeEmpty, int backgroundColor, Operation<Font.PreparedText> original, @Local(argsOnly = true) NameTagFeatureRenderer.Submit submit) {
        NametagStyle style = NametagRenderer.resolve(
            originalColor,
            drawShadow,
            backgroundColor,
            submit.displayMode() == Font.DisplayMode.SEE_THROUGH,
            x,
            y,
            -2.0F * x
        );
        return original.call(
            font,
            text,
            x,
            style.getTextY(),
            style.getTextColor(),
            style.getTextShadow(),
            includeEmpty,
            NametagRenderer.vanillaBackgroundColor(style)
        );
    }
}
*///?} elif > 1.8.9 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
//? if >= 1.21.10 {
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.SubmitNodeStorage;
import org.polyfrost.polynametag.client.BackgroundYieldHolder;
//?}
//~ if < 1.21.11 'rendertype.RenderType' -> 'RenderType'
import net.minecraft.client.renderer.rendertype.RenderType;
//? if >= 1.21.11
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.network.chat.Component;
//~ if < 26.1 'Matrix4fc' -> 'Matrix4f'
import org.joml.Matrix4fc;
import org.polyfrost.polynametag.api.NametagStyle;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//~ if < 1.21.10 'feature.NameTagFeatureRenderer' -> 'entity.EntityRenderer'
@Mixin(targets = "net.minecraft.client.renderer.feature.NameTagFeatureRenderer")
public abstract class Mixin_WrapNametagRender {
    @WrapOperation(
        //? if >= 26.1 {
        method = "renderTranslucent",
        //?} elif >= 1.21.10 {
        /^method = "render",
        ^///?} else
        //method = "renderNameTag",
        at = @At(
            value = "INVOKE",
			//~ if < 26.1 'Matrix4fc' -> 'Matrix4f'
			//~ if < 1.21.8 ')V' -> ')I'
            target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4fc;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V"
        )
    )
    //~ if < 1.21.8 'void' -> 'int'
    private void wrapNametagRender(
        Font font,
        Component text,
        float x,
        float y,
        int color,
        boolean shadow,
		//~ if < 26.1 'Matrix4fc' -> 'Matrix4f'
        Matrix4fc matrix,
        MultiBufferSource bufferSource,
        Font.DisplayMode displayMode,
        int backgroundColor,
        int packedLightCoords,
        //~ if < 1.21.8 'Void' -> 'Integer'
        Operation<Void> original
        //? if >= 1.21.10 {
        , @Local SubmitNodeStorage.NameTagSubmit submit
        //?}
    ) {
        boolean seeThrough = displayMode == Font.DisplayMode.SEE_THROUGH;
        NametagStyle style = NametagRenderer.resolve(color, shadow, backgroundColor, seeThrough, x, y, -2.0F * x);
        //? if >= 1.21.10 {
        boolean yielded = ((BackgroundYieldHolder) (Object) submit).polynametag$backgroundYielded();
        //?} else
        //boolean yielded = NametagRenderer.getBackgroundYielded();
        if (style.getOwnBackground() && !yielded) {
            RenderType type;
            //~ if < 1.21.11 'RenderTypes' -> 'RenderType'
            type = seeThrough ? RenderTypes.textBackgroundSeeThrough() : RenderTypes.textBackground();
            VertexConsumer consumer = bufferSource.getBuffer(type);

            int argb = style.getBackgroundColor();
            float[] vertices = NametagRenderer.backgroundQuadBuffer();
            int count = NametagRenderer.backgroundQuads(style);
            for (int i = 0; i < count; i += 2) {
                consumer.addVertex(matrix, vertices[i], vertices[i + 1], NametagRenderer.BACKGROUND_DEPTH)
                    .setColor(argb)
                    .setLight(packedLightCoords);
            }
        }

        //~ if < 1.21.8 'original.call(' -> 'return original.call('
        original.call(
            font,
            text,
            x,
            style.getTextY(),
            style.getTextColor(),
            style.getTextShadow(),
            matrix,
            bufferSource,
            displayMode,
            NametagRenderer.vanillaBackgroundColor(style),
            packedLightCoords
        );
    }
}
*///?} else {
/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.world.entity.Entity;
import org.polyfrost.polynametag.api.NametagStyle;
import org.polyfrost.polynametag.client.NametagRenderer;
import org.polyfrost.polynametag.client.PolyNametagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.renderer.entity.EntityRenderer")
public abstract class Mixin_WrapNametagRender {
    @WrapOperation(
        method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Ljava/lang/String;DDDI)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/TextRenderer;draw(Ljava/lang/String;III)I",
            ordinal = 0
        )
    )
    private int wrapNametagRender(TextRenderer font, String str, int x, int y, int color, Operation<Integer> original) {
        if (!PolyNametagConfig.isEnabled()) {
            return original.call(font, str, x, y, color);
        }
        NametagStyle style = NametagRenderer.resolve(color, false, NametagRenderer.LEGACY_BACKGROUND, true, x, y, -2.0F * x);
        return polynametag$drawStyled(font, str, x, y, style, original, false);
    }

    @WrapOperation(
        method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Ljava/lang/String;DDDI)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/TextRenderer;draw(Ljava/lang/String;III)I",
            ordinal = 1
        )
    )
    private int wrapOpaqueNametagRender(TextRenderer font, String str, int x, int y, int color, Operation<Integer> original) {
        if (!PolyNametagConfig.isEnabled()) {
            return original.call(font, str, x, y, color);
        }
        NametagStyle style = NametagRenderer.resolve(color, false, 0, false, x, y, -2.0F * x);
        return polynametag$drawStyled(font, str, x, y, style, original, true);
    }

    @Unique
    private static int polynametag$drawStyled(
        TextRenderer font,
        String str,
        int x,
        int y,
        NametagStyle style,
        Operation<Integer> original,
        boolean writesDepth
    ) {
        float dy = style.getTextY() - y;
        boolean moved = dy != 0.0F;
        if (moved) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0F, dy, 0.0F);
        }
        try {
            int textColor = style.getTextColor();
            if (style.getTextShadow()) {
                if (writesDepth) GlStateManager.depthMask(false);
                GlStateManager.enableAlphaTest();
                TextRendererInvoker invoker = (TextRendererInvoker) font;
                invoker.polynametag$reset();
                invoker.polynametag$drawLayer(str, x + 1.0F, y + 1.0F, textColor, true);
                if (writesDepth) GlStateManager.depthMask(true);
            }
            return original.call(font, str, x, y, textColor);
        } finally {
            if (moved) GlStateManager.popMatrix();
        }
    }

    @ModifyExpressionValue(
        method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Ljava/lang/String;DDDI)V",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;cameraPitch:F")
    )
    private float polynametag$faceCamera(float pitch) {
        return NametagRenderer.cameraPitch(pitch);
    }

    @Unique
    private static final float POLYNAMETAG$SNEAK_OFFSET = 9.374999F;

    @Inject(
        method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Ljava/lang/String;DDDI)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/platform/GlStateManager;disableLighting()V",
            shift = At.Shift.AFTER
        )
    )
    private void polynametag$lowerSneakingNametag(Entity entity, String name, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        if (NametagRenderer.lowersSneakingNametag(entity)) {
            GlStateManager.translatef(0.0F, POLYNAMETAG$SNEAK_OFFSET, 0.0F);
        }
    }

    @WrapWithCondition(
        method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Ljava/lang/String;DDDI)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/platform/GlStateManager;disableDepthTest()V"
        )
    )
    private boolean polynametag$occludeSneakingNametag(@Local(argsOnly = true) Entity entity) {
        return !NametagRenderer.lowersSneakingNametag(entity);
    }

    @WrapWithCondition(
        method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Ljava/lang/String;DDDI)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/TextRenderer;draw(Ljava/lang/String;III)I",
            ordinal = 1
        )
    )
    private boolean polynametag$skipOpaqueSneakingPass(TextRenderer font, String str, int textX, int textY, int color, @Local(argsOnly = true) Entity entity) {
        return !NametagRenderer.lowersSneakingNametag(entity);
    }
}
*///?}
