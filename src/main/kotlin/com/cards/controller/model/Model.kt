package com.cards.controller.model

import com.cards.game.card.Card
import com.cards.game.hearts.GameMaster
import com.cards.game.hearts.Player

class Model(gameMaster: GameMaster)  {
    val onTable = TableModel(
        gameMaster.trickOnTable.getCardPlayedBy(Player.SOUTH),
        gameMaster.trickOnTable.getCardPlayedBy(Player.WEST),
        gameMaster.trickOnTable.getCardPlayedBy(Player.NORTH),
        gameMaster.trickOnTable.getCardPlayedBy(Player.EAST)
    )

    val playerSouth = Array(gameMaster.maxCardsInHand) { i -> gameMaster.getHeartsPlayer(Player.SOUTH).cardsInHand.elementAtOrNull(i)}
    val playerWest = Array(gameMaster.maxCardsInHand) { i -> gameMaster.getHeartsPlayer(Player.WEST).cardsInHand.elementAtOrNull(i)}
    val playerNorth = Array(gameMaster.maxCardsInHand) { i -> gameMaster.getHeartsPlayer(Player.NORTH).cardsInHand.elementAtOrNull(i)}
    val playerEast = Array(gameMaster.maxCardsInHand) { i -> gameMaster.getHeartsPlayer(Player.EAST).cardsInHand.elementAtOrNull(i)}
}

data class TableModel(
    val south: Card?,
    val west: Card?,
    val north: Card?,
    val east: Card?)

