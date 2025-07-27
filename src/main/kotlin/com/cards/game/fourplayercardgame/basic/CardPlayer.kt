package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.TablePosition

abstract class CardPlayer(
    val tablePosition: TablePosition,
    val game: Game) {

    private var cardsInHand: MutableList<Card> = mutableListOf()

    abstract fun chooseCard(): Card

    fun getCardsInHand() = cardsInHand.toList()
    fun hasColorInHand(color: CardColor) = cardsInHand.any { card -> card.color == color }

    fun setCardsInHand(cardsFromDealer: List<Card>) {
        cardsInHand = cardsFromDealer.toMutableList()
        cardsInHand.sortBy { card -> 100 * card.color.ordinal + card.rank.ordinal }
    }

    fun removeCard(card: Card) {
        if (!cardsInHand.remove(card)) {
            throw Exception("cannot remove card $card from hand of player $tablePosition ")
        }
    }

}