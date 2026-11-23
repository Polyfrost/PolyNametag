import dev.kikugie.stonecutter.build.config.ReplacementContainer.StringReplacementSpec

plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.3" /* [SC] DO NOT EDIT */

stonecutter tasks {
    order("publishModrinth")
    order("publishCurseforge")
}

stonecutter parameters {
    replacements {
        string(current.parsed eq "1.8.9") {
            replacePath("net.minecraft.client.renderer.entity", "net.minecraft.client.render.entity")
            replacePath("net.minecraft.world.entity.Entity", "net.minecraft.entity.Entity")
            replacePath("net.minecraft.world.entity.LivingEntity", "net.minecraft.entity.living.LivingEntity")
        }
    }
}

private fun StringReplacementSpec.replacePath(from: String, to: String) {
    replace(from, to)
    replace(from.replace('.', '/'), to.replace('.', '/'))
}
