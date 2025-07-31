package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Table
import com.cards.game.fourplayercardgame.basic.Table.EAST
import com.cards.game.fourplayercardgame.basic.Table.NORTH
import com.cards.game.fourplayercardgame.basic.Table.SOUTH
import com.cards.game.fourplayercardgame.basic.Table.WEST

class PlayerKlaverjassen(
    tablePosition: Table,
    protected val game: GameKlaverjassen) : Player(tablePosition) {

    override fun nextPlayer(): Player {
        return when(this.tablePosition) {
            SOUTH -> game.getCardPlayer(WEST)
            WEST -> game.getCardPlayer(NORTH)
            NORTH -> game.getCardPlayer(EAST)
            EAST -> game.getCardPlayer(SOUTH)
        }
    }

    override fun chooseCard(): Card {
        val legalCards = game.getCurrentRound().getTrickOnTable().getLegalPlayableCards(getCardsInHand())
        return legalCards.first()
    }

    fun chooseTrumpColor(cardColorOptions: List<CardColor> = CardColor.values().toList()): CardColor {
        return cardColorOptions[tablePosition.ordinal % cardColorOptions.size]
    }
}