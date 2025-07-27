package com.cards.controller.model

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.TablePosition

data class CardPlayedModel(
    val player: TablePosition,
    val cardPlayed: Card,
    val nextPlayer: TablePosition,
    val cardsInHand: Int,
    val trickCompleted: TrickCompletedModel?)

data class TrickCompletedModel(val trickWinner: TablePosition, val roundCompleted: Boolean, val gameOver: Boolean)