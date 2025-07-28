package com.cards.controller.model

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.TablePosition

data class GameStatusModel(
    val onTable: TableModel,
    val playerToMove: TablePosition,
    val leadPlayer: TablePosition,
    val playerSouth: List<CardInHandModel>,
    val playerWest: List<CardInHandModel>,
    val playerNorth: List<CardInHandModel>,
    val playerEast: List<CardInHandModel>,
    val gameJsonString: String,

    val goingUp: Boolean,
)

data class CardInHandModel(
    val card: Card,
    val playable: Boolean,
    val geniusValue: String)
