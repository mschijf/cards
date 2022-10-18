package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor

class Analyzer(
    private val cardsInHand : List<Card>,
    private val cardsPlayed: List<Card>,
    private val cardsStillInPlay: List<Card>) {
    val metaCardList = cardsInHand.map { card -> MetaCardInfo(card, 0) }

    fun getCardValue(card: Card): Int? = metaCardList.firstOrNull { metacard -> metacard.card == card }?.value
    
    constructor(cardsInHand: List<Card>):this(cardsInHand, emptyList<Card>(), emptyList<Card>())

    class MetaCardInfo(val card: Card, var value: Int)

    fun evaluateSpecificCard(card: Card, value: Int): Analyzer {
        metaCardList
            .filter { metaCardInfo -> metaCardInfo.card == card }
            .forEach { metaCardInfo -> metaCardInfo.value += value }
        return this
    }

    fun evaluateSpecificColor(cardColor: CardColor, value: Int): Analyzer {
        metaCardList
            .filter { metaCardInfo -> metaCardInfo.card.color == cardColor }
            .forEach { metaCardInfo -> metaCardInfo.value += value }
        return this
    }

    fun evaluateHighestCardsInColor(value: Int): Analyzer {
        metaCardList
            .filter { metaCardInfo -> isHighestCardOfColor(metaCardInfo.card) }
            .forEach { metaCardInfo -> metaCardInfo.value += value }
        return this
    }

    fun evaluateByRank(rankStepValue: Int): Analyzer {
        metaCardList
            .forEach { metaCardInfo -> metaCardInfo.value += rankStepValue * HeartsRules.toRankNumber(metaCardInfo.card) }
        return this
    }
    

    fun evaluateByRankLowerThanOtherCard(otherCard: Card, baseValue: Int, rankStepValue: Int): Analyzer {
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

    fun evaluateByRankHigherThanOtherCard(otherCard: Card, baseValue: Int, rankStepValue: Int): Analyzer {
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

    fun evaluateSpecificCardLowerThanOtherCard(card: Card, value: Int, otherCard: Card): Analyzer {
        metaCardList
            .filter { metaCardInfo -> metaCardInfo.card == card }
            .filter { metaCardInfo ->
                HeartsRules.toRankNumber(metaCardInfo.card) < HeartsRules.toRankNumber(otherCard)
            }
            .forEach { metaCardInfo -> metaCardInfo.value += value }
        return this
    }

    fun evaluateSingleCardOfColor(value: Int, higherThanAvailableCard: Card): Analyzer {
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

    fun evaluateSingleCardOfColor(value: Int, color: CardColor): Analyzer {
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

    fun evaluateFreeCards(value: Int): Analyzer {
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

    fun evaluateLeadPlayerByColor(color: CardColor): Analyzer {
        val cardsOfColorInPlay = cardsStillInPlay.count { it.color == color }
        val cardsOfColorInHand = cardsInHand.count { it.color == color }

        if (cardsOfColorInHand == 0)
            return this

        if (cardsOfColorInPlay == 0) {
            metaCardList
                .filter { mc -> mc.card.color == color }
                .forEach { mc -> mc.value += -100}
            return this
        }

        if (cardsOfColorInHand == 1) {
            //todo: check queen of spades, jack of clubs?
            val onlyCard = cardsInHand.first { it.color == color }
            val cardsInPlayHigher = cardsStillInPlay.filter { it.color == color }.count{ HeartsRules.higher(it, onlyCard)}
            val cardsInPlayLower = cardsStillInPlay.filter { it.color == color }.count{ HeartsRules.lower(it, onlyCard)}

            if (cardsInPlayLower == 0) {
                metaCardList
                    .filter { mc -> mc.card == onlyCard }
                    .forEach { mc -> mc.value += 100 }
            }else if (cardsInPlayHigher == 0) {
                metaCardList
                    .filter { mc -> mc.card == onlyCard }
                    .forEach { mc -> mc.value += -100}
            } else if (cardsInPlayLower >= 3) {
                metaCardList
                    .filter { mc -> mc.card == onlyCard }
                    .forEach { mc -> mc.value += -20*(cardsInPlayLower - cardsInPlayHigher) }
            } else {
                metaCardList
                    .filter { mc -> mc.card == onlyCard }
                    .forEach { mc -> mc.value += 20*(cardsInPlayHigher-cardsInPlayLower) }
            }
            return this
        }

        if (cardsOfColorInHand == 2) {
            //todo: better evaluating
            //todo: check queen of spades, jack of clubs
            val lowestCardInHand = cardsInHand.filter { it.color == color }.minByOrNull{HeartsRules.toRankNumber(it)}!!
            val cardsInPlayHigherLowest = cardsInHand.filter { it.color == color }.count{ HeartsRules.higher(it, lowestCardInHand)}
            val cardsInPlayLowerLowest = cardsInHand.filter { it.color == color }.count{ HeartsRules.lower(it, lowestCardInHand)}

            metaCardList
                .filter { it.card.color == color }
                .sortedByDescending { HeartsRules.toRankNumber(it.card) }
                .forEachIndexed { index, it -> it.value += 20 + (index+1) }

            if (cardsInPlayLowerLowest <= 2 && cardsInPlayHigherLowest >= 1) {
                metaCardList
                    .filter { it.card.color == color }
                    .minByOrNull { HeartsRules.toRankNumber(it.card) }!!
                    .value += cardsInPlayHigherLowest * 20
            }
            return this
        }

        if (cardsOfColorInHand == 3) {
            //todo: better evaluating
            //todo: check queen of spades, jack of clubs

            val lowestCardInHand = cardsInHand.filter { it.color == color }.minByOrNull{HeartsRules.toRankNumber(it)}!!
            val cardsInPlayHigherLowest = cardsInHand.filter { it.color == color }.count{ HeartsRules.higher(it, lowestCardInHand)}
            val cardsInPlayLowerLowest = cardsInHand.filter { it.color == color }.count{ HeartsRules.lower(it, lowestCardInHand)}

            metaCardList
                .filter { it.card.color == color }
                .sortedByDescending { HeartsRules.toRankNumber(it.card) }
                .forEachIndexed { index, it -> it.value += 30 + (index+1) }

            if (cardsInPlayLowerLowest <= 2 && cardsInPlayHigherLowest >= 1) {
                metaCardList
                    .filter { it.card.color == color }
                    .minByOrNull { HeartsRules.toRankNumber(it.card) }!!
                    .value += cardsInPlayHigherLowest * 20
            }
            return this
        }

        if (cardsOfColorInHand >= 4) {
            val lowestCardInHand = cardsInHand.filter { it.color == color }.minByOrNull{HeartsRules.toRankNumber(it)}!!
            val cardsInPlayHigherLowest = cardsInHand.filter { it.color == color }.count{ HeartsRules.higher(it, lowestCardInHand)}
            val cardsInPlayLowerLowest = cardsInHand.filter { it.color == color }.count{ HeartsRules.lower(it, lowestCardInHand)}

            metaCardList
                .filter { it.card.color == color }
                .sortedByDescending { HeartsRules.toRankNumber(it.card) }
                .forEachIndexed { index, it -> it.value += 50 + (index+1) }

            if (cardsInPlayLowerLowest <= 2 && cardsInPlayHigherLowest >= 1) {
                metaCardList
                    .filter { it.card.color == color }
                    .minByOrNull { HeartsRules.toRankNumber(it.card) }!!
                    .value += cardsInPlayHigherLowest * 20
            }
            return this
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

    fun hasOnlyLowerCardsThanLeader(winningCard: Card): Boolean {
        return cardsInHand
            .filter{crd -> crd.color == winningCard.color}
            .all { crd -> HeartsRules.toRankNumber(crd) < HeartsRules.toRankNumber(winningCard) }
    }
    fun hasOnlyHigherCardsThanLeader(winningCard: Card): Boolean {
        return cardsInHand
            .filter{crd -> crd.color == winningCard.color}
            .all { crd -> HeartsRules.toRankNumber(crd) > HeartsRules.toRankNumber(winningCard) }
    }
    fun hasAllCardsOfColor(color: CardColor): Boolean {
        return (cardsInHand.count { cp -> cp.color == color } + cardsPlayed.count { cp -> cp.color == color } == 8)
    }
    fun canGetRidOfLeadPosition(leadColor: CardColor): Boolean {
        return hasLowestCardOfColorInHandAndHigherInPLayExists(leadColor)
        //todo: mag ook een een-na-laagste kaart zijn, mits er nog steeds een hoogste is
        // en kleuren verdeeld over verschillende spelers
    }

    private fun hasLowestCardOfColorInHandAndHigherInPLayExists(color: CardColor): Boolean {
        val lowestCardInHand = lowestCardOfColorInCardList(cardsInHand, color)
        val lowestCardStillInPlay = lowestCardOfColorInCardList(cardsStillInPlay, color)
        if (lowestCardStillInPlay == null || lowestCardInHand == null)
            return false
        return HeartsRules.toRankNumber(lowestCardInHand) < HeartsRules.toRankNumber(lowestCardStillInPlay)
    }
    private fun lowestCardOfColorInCardList(cardList: List<Card>, color: CardColor): Card? {
        return cardList
            .filter { c -> c.color == color}
            .minByOrNull { c -> HeartsRules.toRankNumber(c) }
    }

    //------------------------------------------------------------------------------------------------------------------



}




