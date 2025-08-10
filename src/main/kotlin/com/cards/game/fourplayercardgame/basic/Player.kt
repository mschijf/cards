package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card
import com.cards.game.card.CardColor

abstract class Player(
    val tablePosition: Table) {

    private var cardsInHand: MutableList<Card> = mutableListOf()

    abstract fun chooseCard(): Card
    abstract fun nextPlayer(): Player
    abstract fun previousPlayer(): Player

    fun getCardsInHand() = cardsInHand.toList()
    fun hasColorInHand(color: CardColor) = cardsInHand.any { card -> card.color == color }

    fun setCardsInHand(cardsFromDealer: List<Card>) {
        cardsInHand = cardsFromDealer.toMutableList()
    }

    fun removeCard(card: Card) {
        if (!cardsInHand.remove(card)) {
            throw Exception("cannot remove card $card from hand of player $tablePosition ")
        }
    }

    override fun toString() = "pl-$tablePosition"
}