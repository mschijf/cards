package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card
import com.cards.game.card.CardColor

abstract class Trick(
    private val leadPosition: TablePosition) {

    private val cardsPlayed = mutableListOf<Card>()

    abstract fun getWinner(): TablePosition?
    abstract fun getWinningCard(): Card?

    fun getLeadPosition() = leadPosition
    fun isLeadPosition(position: TablePosition) = getLeadPosition() == position
    fun getLeadColor() = cardsPlayed.firstOrNull()?.color
    fun isLeadColor(color: CardColor) = color == getLeadColor()
    fun getCardsPlayed() = cardsPlayed.toList()
    fun getPlayersPlayed() = cardsPlayed.mapIndexed { index, _ -> leadPosition.clockwiseNext(index) }
    fun hasNotStarted() = cardsPlayed.isEmpty()
    fun isActive() = !isComplete()
    fun isComplete() = cardsPlayed.size == TablePosition.values().size
    fun isLastPlayerToMove() = (getCardsPlayed().size == TablePosition.values().size)
    fun getPositionToMove() = leadPosition.clockwiseNext(cardsPlayed.size)

    fun getCardPlayedBy(tablePosition: TablePosition): Card? {
        val distance = tablePosition.clockwiseDistanceFrom(leadPosition)
        return cardsPlayed.elementAtOrNull(distance)
    }

    fun getPositionByCardPlayed(card: Card?): TablePosition? {
        val index = getCardsPlayed().indexOf(card)
        return if (index >= 0) leadPosition.clockwiseNext(index) else null
    }

    fun addCard(tablePosition: TablePosition, aCard: Card) {
        if (isComplete())
            throw Exception("Adding a card to a completed trick")
        cardsPlayed.add(aCard)
    }

    fun removeLastCard() {
        if (hasNotStarted())
            throw Exception("Removing a card from a not started trick")
        cardsPlayed.removeLast()
    }
}