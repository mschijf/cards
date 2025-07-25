package com.cards.game.fourplayercardgame

import com.cards.game.fourplayercardgame.basic.Player

data class GameStatusAfterLastMove(
    val trickCompleted: Boolean,
    val trickWinner: Player?,
    val roundCompleted: Boolean,
    val gameFinished: Boolean)