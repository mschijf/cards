package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card
import com.cards.game.card.CardColor

abstract class Trick(
    private val leadPosition: TablePosition) {

    private val cardsPlayed = mutableListOf<CardPlayedAtPosition>()

    abstract fun getWinner(): TablePosition?
    abstract fun getWinningCard(): Card?

    fun getLeadPosition() = leadPosition
    fun isLeadPosition(position: TablePosition) = getLeadPosition() == position
    fun getLeadColor() = cardsPlayed.firstOrNull()?.card?.color
    fun isLeadColor(color: CardColor) = color == getLeadColor()
    fun getCardsPlayed() = cardsPlayed
    fun hasNotStarted() = cardsPlayed.isEmpty()
    fun isActive() = !isComplete()
    fun isComplete() = cardsPlayed.size == TablePosition.values().size
    fun isLastPlayerToMove() = (getCardsPlayed().size == TablePosition.values().size)
    fun getPositionToMove() = getCardsPlayed().lastOrNull()?.position?.clockwiseNext()?:leadPosition

    fun getCardPlayedBy(tablePosition: TablePosition): Card? {
        return cardsPlayed
            .firstOrNull { p -> p.position == tablePosition }
            ?.card
    }

    fun addCard(tablePosition: TablePosition, aCard: Card) {
        if (isComplete())
            throw Exception("Adding a card to a completed trick")
        cardsPlayed.add(CardPlayedAtPosition(tablePosition, aCard))
    }

    fun removeLastCard() {
        if (hasNotStarted())
            throw Exception("Removing a card from a not started trick")
        cardsPlayed.removeLast()
    }
}