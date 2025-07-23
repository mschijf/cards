package com.cards.controller.model

import com.cards.game.fourplayercardgame.Player
import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.GameStatusAfterLastMove

data class CardPlayedModel(
    val player: Player,
    val cardPlayed: Card,
    val nextPlayer: Player,
    val cardsInHand: Int,
    val trickCompleted: TrickCompletedModel?) {

    companion object {
        fun of(player: Player,
               cardPlayed: Card,
               nextPlayer: Player,
               cardsInHand: Int,
               gameStatusAfterLastMove: GameStatusAfterLastMove): CardPlayedModel {

            val trickCompleted = if (gameStatusAfterLastMove.trickCompleted)
                TrickCompletedModel(
                    gameStatusAfterLastMove.trickWinner!!,
                    gameStatusAfterLastMove.roundCompleted,
                    gameStatusAfterLastMove.gameFinished)
            else
                null

            return CardPlayedModel(player, cardPlayed,nextPlayer, cardsInHand, trickCompleted)
        }
    }
}

data class TrickCompletedModel(val trickWinner: Player, val roundCompleted: Boolean, val gameOver: Boolean)