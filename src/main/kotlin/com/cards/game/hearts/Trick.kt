package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor

class Trick(
    private val sideToLead: TableSide) {

    private val cardsPlayed = mutableListOf<Card>()

    fun getSideToLead() = sideToLead
    fun isSideToLead(side: TableSide) = getSideToLead() == side
    fun isLastSideToPlay(side: TableSide) = side.clockwiseDistanceFrom(sideToLead) == 3
    fun getSideToPlay() = sideToLead.clockwiseNext(cardsPlayed.size)

    fun getLeadColor() = cardsPlayed.firstOrNull()?.color
    fun isLeadColor(color: CardColor) = color == getLeadColor()

    fun getCardsPlayed() = cardsPlayed.toList()

    fun hasNotStarted() = cardsPlayed.isEmpty()
    fun isActive() = !isComplete()
    fun isComplete() = cardsPlayed.size == TableSide.values().size


    fun getCardPlayedBy(tableSide: TableSide): Card? {
        val distance = sideToLead.clockwiseDistanceFrom(tableSide)
        return cardsPlayed.elementAtOrNull(distance)
    }

    fun getSideThatPlayedCard(card: Card?): TableSide? {
        val index = getCardsPlayed().indexOf(card)
        return if (index >= 0) sideToLead.clockwiseNext(index) else null
    }

    fun addCard(aCard: Card) {
        if (isComplete())
            throw Exception("Adding a card to a completed trick")
        cardsPlayed.add(aCard)
    }

    fun getWinningSide(): TableSide? {
        return getSideThatPlayedCard(getWinningCard())
    }

    fun getWinningCard(): Card? {
        return getCardsPlayed()
            .filter { card -> isLeadColor(card.color) }
            .maxByOrNull { card -> card.toRankNumber() }
    }

    fun getScore(): ScoreHearts {
        return if (!isComplete()) {
            ScoreHearts.ZERO
        } else {
            ScoreHearts.scoreForPlayer(getWinningSide()!!, getCardsPlayed().sumOf { card -> card.cardValue() })
        }
    }

}