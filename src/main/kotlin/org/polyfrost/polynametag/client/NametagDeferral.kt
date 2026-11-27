package org.polyfrost.polynametag.client

//? if > 1.8.9 && < 26.2 {
/*import com.mojang.blaze3d.pipeline.RenderTarget
import com.mojang.blaze3d.pipeline.TextureTarget
import com.mojang.blaze3d.vertex.ByteBufferBuilder
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource

object NametagDeferral {
    private const val BUFFER_SIZE = 1 shl 16

    private var bufferSource: MultiBufferSource.BufferSource? = null
    private var opaqueDepth: TextureTarget? = null
    private var savedDepth: TextureTarget? = null
    private var inLevel = false
    private var pending = false
    private var depthCaptured = false

    @JvmStatic
    fun isActive(): Boolean = inLevel

    @JvmStatic
    fun beginLevel() {
        inLevel = PolyNametagConfig.isEnabled
        pending = false
        depthCaptured = false
    }

    @JvmStatic
    fun endLevel() {
        flush()
        inLevel = false
    }

    @JvmStatic
    fun redirect(original: MultiBufferSource): MultiBufferSource {
        if (!inLevel) {
            return original
        }

        pending = true
        return bufferSource ?: MultiBufferSource.immediate(ByteBufferBuilder(BUFFER_SIZE)).also { bufferSource = it }
    }

    @JvmStatic
    fun captureOpaqueDepth() {
        if (!pending || depthCaptured) {
            return
        }

        val main = Minecraft.getInstance().mainRenderTarget
        opaqueDepth = copyDepth(opaqueDepth, main, "PolyNametag Opaque Depth")
        bindMain(main)
        depthCaptured = true
    }

    @JvmStatic
    fun flush() {
        if (!pending) {
            return
        }

        pending = false
        val source = bufferSource ?: return
        val main = Minecraft.getInstance().mainRenderTarget
        val opaque = opaqueDepth
        if (!depthCaptured || opaque == null) {
            source.endBatch()
            return
        }

        depthCaptured = false
        val saved = copyDepth(savedDepth, main, "PolyNametag Saved Depth")
        savedDepth = saved
        main.copyDepthFrom(opaque)
        bindMain(main)
        source.endBatch()
        main.copyDepthFrom(saved)
        bindMain(main)
    }

    private fun copyDepth(existing: TextureTarget?, main: RenderTarget, label: String): TextureTarget {
        val target = existing ?: createTarget(label, main.width, main.height)
        if (target.width != main.width || target.height != main.height) {
            //? if >= 1.21.2 {
            target.resize(main.width, main.height)
            //?} else
            /*target.resize(main.width, main.height, Minecraft.ON_OSX)*/
        }
        target.copyDepthFrom(main)
        return target
    }

    private fun createTarget(label: String, width: Int, height: Int): TextureTarget {
        //? if >= 1.21.5 {
        return TextureTarget(label, width, height, true)
        //?} elif >= 1.21.2 {
        /*return TextureTarget(width, height, true)
        *///?} else
        /*return TextureTarget(width, height, true, Minecraft.ON_OSX)*/
    }

    private fun bindMain(main: RenderTarget) {
        //? if < 1.21.5
        //main.bindWrite(false)
    }
}
*///?} elif = 1.8.9 {
/*object NametagDeferral {
    private val queue = ArrayList<Runnable>()
    private var inLevel = false
    private var replaying = false

    @JvmStatic
    fun beginLevel() {
        inLevel = PolyNametagConfig.isEnabled
        queue.clear()
    }

    @JvmStatic
    fun endLevel() {
        inLevel = false
        queue.clear()
    }

    @JvmStatic
    fun shouldDefer(): Boolean = inLevel && !replaying

    @JvmStatic
    fun defer(task: Runnable) {
        queue.add(task)
    }

    @JvmStatic
    fun hasPending(): Boolean = queue.isNotEmpty()

    @JvmStatic
    fun replay() {
        replaying = true
        try {
            for (task in queue) {
                task.run()
            }
        } finally {
            queue.clear()
            replaying = false
        }
    }
}
*///?}
