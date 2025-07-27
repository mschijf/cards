package com.cards.game.hearts

import com.cards.game.fourplayercardgame.basic.TablePosition
import org.junit.jupiter.api.Test

internal class ScoreTest {

    @Test
    fun plusTest() {
        val x = TablePosition.values().associateWith { p -> 0 }

        println(x)
    }
}