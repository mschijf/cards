package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card

abstract class Player(
    val tablePosition: TablePosition,
    protected val game: Game) {

    private var cardsInHand: MutableList<Card> = mutableListOf()

    abstract fun chooseCard(): Card

    fun getCardsInHand() = cardsInHand.toList()
    fun getNumberOfCardsInHand() = getCardsInHand().size

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