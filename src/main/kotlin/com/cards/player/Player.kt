package com.cards.player

import com.cards.game.card.Card
import com.cards.game.hearts.Game
import com.cards.game.hearts.TableSide
import com.cards.game.hearts.legalPlayable

open class Player(
    val tableSide: TableSide,
    protected val game: Game
) {

    private var cardsInHand: MutableList<Card> = mutableListOf()

    fun getCardsInHand() = cardsInHand.toList()
    fun getNumberOfCardsInHand() = getCardsInHand().size

    fun setCardsInHand(cardsFromDealer: List<Card>) {
        cardsInHand = cardsFromDealer.toMutableList()
    }

    fun removeCard(card: Card) {
        if (!cardsInHand.remove(card)) {
            throw Exception("cannot remove card $card from hand of player $tableSide ")
        }
    }

    open fun chooseCard(): Card {
        return getCardsInHand()
            .legalPlayable(
                game.getCurrentRound().getTrickOnTable().getCardsPlayed()
            )
            .first()
    }

    override fun toString() = "pl-$tableSide"
}