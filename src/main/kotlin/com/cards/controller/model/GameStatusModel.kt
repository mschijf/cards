package com.cards.controller.model

import com.cards.game.card.Card
import com.cards.game.hearts.GameMaster
import com.cards.game.Player

class GameStatusModel(gameMaster: GameMaster)  {
    val onTable = TableModel(
        gameMaster.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.SOUTH),
        gameMaster.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.WEST),
        gameMaster.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.NORTH),
        gameMaster.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.EAST)
    )

    val playerSouth = Array(gameMaster.maxCardsInHand) { i -> gameMaster.getCardPlayer(Player.SOUTH).getCardsInHand().elementAtOrNull(i)}
    val playerWest = Array(gameMaster.maxCardsInHand) { i -> gameMaster.getCardPlayer(Player.WEST).getCardsInHand().elementAtOrNull(i)}
    val playerNorth = Array(gameMaster.maxCardsInHand) { i -> gameMaster.getCardPlayer(Player.NORTH).getCardsInHand().elementAtOrNull(i)}
    val playerEast = Array(gameMaster.maxCardsInHand) { i -> gameMaster.getCardPlayer(Player.EAST).getCardsInHand().elementAtOrNull(i)}
}

data class TableModel(
    val south: Card?,
    val west: Card?,
    val north: Card?,
    val east: Card?)

