package com.cards.controller.model

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.Player

data class GameStatusModel(
    val onTable: TableModel,
    val playerToMove: Player,
    val leadPlayer: Player,
    val playerSouth: List<Card?>,
    val playerWest: List<Card?>,
    val playerNorth: List<Card?>,
    val playerEast: List<Card?>,
    val seed: Int,
    val gameJsonString: String,

    val goingUp: Boolean,
    val geniusValueSouth: List<String?>,
)