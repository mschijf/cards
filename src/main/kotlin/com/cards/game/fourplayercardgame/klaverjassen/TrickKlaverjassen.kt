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
            .maxByOrNull { playerPlayedCard -> playerPlayedCard.card.toRankNumberTrump() }
            ?.card
    }

    private fun legalTrumpCardsToPlay(cardsList: List<Card>):List<Card> {
        val highestTrumpCard = highestTrumpCard()
        val maxTrumpCardRank = if (highestTrumpCard != null) highestTrumpCard.toRankNumberTrump() else Int.MAX_VALUE

        return cardsList
            .filter { card -> (card.color == round.getTrumpColor()) && card.toRankNumberTrump() > maxTrumpCardRank }
            .ifEmpty { cardsList.filter { card -> card.color == round.getTrumpColor() } }
    }


    override fun getLegalPlayableCards(cardsList: List<Card>): List<Card> {
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

    override fun getWinner(): Player? {
        val winningCard = getWinningCard()
        return if (!isComplete()) {
            null
        } else {
            getCardsPlayed().firstOrNull{ playerPlayedCard -> playerPlayedCard.card == winningCard }?.player
        }
    }

    override fun getWinningCard(): Card? {
        return if (getCardsPlayed().any { crd -> crd.card.color == round.getTrumpColor() }) {
            getCardsPlayed()
                .filter { playerPlayedCard -> playerPlayedCard.card.color == round.getTrumpColor() }
                .maxByOrNull { playerPlayedCard -> playerPlayedCard.card.toRankNumberTrump() }
                ?.card
        } else {
            getCardsPlayed()
                .filter { playerPlayedCard -> isLeadColor(playerPlayedCard.card.color) }
                .maxByOrNull { playerPlayedCard -> playerPlayedCard.card.toRankNumberNoTrump() }
                ?.card
        }
    }

    fun getScore(): ScoreKlaverjassen {
        val lastTrickPoints = if (round.isLastTrick(this)) 10 else 0
        return if (!isComplete()) {
            ScoreKlaverjassen.ZERO
        } else {
            ScoreKlaverjassen.scoreForPlayer(
                getWinner()!!,
                lastTrickPoints + getCardsPlayed().sumOf { playerPlayedCard -> playerPlayedCard.card.cardValue(round.getTrumpColor()) },
                getCardsPlayed().map { it.card }.bonusValue(trumpColor = round.getTrumpColor())
            )
        }
    }

}