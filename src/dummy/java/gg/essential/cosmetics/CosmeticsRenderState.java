package gg.essential.cosmetics;

import net.minecraft.client.entity.AbstractClientPlayer;

public interface CosmeticsRenderState {
    final class Live implements CosmeticsRenderState {

        @SuppressWarnings("unused")
        public Live(AbstractClientPlayer player) {
        }
    }
}
