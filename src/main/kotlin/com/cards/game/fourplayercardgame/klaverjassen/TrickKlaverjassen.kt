package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Trick

class TrickKlaverjassen(
    leadPlayer: Player,
    private val round: RoundKlaverjassen): Trick(leadPlayer) {

    private fun highestTrumpCard() : Card? {
        return getCardsPlayed()
            .filter{ playerPlayedCard -> playerPlayedCard.card.color == round.getTrumpColor() }
            .maxByOrNull { playerPlayedCard -> KLAVERJASSEN.toRankNumberTrump(playerPlayedCard.card) }
            ?.card
    }

    private fun legalTrumpCardsToPlay(cardsList: List<Card>):List<Card> {
        val highestTrumpCard = highestTrumpCard()
        val maxTrumpCardRank = if (highestTrumpCard != null) KLAVERJASSEN.toRankNumberTrump(highestTrumpCard) else Int.MAX_VALUE

        return cardsList
            .filter { card -> (card.color == round.getTrumpColor()) && KLAVERJASSEN.toRankNumberTrump(card) > maxTrumpCardRank }
            .ifEmpty { cardsList.filter { card -> card.color == round.getTrumpColor() } }
    }


    override fun legalPlayableCards(cardsList: List<Card>): List<Card> {
        if (hasNotStarted())
            return cardsList

        if (cardsList.any {card -> isLeadColor(card.color)}) {
            if (isLeadColor(round.getTrumpColor())) {
                return legalTrumpCardsToPlay(cardsList).ifEmpty { cardsList }
            } else {
                return cardsList.filter { card -> isLeadColor(card.color) }.ifEmpty { cardsList }
            }
        }

        if (cardsList.any {card -> card.color == round.getTrumpColor()}) {
            return legalTrumpCardsToPlay(cardsList)
        }

        return cardsList
    }

    override fun winner(): Player? {
        val winningCard = winningCard()
        return if (!isComplete()) {
            null
        } else {
            getCardsPlayed().firstOrNull{ playerPlayedCard -> playerPlayedCard.card == winningCard }?.player
        }
    }

    override fun winningCard(): Card? {
        return if (getCardsPlayed().any { crd -> crd.card.color == round.getTrumpColor() }) {
            getCardsPlayed()
                .filter { playerPlayedCard -> playerPlayedCard.card.color == round.getTrumpColor() }
                .maxByOrNull { playerPlayedCard -> KLAVERJASSEN.toRankNumberTrump(playerPlayedCard.card) }
                ?.card
        } else {
            getCardsPlayed()
                .filter { playerPlayedCard -> isLeadColor(playerPlayedCard.card.color) }
                .maxByOrNull { playerPlayedCard -> KLAVERJASSEN.toRankNumberNoTrump(playerPlayedCard.card) }
                ?.card
        }
    }

    fun getScore(): ScoreKlaverjassen {
        return if (!isComplete()) {
            ScoreKlaverjassen.ZERO
        } else {
            ScoreKlaverjassen.scoreForPlayer(
                winner()!!,
                getCardsPlayed().sumOf { playerPlayedCard -> KLAVERJASSEN.cardValue(playerPlayedCard.card, round.getTrumpColor()) },
                KLAVERJASSEN.bonusValue(getCardsPlayed().map { it.card }, trumpColor = round.getTrumpColor())
            )
        }
    }

}