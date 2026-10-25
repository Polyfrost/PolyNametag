package org.polyfrost.polynametag.client

import net.minecraft.world.entity.Entity
//? if = 1.8.9 {
/*import net.minecraft.entity.living.player.PlayerEntity
import net.minecraft.scoreboard.team.Team
*///?}

object TeamColors {
    //? if = 1.8.9 {
    /*@JvmStatic
    fun background(entity: Entity, argb: Int): Int {
        if (!PolyNametagConfig.isEnabled || !PolyNametagConfig.teamBackground || entity !is PlayerEntity) return argb
        val team = entity.scoreboardTeam as? Team ?: return argb
        return apply(argb, fromPrefix(team.prefix))
    }
    *///?}

    fun apply(argb: Int, rgb: Int?): Int =
        if (rgb == null) argb else (argb and -0x1000000) or (rgb and 0xFFFFFF)

    // Legacy servers encode the team color in the prefix (often followed by bold).
    fun fromPrefix(prefix: String): Int? {
        var color: Int? = null
        var i = 0
        while (i + 1 < prefix.length) {
            if (prefix[i++] != '§') continue
            val code = prefix[i++].lowercaseChar()
            val index = "0123456789abcdef".indexOf(code)
            if (index >= 0) {
                val shade = (index shr 3) * 85
                val red = ((index shr 2) and 1) * 170 + shade + if (index == 6) 85 else 0
                val green = ((index shr 1) and 1) * 170 + shade
                val blue = (index and 1) * 170 + shade
                color = (red shl 16) or (green shl 8) or blue
            } else if (code == 'r') {
                color = null
            }
        }
        return color
    }
}
