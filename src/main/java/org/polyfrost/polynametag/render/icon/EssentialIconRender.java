package org.polyfrost.polynametag.render.icon;

import gg.essential.universal.UMatrixStack;
import net.minecraft.entity.Entity;

public interface EssentialIconRender {
    void drawIndicator(UMatrixStack matrices, Entity entity, String str, int light);
    boolean canDrawIndicator(Entity entity);
}
