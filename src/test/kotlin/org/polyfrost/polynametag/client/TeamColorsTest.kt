package org.polyfrost.polynametag.client

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TeamColorsTest {
    @Test
    fun `legacy prefixes respect color changes formatting and reset`() {
        assertEquals(0xFF5555, TeamColors.fromPrefix("§c§lRED "))
        assertEquals(0x5555FF, TeamColors.fromPrefix("§cRank §9"))
        assertEquals(0xFFAA00, TeamColors.fromPrefix("§6"))
        assertEquals(0x55FFFF, TeamColors.fromPrefix("§B"))
        assertNull(TeamColors.fromPrefix("§c§r"))
        assertNull(TeamColors.fromPrefix("§lTeam §"))
        assertNull(TeamColors.fromPrefix(""))
    }

    @Test
    fun `team tint preserves opacity including hidden backgrounds`() {
        assertEquals(0x3FFF5555, TeamColors.apply(0x3F000000, 0xFF5555))
        assertEquals(0x00FF5555, TeamColors.apply(0, 0xFF5555))
        assertEquals(0x80443322.toInt(), TeamColors.apply(0x80443322.toInt(), null))
    }
}
