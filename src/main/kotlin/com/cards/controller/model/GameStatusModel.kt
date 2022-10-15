package com.cards.controller.model

import com.cards.game.fourplayercardgame.Player
import com.cards.game.card.Card
import com.cards.game.hearts.GameMaster
import com.cards.game.hearts.Genius

class GameStatusModel(gameMaster: GameMaster)  {
    val onTable = TableModel(
        gameMaster.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.SOUTH),
        gameMaster.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.WEST),
        gameMaster.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.NORTH),
        gameMaster.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.EAST)
    )
    val playerToMove = gameMaster.game.getPlayerToMove()

    val goingUp = gameMaster.game.getGoingUp()

    val playerSouth = Array(gameMaster.maxCardsInHand) { i -> gameMaster.getCardPlayer(Player.SOUTH).getCardsInHand().elementAtOrNull(i)}
    val playerWest = Array(gameMaster.maxCardsInHand) { i -> gameMaster.getCardPlayer(Player.WEST).getCardsInHand().elementAtOrNull(i)}
    val playerNorth = Array(gameMaster.maxCardsInHand) { i -> gameMaster.getCardPlayer(Player.NORTH).getCardsInHand().elementAtOrNull(i)}
    val playerEast = Array(gameMaster.maxCardsInHand) { i -> gameMaster.getCardPlayer(Player.EAST).getCardsInHand().elementAtOrNull(i)}

    val valueSouth = Array(gameMaster.maxCardsInHand) { i ->
        getGeniusCardValue(
            gameMaster.getCardPlayer(Player.SOUTH),
            gameMaster.getCardPlayer(Player.SOUTH).getCardsInHand().elementAtOrNull(i))}

    private fun getGeniusCardValue(genius: Genius, card: Card?): String? {
        if (card == null)
            return null
        return genius.getMetaCardList().getCardValue(card)?.toString() ?: "x"
    }


}

data class TableModel(
    val south: Card?,
    val west: Card?,
    val north: Card?,
    val east: Card?)

