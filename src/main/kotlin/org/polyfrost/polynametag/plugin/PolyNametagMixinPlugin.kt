package org.polyfrost.polynametag.plugin

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class PolyNametagMixinPlugin : IMixinConfigPlugin {
    override fun getMixins(): List<String> = buildList {
        //? if >= 26.3 {
        add("client.Mixin_RenderBackgroundShape263")
        add("client.Mixin_WrapNametagRender263")
        //?}

        //? if = 26.2 {
        /*add("client.Mixin_RenderBackgroundShape262")
        add("client.Mixin_WrapNametagRender262")
        *///?}

        //? if < 26.2 {
        /*add("client.Mixin_RenderBackgroundShape")
        *///?}

        //? if = 1.21.10 {
        /*add("client.Mixin_WrapNametagRender12110")
        *///?}

        //? if < 1.21.10 {
        /*add("client.Mixin_WrapNametagRender")
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
