package com.cards.controller.basic.model

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.Table

data class CardPlayedModel(
    val player: Table,
    val cardPlayed: Card,
    val nextPlayer: Table,
    val cardsInHand: Int,
    val trickCompleted: TrickCompletedModel?)

data class TrickCompletedModel(val trickWinner: Table, val roundCompleted: Boolean, val gameOver: Boolean)