package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.Player

open class CardPlayer(
    val player: Player,
    @Transient protected var game: Game) {
    private var cardsInHand: MutableList<Card> = mutableListOf()

    fun getCardsInHand() = cardsInHand.toList()

    fun insertGame(newGame: Game) {
        game = newGame
    }

    fun hasColorInHand(color: CardColor) = cardsInHand.any { card -> card.color == color }

    fun setCardsInHand(cardsFromDealer: List<Card>) {
        cardsInHand = cardsFromDealer.toMutableList()
        cardsInHand.sortBy { card -> 100 * card.color.ordinal + card.rank.ordinal }
    }

    fun removeCard(card: Card) {
        if (!cardsInHand.remove(card)) {
            throw Exception("cannot remove card $card from hand of player $player ")
        }
    }

    open fun chooseCard(): Card {
        val leadColor = game.getCurrentRound().getTrickOnTable().leadColor()
        val legalCards = HeartsRules.legalPlayableCards(getCardsInHand(), leadColor)
        return legalCards.random()
    }
}
