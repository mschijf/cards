package com.cards.controller.model

import com.cards.game.fourplayercardgame.Player
import com.cards.game.card.Card
import com.cards.game.hearts.GameStatusAfterLastMove

class CardPlayedModel(
    val player: Player,
    val cardPlayed: Card,
    val nextPlayer: Player,
    gameStatusAfterLastMove: GameStatusAfterLastMove) {
    val trickCompleted =
        if (gameStatusAfterLastMove.trickCompleted)
            TrickCompletedModel(gameStatusAfterLastMove.trickWinner!!, gameStatusAfterLastMove.roundCompleted, gameStatusAfterLastMove.gameFinished)
        else
            null
}

data class TrickCompletedModel(val trickWinner: Player, val roundCompleted: Boolean, val gameOver: Boolean)