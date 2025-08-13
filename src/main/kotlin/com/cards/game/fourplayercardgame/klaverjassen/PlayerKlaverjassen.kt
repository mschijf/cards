package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.TablePosition

open class PlayerKlaverjassen(tablePosition: TablePosition, game: GameKlaverjassen) : Player(tablePosition, game) {

    fun getCurrentRound() = game.getCurrentRound() as RoundKlaverjassen

    override fun chooseCard(): Card {
        return getCardsInHand()
            .legalPlayable(
                getCurrentRound().getTrickOnTable().getCardsPlayed(),
                getCurrentRound().getTrumpColor())
            .first()
    }

    open fun chooseTrumpColor(cardColorOptions: List<CardColor> = CardColor.values().toList()): CardColor {
        return cardColorOptions[tablePosition.ordinal % cardColorOptions.size]
    }
}