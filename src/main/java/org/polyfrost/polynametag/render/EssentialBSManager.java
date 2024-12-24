package org.polyfrost.polynametag.render;

import gg.essential.universal.UMatrixStack;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import org.polyfrost.polynametag.PolyNametag;
import org.polyfrost.polynametag.render.icon.EssentialIconRender;

import java.util.UUID;

public class EssentialBSManager {

    public enum IconRenderType {
        NONE,
        PRE_1354,
        V1354
    }

    private final EssentialIconRender iconRender;

    public EssentialBSManager() {
        IconRenderType iconRenderType1 = IconRenderType.NONE;

        if (PolyNametag.INSTANCE.isEssential()) {
            try {
                // Reflective access to OnboardingData.hasAcceptedTos()
                Class<?> onboardingDataClass = Class.forName("gg.essential.data.OnboardingData");
                onboardingDataClass.getDeclaredMethod("hasAcceptedTos");

                // Reflective access to EssentialConfig.getShowEssentialIndicatorOnNametag()
                Class<?> essentialConfigClass = Class.forName("gg.essential.config.EssentialConfig");
                essentialConfigClass.getDeclaredMethod("getShowEssentialIndicatorOnNametag");

                // Reflective access to EssentialConfig.INSTANCE
                essentialConfigClass.getDeclaredField("INSTANCE");

                // Reflective access to Essential.getConnectionManager()
                Class<?> essentialClass = Class.forName("gg.essential.Essential");
                essentialClass.getDeclaredMethod("getConnectionManager");

                // Reflective access to Essential.getInstance()
                essentialClass.getDeclaredMethod("getInstance");

                // Reflective access to ConnectionManager.getProfileManager()
                Class<?> connectionManagerClass = Class.forName("gg.essential.network.connectionmanager.ConnectionManager");
                connectionManagerClass.getDeclaredMethod("getProfileManager");

                // Reflective access to ProfileManager.getStatus()
                Class<?> profileManagerClass = Class.forName("gg.essential.network.connectionmanager.profile.ProfileManager");
                Class<?> profileStatusClass = Class.forName("gg.essential.connectionmanager.common.enums.ProfileStatus");
                profileManagerClass.getDeclaredMethod("getStatus", UUID.class);

                // Reflective access to ProfileStatus.OFFLINE
                profileStatusClass.getDeclaredField("OFFLINE");

                try {
                    // Reflective access to CosmeticsRenderState$Live constructor
                    Class<?> cosmeticsRenderStateClass = Class.forName("gg.essential.cosmetics.CosmeticsRenderState$Live");
                    cosmeticsRenderStateClass.getDeclaredConstructor(AbstractClientPlayer.class);

                    // Reflective access to OnlineIndicator.drawNametagIndicator()
                    Class<?> onlineIndicatorClass = Class.forName("gg.essential.handlers.OnlineIndicator");
                    Class<?> uMatrixStackClass = Class.forName("gg.essential.universal.UMatrixStack");
                    onlineIndicatorClass.getDeclaredMethod("drawNametagIndicator",
                            uMatrixStackClass, Class.forName("gg.essential.cosmetics.CosmeticsRenderState"), String.class, int.class);
                    iconRenderType1 = IconRenderType.V1354;
                } catch (Exception e) {
                    iconRenderType1 = IconRenderType.PRE_1354;
                    try {
                        // Reflective access to OnlineIndicator.drawNametagIndicator()
                        Class<?> onlineIndicatorClass = Class.forName("gg.essential.handlers.OnlineIndicator");
                        Class<?> uMatrixStackClass = Class.forName("gg.essential.universal.UMatrixStack");
                        onlineIndicatorClass.getDeclaredMethod("drawNametagIndicator",
                                uMatrixStackClass, Entity.class, String.class, int.class);
                    } catch (Exception e2) {
                        e.printStackTrace();
                        e2.printStackTrace();
                        iconRenderType1 = IconRenderType.NONE;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        IconRenderType iconRenderType = iconRenderType1;
        EssentialIconRender iconRender1;
        switch (iconRenderType) {
            case V1354:
                try {
                    iconRender1 = Class.forName("org.polyfrost.polynametag.render.icon.V1354IconRender").asSubclass(EssentialIconRender.class).getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    iconRender1 = null;
                }
                break;
            case PRE_1354:
                try {
                    iconRender1 = Class.forName("org.polyfrost.polynametag.render.icon.Pre1354IconRender").asSubclass(EssentialIconRender.class).getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    iconRender1 = null;
                }
                break;
            default:
                iconRender1 = null;
                break;
        }
        iconRender = iconRender1;
    }

    public void drawIndicator(UMatrixStack matrices, Entity entity, String str, int light) {
        if (iconRender != null) {
            iconRender.drawIndicator(matrices, entity, str, light);
        }
    }

    public boolean canDrawIndicator(Entity entity) {
        if (iconRender != null) {
            return iconRender.canDrawIndicator(entity);
        }
        return false;
    }
}
