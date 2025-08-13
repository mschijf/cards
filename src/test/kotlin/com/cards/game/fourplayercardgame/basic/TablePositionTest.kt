package com.cards.game.fourplayercardgame.basic

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TablePositionTest {
    @Test
    fun clockwiseNext() {
        assertEquals(TablePosition.WEST, TablePosition.WEST.clockwiseNext(0))
        assertEquals(TablePosition.NORTH, TablePosition.WEST.clockwiseNext())
        assertEquals(TablePosition.EAST, TablePosition.WEST.clockwiseNext(2))
        assertEquals(TablePosition.SOUTH, TablePosition.WEST.clockwiseNext(3))
        assertEquals(TablePosition.EAST, TablePosition.WEST.clockwiseNext(6))
    }

}