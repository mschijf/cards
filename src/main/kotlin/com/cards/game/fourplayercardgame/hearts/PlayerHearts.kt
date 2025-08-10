package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Table

open class PlayerHearts (
    tablePosition: Table,
    game: GameHearts) : Player(tablePosition, game) {

    override fun chooseCard(): Card {
        val legalCards = game.getCurrentRound().getTrickOnTable().getLegalPlayableCards(getCardsInHand())
        return legalCards.first()
    }
}