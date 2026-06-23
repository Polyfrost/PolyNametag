package org.polyfrost.polynametag

import net.fabricmc.api.ClientModInitializer
import org.polyfrost.polynametag.client.PolyNametagClient

class PolyNametagEntrypoint : ClientModInitializer {
    override
    fun onInitializeClient() {
        PolyNametagClient.initialize()
    }
}
