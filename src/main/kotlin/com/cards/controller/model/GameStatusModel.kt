package com.cards.controller.model

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.TablePosition

data class GameStatusModel(
    val onTable: TableModel,
    val playerToMove: TablePosition,
    val leadPlayer: TablePosition,
    val playerSouth: List<Card?>,
    val playerWest: List<Card?>,
    val playerNorth: List<Card?>,
    val playerEast: List<Card?>,
    val gameJsonString: String,

    val goingUp: Boolean,
    val geniusValueSouth: List<String?>,
)
