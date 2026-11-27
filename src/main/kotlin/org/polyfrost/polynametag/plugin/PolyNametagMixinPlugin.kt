package org.polyfrost.polynametag.plugin

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class PolyNametagMixinPlugin : IMixinConfigPlugin {
    override fun getMixins(): List<String> = buildList {
        //? if >= 26.3 {
        add("client.Mixin_RenderBackgroundShape")
        add("client.Mixin_WrapNametagRender")
        //?}

        //? if = 26.2 {
        /*add("client.Mixin_NametagSubmitYield")
        add("client.Mixin_RenderBackgroundShape")
        add("client.Mixin_WrapNametagRender")
        *///?}

        //? if < 26.2 {
        /*add("client.Mixin_WrapNametagRender")
        *///?}

        //? if >= 1.21.10 && < 26.2 {
        /*add("client.Mixin_NametagSubmitYield")
        *///?}

        //? if = 1.8.9 {
        /*add("client.Mixin_RenderBackgroundShape")
        add("client.Mixin_FixSneakingNametag")
        add("client.Mixin_RespectCustomNameVisible")
        add("client.TextRendererInvoker")
        *///?}
    }

    override fun shouldApplyMixin(targetClassName: String?, mixinClassName: String?): Boolean = true

    override fun getRefMapperConfig(): String? = null

    override fun onLoad(mixinPackage: String?) {
    }

    override fun acceptTargets(myTargets: Set<String?>?, otherTargets: Set<String?>?) {
    }

    override fun preApply(targetClassName: String?, targetClass: ClassNode?, mixinClassName: String?, mixinInfo: IMixinInfo?) {
    }

    override fun postApply(targetClassName: String?, targetClass: ClassNode?, mixinClassName: String?, mixinInfo: IMixinInfo?) {
    }
}
