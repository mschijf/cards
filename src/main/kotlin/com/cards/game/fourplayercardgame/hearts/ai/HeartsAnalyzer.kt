package com.cards.game.fourplayercardgame.hearts.ai

import com.cards.game.card.CARDDECK
import com.cards.game.card.Card
import com.cards.game.card.CardColor

class HeartsAnalyzer(
    private val cardsInHand : List<Card>,
    private val cardsPlayed: List<Card>,
    private val cardsStillInPlay: List<Card>) {

    val metaCardList = cardsInHand.map { card -> MetaCardInfo(card, 0) }

    private fun toRankNumber (card: Card) : Int = card.rank.rankNumber - 7
    private fun higher (card1: Card, card2: Card) = toRankNumber(card1) > toRankNumber(card2)
    private fun lower (card1: Card, card2: Card) = toRankNumber(card1) < toRankNumber(card2)

    fun getCardAnalysisValue(card: Card): Int? = metaCardList.firstOrNull { metacard -> metacard.card == card }?.value
    
    constructor(cardsInHand: List<Card>):this(cardsInHand, emptyList<Card>(), emptyList<Card>())

    class MetaCardInfo(val card: Card, var value: Int)

    fun evaluateSpecificCard(card: Card, value: Int): HeartsAnalyzer {
        metaCardList
            .filter { metaCardInfo -> metaCardInfo.card == card }
            .forEach { metaCardInfo -> metaCardInfo.value += value }
        return this
    }

    fun evaluateSpecificColor(cardColor: CardColor, value: Int): HeartsAnalyzer {
        metaCardList
            .filter { metaCardInfo -> metaCardInfo.card.color == cardColor }
            .forEach { metaCardInfo -> metaCardInfo.value += value }
        return this
    }

    fun evaluateHighestCardsInColor(value: Int): HeartsAnalyzer {
        metaCardList
            .filter { metaCardInfo -> isHighestCardOfColor(metaCardInfo.card) }
            .forEach { metaCardInfo -> metaCardInfo.value += value }
        return this
    }

    fun evaluateByRank(rankStepValue: Int): HeartsAnalyzer {
        metaCardList
            .forEach { metaCardInfo -> metaCardInfo.value += rankStepValue * toRankNumber(metaCardInfo.card) }
        return this
    }
    

    fun evaluateByRankLowerThanOtherCard(otherCard: Card, baseValue: Int, rankStepValue: Int): HeartsAnalyzer {
        metaCardList
            .filter { metaCardInfo -> metaCardInfo.card.color == otherCard.color }
            .filter { metaCardInfo ->
                toRankNumber(metaCardInfo.card) < toRankNumber(
                    otherCard
                )
            }
            .sortedBy { mc -> toRankNumber(mc.card) }
            .forEachIndexed { index, metaCardInfo -> metaCardInfo.value += baseValue + (index+1) * rankStepValue }
        return this
    }

    fun evaluateByRankHigherThanOtherCard(otherCard: Card, baseValue: Int, rankStepValue: Int): HeartsAnalyzer {
        metaCardList
            .filter { metaCardInfo -> metaCardInfo.card.color == otherCard.color }
            .filter { metaCardInfo ->
                toRankNumber(metaCardInfo.card) > toRankNumber(
                    otherCard
                )
            }
            .sortedBy { mc -> toRankNumber(mc.card) }
            .forEachIndexed { index, metaCardInfo -> metaCardInfo.value += baseValue + (index+1) * rankStepValue }
        return this
    }

    fun evaluateSpecificCardLowerThanOtherCard(card: Card, value: Int, otherCard: Card): HeartsAnalyzer {
        metaCardList
            .filter { metaCardInfo -> metaCardInfo.card == card }
            .filter { metaCardInfo ->
                toRankNumber(metaCardInfo.card) < toRankNumber(otherCard)
            }
            .forEach { metaCardInfo -> metaCardInfo.value += value }
        return this
    }

    fun evaluateSingleCardOfColor(value: Int, higherThanAvailableCard: Card): HeartsAnalyzer {
        val color = higherThanAvailableCard.color
        if (cardsInHand.count { c -> c.color == color } == 1) {
            val single = cardsInHand.first { c -> c.color == color }
            if (cardsStillInPlay.contains(higherThanAvailableCard) ){
                if (toRankNumber(single) > toRankNumber(higherThanAvailableCard)) {
                    metaCardList
                        .filter { metaCardInfo -> metaCardInfo.card.color == color }
                        .forEach { metaCardInfo -> metaCardInfo.value += value }
                }
            }
        }
        return this
    }

    fun evaluateSingleCardOfColor(value: Int, color: CardColor): HeartsAnalyzer {
        if (cardsInHand.count { c -> c.color == color } == 1) {
            val single = cardsInHand.first { c -> c.color == color }

            val countHigher = cardsStillInPlay
                .filter { c -> c.color == color }
                .count { c -> toRankNumber(c) > toRankNumber(single) }
            val countLower = cardsStillInPlay
                .filter { c -> c.color == color }
                .count { c -> toRankNumber(c) < toRankNumber(single) }
            if (countHigher <= 3 && countLower >= 1) {
                metaCardList
                    .filter { metaCardInfo -> metaCardInfo.card.color == color }
                    .forEach { metaCardInfo -> metaCardInfo.value += value }
            }
        }
        return this
    }

    fun evaluateFreeCards(value: Int): HeartsAnalyzer {
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

    fun evaluateLeadPlayerByColor(color: CardColor): HeartsAnalyzer {
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
            val cardsInPlayHigher = cardsStillInPlay.filter { it.color == color }.count{ higher(it, onlyCard)}
            val cardsInPlayLower = cardsStillInPlay.filter { it.color == color }.count{ lower(it, onlyCard)}

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
            val lowestCardInHand = cardsInHand.filter { it.color == color }.minByOrNull{toRankNumber(it)}!!
            val cardsInPlayHigherLowest = cardsInHand.filter { it.color == color }.count{ higher(it, lowestCardInHand)}
            val cardsInPlayLowerLowest = cardsInHand.filter { it.color == color }.count{ lower(it, lowestCardInHand)}

            metaCardList
                .filter { it.card.color == color }
                .sortedByDescending { toRankNumber(it.card) }
                .forEachIndexed { index, it -> it.value += 20 + (index+1) }

            if (cardsInPlayLowerLowest <= 2 && cardsInPlayHigherLowest >= 1) {
                metaCardList
                    .filter { it.card.color == color }
                    .minByOrNull { toRankNumber(it.card) }!!
                    .value += cardsInPlayHigherLowest * 20
            }
            return this
        }

        if (cardsOfColorInHand == 3) {
            //todo: better evaluating
            //todo: check queen of spades, jack of clubs

            val lowestCardInHand = cardsInHand.filter { it.color == color }.minByOrNull{toRankNumber(it)}!!
            val cardsInPlayHigherLowest = cardsInHand.filter { it.color == color }.count{ higher(it, lowestCardInHand)}
            val cardsInPlayLowerLowest = cardsInHand.filter { it.color == color }.count{ lower(it, lowestCardInHand)}

            metaCardList
                .filter { it.card.color == color }
                .sortedByDescending { toRankNumber(it.card) }
                .forEachIndexed { index, it -> it.value += 30 + (index+1) }

            if (cardsInPlayLowerLowest <= 2 && cardsInPlayHigherLowest >= 1) {
                metaCardList
                    .filter { it.card.color == color }
                    .minByOrNull { toRankNumber(it.card) }!!
                    .value += cardsInPlayHigherLowest * 20
            }
            return this
        }

        if (cardsOfColorInHand >= 4) {
            val lowestCardInHand = cardsInHand.filter { it.color == color }.minByOrNull{toRankNumber(it)}!!
            val cardsInPlayHigherLowest = cardsInHand.filter { it.color == color }.count{ higher(it, lowestCardInHand)}
            val cardsInPlayLowerLowest = cardsInHand.filter { it.color == color }.count{ lower(it, lowestCardInHand)}

            metaCardList
                .filter { it.card.color == color }
                .sortedByDescending { toRankNumber(it.card) }
                .forEachIndexed { index, it -> it.value += 50 + (index+1) }

            if (cardsInPlayLowerLowest <= 2 && cardsInPlayHigherLowest >= 1) {
                metaCardList
                    .filter { it.card.color == color }
                    .minByOrNull { toRankNumber(it.card) }!!
                    .value += cardsInPlayHigherLowest * 20
            }
            return this
        }

        return this
    }

    //------------------------------------------------------------------------------------------------------------------

    private fun higherCardsThen(card: Card): List<Card> {
        return CARDDECK.baseDeckCardsSevenAndHigher
            .filter { crd -> crd.color  ==  card.color }
            .filter { crd -> toRankNumber(crd) > toRankNumber(card) }
    }

    private fun isHighestCardOfColor(card: Card): Boolean {
        val nCardsHigherPlayed = (cardsInHand union cardsPlayed)
            .filter { cardPlayed -> cardPlayed.color == card.color }
            .count { cardPlayed -> toRankNumber(cardPlayed) > toRankNumber(card) }
        val nCardsHigher = higherCardsThen(card).count()

        return nCardsHigher == nCardsHigherPlayed
    }

    fun hasOnlyLowerCardsThanLeader(winningCard: Card): Boolean {
        return cardsInHand
            .filter{crd -> crd.color == winningCard.color}
            .all { crd -> toRankNumber(crd) < toRankNumber(winningCard) }
    }
    fun hasOnlyHigherCardsThanLeader(winningCard: Card): Boolean {
        return cardsInHand
            .filter{crd -> crd.color == winningCard.color}
            .all { crd -> toRankNumber(crd) > toRankNumber(winningCard) }
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
        return toRankNumber(lowestCardInHand) < toRankNumber(lowestCardStillInPlay)
    }
    private fun lowestCardOfColorInCardList(cardList: List<Card>, color: CardColor): Card? {
        return cardList
            .filter { c -> c.color == color}
            .minByOrNull { c -> toRankNumber(c) }
    }

    //------------------------------------------------------------------------------------------------------------------



}




