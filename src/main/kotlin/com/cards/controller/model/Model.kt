package com.cards.controller.model

import com.cards.game.card.Card
import com.cards.game.hearts.GameMaster
import com.cards.game.hearts.Player

class Model(gameMaster: GameMaster)  {
    val onTable = TableModel(
        gameMaster.onTable.getCardPlayedByPlayer(Player.SOUTH),
        gameMaster.onTable.getCardPlayedByPlayer(Player.WEST),
        gameMaster.onTable.getCardPlayedByPlayer(Player.NORTH),
        gameMaster.onTable.getCardPlayedByPlayer(Player.EAST)
    )
    val playerSouth = gameMaster.getPlayer(Player.SOUTH).cardsInHand
    val playerWest = gameMaster.getPlayer(Player.WEST).cardsInHand
    val playerNorth = gameMaster.getPlayer(Player.NORTH).cardsInHand
    val playerEast = gameMaster.getPlayer(Player.EAST).cardsInHand
}

data class TableModel(
    val south: Card?,
    val west: Card?,
    val north: Card?,
    val east: Card?)

