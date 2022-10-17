package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import java.awt.HeadlessException

class MetaCardList(
    private val cardsInHand : List<Card>,
    private val cardsPlayed: List<Card>,
    private val cardsStillInPlay: List<Card>) {
    val metaCardList = cardsInHand.map { card -> MetaCardInfo(card, 0) }

    fun getCardValue(card: Card): Int? = metaCardList.firstOrNull { metacard -> metacard.card == card }?.value
    
    constructor(cardsInHand: List<Card>):this(cardsInHand, emptyList<Card>(), emptyList<Card>())

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
            .forEach { metaCardInfo -> metaCardInfo.value += rankStepValue * HeartsRules.toRankNumber(metaCardInfo.card) }
        return this
    }
    

    fun evaluateByRankLowerThanOtherCard(otherCard: Card, baseValue: Int, rankStepValue: Int): MetaCardList {
        metaCardList
            .filter { metaCardInfo -> metaCardInfo.card.color == otherCard.color }
            .filter { metaCardInfo ->
                HeartsRules.toRankNumber(metaCardInfo.card) < HeartsRules.toRankNumber(
                    otherCard
                )
            }
            .sortedBy { mc -> HeartsRules.toRankNumber(mc.card) }
            .forEachIndexed { index, metaCardInfo -> metaCardInfo.value += baseValue + (index+1) * rankStepValue }
        return this
    }

    fun evaluateByRankHigherThanOtherCard(otherCard: Card, baseValue: Int, rankStepValue: Int): MetaCardList {
        metaCardList
            .filter { metaCardInfo -> metaCardInfo.card.color == otherCard.color }
            .filter { metaCardInfo ->
                HeartsRules.toRankNumber(metaCardInfo.card) > HeartsRules.toRankNumber(
                    otherCard
                )
            }
            .sortedBy { mc -> HeartsRules.toRankNumber(mc.card) }
            .forEachIndexed { index, metaCardInfo -> metaCardInfo.value += baseValue + (index+1) * rankStepValue }
        return this
    }

    fun evaluateSpecificCardLowerThanOtherCard(card: Card, value: Int, otherCard: Card): MetaCardList {
        metaCardList
            .filter { metaCardInfo -> metaCardInfo.card == card }
            .filter { metaCardInfo ->
                HeartsRules.toRankNumber(metaCardInfo.card) < HeartsRules.toRankNumber(otherCard)
            }
            .forEach { metaCardInfo -> metaCardInfo.value += value }
        return this
    }

    fun evaluateSingleCardOfColor(value: Int, higherThanAvailableCard: Card): MetaCardList {
        val color = higherThanAvailableCard.color
        if (cardsInHand.count { c -> c.color == color } == 1) {
            val single = cardsInHand.first { c -> c.color == color }
            if (cardsStillInPlay.contains(higherThanAvailableCard) ){
                if (HeartsRules.toRankNumber(single) > HeartsRules.toRankNumber(higherThanAvailableCard)) {
                    metaCardList
                        .filter { metaCardInfo -> metaCardInfo.card.color == color }
                        .forEach { metaCardInfo -> metaCardInfo.value += value }
                }
            }
        }
        return this
    }

    fun evaluateSingleCardOfColor(value: Int, color: CardColor): MetaCardList {
        if (cardsInHand.count { c -> c.color == color } == 1) {
            val single = cardsInHand.first { c -> c.color == color }

            val countHigher = cardsStillInPlay
                .filter { c -> c.color == color }
                .count { c -> HeartsRules.toRankNumber(c) > HeartsRules.toRankNumber(single) }
            val countLower = cardsStillInPlay
                .filter { c -> c.color == color }
                .count { c -> HeartsRules.toRankNumber(c) < HeartsRules.toRankNumber(single) }
            if (countHigher <= 3 && countLower >= 1) {
                metaCardList
                    .filter { metaCardInfo -> metaCardInfo.card.color == color }
                    .forEach { metaCardInfo -> metaCardInfo.value += value }
            }
        }
        return this
    }

    fun evaluateFreeCards(value: Int): MetaCardList {
        CardColor.values().forEach { color ->
            if (cardsStillInPlay.none { c -> c.color == color }) {
                metaCardList
                    .filter { mc -> mc.card.color == color }
                    .forEach { mc -> mc.value += value }
            }
        }
        return this
    }

    //------------------------------------------------------------------------------------------------------------------

    private fun isHighestCardOfColor(card: Card): Boolean {
        val nCardsHigherPlayed = (cardsInHand union cardsPlayed)
            .filter { cardPlayed -> cardPlayed.color == card.color }
            .count { cardPlayed -> HeartsRules.toRankNumber(cardPlayed) > HeartsRules.toRankNumber(card) }
        val nCardsHigher = HeartsRules.higherCardsThen(card).count()

        return nCardsHigher == nCardsHigherPlayed
    }

}




