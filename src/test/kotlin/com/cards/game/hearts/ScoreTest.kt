package com.cards.game.hearts

import com.cards.game.fourplayercardgame.Player
import org.junit.jupiter.api.Test

internal class ScoreTest {

    @Test
    fun plusTest() {
        val x = Player.values().associateWith { p -> 0 }

        println(x)
    }
}