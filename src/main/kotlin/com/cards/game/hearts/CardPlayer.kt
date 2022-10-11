package com.cards.game.hearts

import com.cards.game.fourplayercardgame.Player
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank

class CardPlayer(
    val player: Player,
    private val game: Game) {
    private var cardsInHand: MutableList<Card> = mutableListOf()

    fun getCardsInHand() = cardsInHand.toList()

    fun setCardsInHand(cardsfromDealer: List<Card>) {
        cardsInHand = cardsfromDealer.toMutableList()
        cardsInHand.sortBy { card -> 100*card.color.ordinal + card.rank.ordinal }
    }

    fun removeCard(card: Card) {
        if (!cardsInHand.remove(card)) {
            throw Exception("cannot remove card $card from hand of player $player ")
        }
    }

    fun chooseCard() : Card {
        return measureCardValues()
            .shuffled()
            .maxByOrNull { metaCard -> metaCard.value }!!
            .card
    }


    private fun measureCardValues(): List<MetaCardInfo> {
        val leadColor = game.getCurrentRound().getTrickOnTable().leadColor()
        val legalCards = HeartsRulesBook.legalPlayableCards(cardsInHand, leadColor)
        val metaCardList = legalCards.map { card -> MetaCardInfo(card) }
        val trickLeadCard = game.getCurrentRound().getTrickOnTable().winningCard()

        if (leadColor != null) {
            if (leadColor != metaCardList.first().card.color) {
                metaCardList
                    .sortedBy { mc -> 100 * mc.card.color.ordinal + HeartsRulesBook.toRankNumber(mc.card) }
                    .forEachIndexed { index, metaCardInfo -> metaCardInfo.value = index }
                metaCardList
                    .filter { metaCardInfo -> metaCardInfo.card == Card(CardColor.SPADES, CardRank.QUEEN) }
                    .forEach { metaCardInfo -> metaCardInfo.value += 200 }
                metaCardList
                    .filter { metaCardInfo -> metaCardInfo.card == Card(CardColor.CLUBS, CardRank.JACK) }
                    .forEach { metaCardInfo -> metaCardInfo.value += 100 }
                metaCardList
                    .filter { metaCardInfo -> isHighestCardOfColor(metaCardInfo.card) }
                    .forEach { metaCardInfo -> metaCardInfo.value += 50 }
                metaCardList
                    .filter { metaCardInfo -> metaCardInfo.card.color == CardColor.HEARTS }
                    .forEach { metaCardInfo -> metaCardInfo.value += 30 }
            } else {
                metaCardList
                    .filter { metaCardInfo -> HeartsRulesBook.toRankNumber(metaCardInfo.card) < HeartsRulesBook.toRankNumber(trickLeadCard!!) }
                    .filter { metaCardInfo -> metaCardInfo.card == Card(CardColor.SPADES, CardRank.QUEEN) }
                    .forEach {metaCardInfo -> metaCardInfo.value += 200 }
                metaCardList
                    .filter { metaCardInfo -> HeartsRulesBook.toRankNumber(metaCardInfo.card) < HeartsRulesBook.toRankNumber(trickLeadCard!!) }
                    .filter { metaCardInfo -> metaCardInfo.card == Card(CardColor.CLUBS, CardRank.JACK) }
                    .forEach {metaCardInfo -> metaCardInfo.value += 100 }
                metaCardList
                    .filter { metaCardInfo -> HeartsRulesBook.toRankNumber(metaCardInfo.card) < HeartsRulesBook.toRankNumber(trickLeadCard!!) }
                    .sortedBy { mc -> HeartsRulesBook.toRankNumber(mc.card) }
                    .forEachIndexed { index, metaCardInfo -> metaCardInfo.value += 51 + index }
                metaCardList
                    .filter { metaCardInfo -> HeartsRulesBook.toRankNumber(metaCardInfo.card) > HeartsRulesBook.toRankNumber(trickLeadCard!!) }
                    .sortedBy { mc -> HeartsRulesBook.toRankNumber(mc.card) }
                    .forEachIndexed { index, metaCardInfo -> metaCardInfo.value += 50 - index }
            }
        }
        return metaCardList
    }

    private fun isHighestCardOfColor(card: Card): Boolean {
        val round = game.getCurrentRound()
        val cardsPlayed  = round
            .getCompletedTrickList()
            .flatMap{ trick -> trick.getCardsPlayed()}
            .union (round.getTrickOnTable().getCardsPlayed())
            .map { cardPlayed -> cardPlayed.card}

        val nCardsHigherPlayed = (cardsInHand union cardsPlayed)
            .filter { cardPlayed -> cardPlayed.color == card.color }
            .count { cardPlayed -> HeartsRulesBook.toRankNumber(cardPlayed) > HeartsRulesBook.toRankNumber(card) }
        val nCardsHigher = HeartsRulesBook.higherCardsThen(card).count()

        return nCardsHigher == nCardsHigherPlayed
    }
}

class MetaCardInfo(
    val card: Card) {
    var value = 0
}
