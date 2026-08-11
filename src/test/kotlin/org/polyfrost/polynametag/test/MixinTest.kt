package org.polyfrost.polynametag.test

import net.minecraft.SharedConstants
import net.minecraft.server.Bootstrap
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.mixin.MixinEnvironment.Option
import org.spongepowered.asm.mixin.transformer.IMixinTransformer

// audits mixins without launching a full Minecraft client
// approach taken from https://github.com/SkyblockerMod/Skyblocker
class MixinTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun setupEnvironment() {
            SharedConstants.tryDetectVersion()
            Bootstrap.bootStrap()
        }
    }

    @Test
    fun `mixins load successfully`() {
        val environment = MixinEnvironment.getCurrentEnvironment()
        Assertions.assertInstanceOf(
            IMixinTransformer::class.java,
            environment.activeTransformer,
        )
        // refmap remapping retries failed selectors without the descriptor which production never does
        environment.setOption(Option.REFMAP_REMAP, false)
        environment.audit()
    }
}
