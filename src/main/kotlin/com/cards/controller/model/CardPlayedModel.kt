package com.cards.controller.model

import com.cards.game.Player
import com.cards.game.card.Card

class CardPlayedModel(
    val player: Player,
    val cardPlayed: Card,
    val nextPlayer: Player,
    val trickCompleted: Boolean,
    val trickWinner: Player?,
    val roundCompleted: Boolean)