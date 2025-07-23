package com.cards.controller.model

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.GameMaster
import com.cards.game.fourplayercardgame.Player
import com.cards.game.fourplayercardgame.Table

data class GameStatusModel(
    val onTable: TableModel,
    val playerToMove: Player,
    val leadPlayer: Player,
    val playerSouth: List<Card?>,
    val playerWest: List<Card?>,
    val playerNorth: List<Card?>,
    val playerEast: List<Card?>,
    val goingUp: Boolean,
    val valueSouth: List<String>,
    val seed: Int,
    val gameJsonString: String,
)  {
    companion object {
        fun of(gameMaster: GameMaster): GameStatusModel {
            val onTable = TableModel(
                gameMaster.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.SOUTH),
                gameMaster.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.WEST),
                gameMaster.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.NORTH),
                gameMaster.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.EAST)
            )
            val playerToMove = gameMaster.game.getPlayerToMove()
            val leadPlayer = gameMaster.game.getCurrentRound().getTrickOnTable().getLeadPlayer()

            val goingUp = true //gameMaster.game.getGoingUp()

            val playerSouth = List(Table.nCardsInHand) { i -> gameMaster.getCardPlayer(Player.SOUTH).getCardsInHand().elementAtOrNull(i)}
            val playerWest = List(Table.nCardsInHand) { i -> gameMaster.getCardPlayer(Player.WEST).getCardsInHand().elementAtOrNull(i)}
            val playerNorth = List(Table.nCardsInHand) { i -> gameMaster.getCardPlayer(Player.NORTH).getCardsInHand().elementAtOrNull(i)}
            val playerEast = List(Table.nCardsInHand) { i -> gameMaster.getCardPlayer(Player.EAST).getCardsInHand().elementAtOrNull(i)}

            val valueSouth = List(Table.nCardsInHand) { i -> "x" }

//    val valueSouth = Array(Table.nCardsInHand) { i ->
//        getGeniusCardValue(
//            gameMaster.getCardPlayer(Player.SOUTH),
//            gameMaster.getCardPlayer(Player.SOUTH).getCardsInHand().elementAtOrNull(i))}
//
//    private fun getGeniusCardValue(geniusHeartsPlayer: GeniusHeartsPlayer, card: Card?): String? {
//        if (card == null)
//            return null
//        return geniusHeartsPlayer.getMetaCardList().getCardValue(card)?.toString() ?: "x"
//    }

            val seed = gameMaster.game.getSeed()
            val gameJsonString = "" //Gson().toJson(gameMaster)

            return GameStatusModel(
                onTable,
                playerToMove,
                leadPlayer,
                playerSouth,
                playerWest,
                playerNorth,
                playerEast,
                goingUp,
                valueSouth,
                seed,
                gameJsonString
            )
        }
    }
}

data class TableModel(
    val south: Card?,
    val west: Card?,
    val north: Card?,
    val east: Card?)

