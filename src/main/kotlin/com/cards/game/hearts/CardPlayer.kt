package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.Player

open class CardPlayer(
    val player: Player,
    protected val game: Game) {
    private var cardsInHand: MutableList<Card> = mutableListOf()

    fun getCardsInHand() = cardsInHand.toList()

    fun setCardsInHand(cardsfromDealer: List<Card>) {
        cardsInHand = cardsfromDealer.toMutableList()
        cardsInHand.sortBy { card -> 100 * card.color.ordinal + card.rank.ordinal }
    }

    fun removeCard(card: Card) {
        if (!cardsInHand.remove(card)) {
            throw Exception("cannot remove card $card from hand of player $player ")
        }
    }

    open fun chooseCard(): Card {
        val leadColor = game.getCurrentRound().getTrickOnTable().leadColor()
        val legalCards = HeartsRulesBook.legalPlayableCards(getCardsInHand(), leadColor)
        return legalCards.random()
    }
}
