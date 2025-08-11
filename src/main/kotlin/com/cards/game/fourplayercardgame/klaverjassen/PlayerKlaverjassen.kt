package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.TablePosition

open class PlayerKlaverjassen(
    tablePosition: TablePosition,
    game: GameKlaverjassen) : Player(tablePosition, game) {

    fun getCurrentRound() = game.getCurrentRound() as RoundKlaverjassen

    override fun chooseCard(): Card {
        return getCardsInHand()
            .legalPlayable(
                getCurrentRound().getTrickOnTable().getCardsPlayed().map { it.card },
                getCurrentRound().getTrumpColor())
            .first()
    }

    open fun chooseTrumpColor(cardColorOptions: List<CardColor> = CardColor.values().toList()): CardColor {
        return cardColorOptions[tablePosition.ordinal % cardColorOptions.size]
    }

    fun isPartner(otherPlayer: Player) = otherPlayer.tablePosition == tablePosition.opposite()
}

//
//fun getOtherPlayers() = game.getPlayerList() - this
//
//fun nextPlayer(): Player {
//    return when(this.tablePosition) {
//        SOUTH -> game.getCardPlayer(WEST)
//        WEST -> game.getCardPlayer(NORTH)
//        NORTH -> game.getCardPlayer(EAST)
//        EAST -> game.getCardPlayer(SOUTH)
//    }
//}
//
//fun previousPlayer(): Player {
//    return when(this.tablePosition) {
//        SOUTH -> game.getCardPlayer(EAST)
//        WEST -> game.getCardPlayer(SOUTH)
//        NORTH -> game.getCardPlayer(WEST)
//        EAST -> game.getCardPlayer(NORTH)
//    }
//}
//
