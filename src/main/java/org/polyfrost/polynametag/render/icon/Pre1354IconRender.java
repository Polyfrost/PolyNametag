package org.polyfrost.polynametag.render.icon;

import gg.essential.Essential;
import gg.essential.config.EssentialConfig;
import gg.essential.connectionmanager.common.enums.ProfileStatus;
import gg.essential.data.OnboardingData;
import gg.essential.handlers.OnlineIndicator;
import gg.essential.universal.UMatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class Pre1354IconRender implements EssentialIconRender {
    @Override
    public void drawIndicator(UMatrixStack matrices, Entity entity, String str, int light) {
        OnlineIndicator.drawNametagIndicator(matrices, entity, str, light);
    }

    @Override
    public boolean canDrawIndicator(Entity entity) {
        if (OnboardingData.hasAcceptedTos() && EssentialConfig.INSTANCE.getShowEssentialIndicatorOnNametag() && entity instanceof EntityPlayer) {
            return Essential.getInstance().getConnectionManager().getProfileManager().getStatus(((EntityPlayer) entity).getGameProfile().getId()) != ProfileStatus.OFFLINE;
        }
        return false;
    }
}
