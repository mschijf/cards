package com.cards.game.fourplayercardgame

data class GameStatusAfterLastMove(
    val trickCompleted: Boolean, val trickWinner: Player?, val roundCompleted: Boolean, val gameFinished: Boolean)