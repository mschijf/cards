package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Table
import com.cards.game.fourplayercardgame.basic.Table.EAST
import com.cards.game.fourplayercardgame.basic.Table.NORTH
import com.cards.game.fourplayercardgame.basic.Table.SOUTH
import com.cards.game.fourplayercardgame.basic.Table.WEST

open class PlayerHearts (
    tablePosition: Table,
    protected val game: GameHearts) : Player(tablePosition) {

    override fun nextPlayer(): Player {
        return when(this.tablePosition) {
            SOUTH -> game.getCardPlayer(WEST)
            WEST -> game.getCardPlayer(NORTH)
            NORTH -> game.getCardPlayer(EAST)
            EAST -> game.getCardPlayer(SOUTH)
        }
    }

    override fun previousPlayer(): Player {
        return when(this.tablePosition) {
            SOUTH -> game.getCardPlayer(EAST)
            WEST -> game.getCardPlayer(SOUTH)
            NORTH -> game.getCardPlayer(WEST)
            EAST -> game.getCardPlayer(NORTH)
        }
    }

    override fun chooseCard(): Card {
        val legalCards = game.getCurrentRound().getTrickOnTable().getLegalPlayableCards(getCardsInHand())
        return legalCards.first()
    }
}