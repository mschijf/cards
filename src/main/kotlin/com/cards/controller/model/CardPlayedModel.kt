package com.cards.controller.model

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.Player

data class CardPlayedModel(
    val player: Player,
    val cardPlayed: Card,
    val nextPlayer: Player,
    val cardsInHand: Int,
    val trickCompleted: TrickCompletedModel?)

data class TrickCompletedModel(val trickWinner: Player, val roundCompleted: Boolean, val gameOver: Boolean)