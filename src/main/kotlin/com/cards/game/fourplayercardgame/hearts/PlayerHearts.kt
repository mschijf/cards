package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.TablePosition

open class PlayerHearts (
    tablePosition: TablePosition,
    protected val game: GameHearts) : Player(tablePosition) {

    override fun nextPlayer(): Player {
        return game.getCardPlayer(this.tablePosition.neighbour())
    }

    override fun chooseCard(): Card {
        val legalCards = game.getCurrentRound().getTrickOnTable().legalPlayableCards(getCardsInHand())
        return legalCards.first()
    }
}