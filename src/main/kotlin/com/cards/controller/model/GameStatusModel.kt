package com.cards.controller.model

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.Table

data class GameStatusModel(
    val onTable: TableModel,
    val playerToMove: Table,
    val leadPlayer: Table,
    val playerSouth: List<CardInHandModel>,
    val playerWest: List<CardInHandModel>,
    val playerNorth: List<CardInHandModel>,
    val playerEast: List<CardInHandModel>,
    val gameJsonString: String,
)

data class CardInHandModel(
    val card: Card,
    val playable: Boolean,
    val geniusValue: String)
