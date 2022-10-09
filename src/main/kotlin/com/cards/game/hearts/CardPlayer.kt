package com.cards.game.hearts

import com.cards.game.fourplayercardgame.Player
import com.cards.game.card.Card

class CardPlayer(
    val player: Player,
    private val game: Game) {
    private var cardsInHand: MutableList<Card> = mutableListOf()

    fun getCardsInHand() = cardsInHand.toList()

    fun setCardsInHand(cardsfromDealer: List<Card>) {
        cardsInHand = cardsfromDealer.toMutableList()
        cardsInHand.sortBy { card -> 100*card.color.ordinal + card.rank.ordinal }
    }

    fun removeCard(card: Card) {
        if (!cardsInHand.remove(card)) {
            throw Exception("cannot remove card $card from hand of player $player ")
        }
    }

    fun chooseCard() : Card {
        val leadColor = game.getCurrentRound().getTrickOnTable().leadColor()
        val legalCards = HeartsRulesBook.legalPlayableCards(cardsInHand, leadColor)
        return legalCards.random()
    }

    fun onCardPlayed(playerPlayed: Player, cardPlayed: Card) {

    }

    fun onGoingUpDownChanged(goingDown: Boolean) {

    }
}