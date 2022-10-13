package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor

class MetaCardList(
    private val cardsInHand : List<Card>,
    private val cardsPlayed: List<Card>) {
    val metaCardList = cardsInHand.map { card -> MetaCardInfo(card, 0) }

    class MetaCardInfo(val card: Card, var value: Int)

    fun evaluateSpecificCard(card: Card, value: Int): MetaCardList {
        metaCardList
            .filter { metaCardInfo -> metaCardInfo.card == card }
            .forEach { metaCardInfo -> metaCardInfo.value += value }
        return this
    }

    fun evaluateSpecificColor(cardColor: CardColor, value: Int): MetaCardList {
        metaCardList
            .filter { metaCardInfo -> metaCardInfo.card.color == cardColor }
            .forEach { metaCardInfo -> metaCardInfo.value += value }
        return this
    }

    fun evaluateHighestCardsInColor(value: Int): MetaCardList {
        metaCardList
            .filter { metaCardInfo -> isHighestCardOfColor(metaCardInfo.card) }
            .forEach { metaCardInfo -> metaCardInfo.value += value }
        return this
    }

    fun evaluateByRank(rankStepValue: Int): MetaCardList {
        metaCardList
            .forEach { metaCardInfo -> metaCardInfo.value += rankStepValue * HeartsRulesBook.toRankNumber(metaCardInfo.card) }
        return this
    }

    fun evaluateGivenCardLowerThanOtherCard(givenCard: Card, otherCard: Card, value: Int): MetaCardList {
        if (otherCard.color == givenCard.color) {
            metaCardList
                .filter { metaCardInfo -> metaCardInfo.card == givenCard }
                .filter { metaCardInfo -> HeartsRulesBook.toRankNumber(metaCardInfo.card) < HeartsRulesBook.toRankNumber(otherCard) }
                .forEach { metaCardInfo -> metaCardInfo.value += value }
        }
        return this
    }

    fun evaluateByRankLowerThanOtherCard(otherCard: Card, baseValue: Int, rankStepValue: Int): MetaCardList {
        metaCardList
            .filter { metaCardInfo -> metaCardInfo.card.color == otherCard.color }
            .filter { metaCardInfo -> HeartsRulesBook.toRankNumber(metaCardInfo.card) < HeartsRulesBook.toRankNumber(otherCard) }
            .sortedBy { mc -> HeartsRulesBook.toRankNumber(mc.card) }
            .forEachIndexed { index, metaCardInfo -> metaCardInfo.value += baseValue + index * rankStepValue }
        return this
    }

    fun evaluateByRankHigherThanOtherCard(otherCard: Card, baseValue: Int, rankStepValue: Int): MetaCardList {
        metaCardList
            .filter { metaCardInfo -> metaCardInfo.card.color == otherCard.color }
            .filter { metaCardInfo -> HeartsRulesBook.toRankNumber(metaCardInfo.card) > HeartsRulesBook.toRankNumber(otherCard) }
            .sortedBy { mc -> HeartsRulesBook.toRankNumber(mc.card) }
            .forEachIndexed { index, metaCardInfo -> metaCardInfo.value += baseValue + index * rankStepValue }
        return this
    }

    //------------------------------------------------------------------------------------------------------------------

    private fun isHighestCardOfColor(card: Card): Boolean {
        val nCardsHigherPlayed = (cardsInHand union cardsPlayed)
            .filter { cardPlayed -> cardPlayed.color == card.color }
            .count { cardPlayed -> HeartsRulesBook.toRankNumber(cardPlayed) > HeartsRulesBook.toRankNumber(card) }
        val nCardsHigher = HeartsRulesBook.higherCardsThen(card).count()

        return nCardsHigher == nCardsHigherPlayed
    }
}




