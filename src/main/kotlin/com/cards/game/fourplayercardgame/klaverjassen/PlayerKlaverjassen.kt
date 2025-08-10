package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Table

open class PlayerKlaverjassen(
    tablePosition: Table,
    game: GameKlaverjassen) : Player(tablePosition, game) {

    fun getCurrentRound() = game.getCurrentRound() as RoundKlaverjassen

    override fun chooseCard(): Card {
        val legalCards = getCurrentRound().getTrickOnTable().getLegalPlayableCards(getCardsInHand())
        return legalCards.first()
    }

    open fun chooseTrumpColor(cardColorOptions: List<CardColor> = CardColor.values().toList()): CardColor {
        return cardColorOptions[tablePosition.ordinal % cardColorOptions.size]
    }

    fun getPartner() = nextPlayer().nextPlayer() as PlayerKlaverjassen
    fun isPartner(otherPlayer: Player?) = getPartner() == otherPlayer
}