package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Score
import com.cards.game.fourplayercardgame.basic.Trick
import kotlin.collections.ifEmpty

class TrickKlaverjassen(leadPlayer: Player, val trumpColor: CardColor): Trick(leadPlayer) {

    private fun highestTrumpCard() : Card? {
        return getCardsPlayed()
            .filter{ playerPlayedCard -> playerPlayedCard.card.color == trumpColor }
            .maxByOrNull { playerPlayedCard -> KLAVERJASSEN.toRankNumberTrump(playerPlayedCard.card) }
            ?.card
    }

    private fun legalTrumpCardsToPlay(cardsList: List<Card>):List<Card> {
        val highestTrumpCard = highestTrumpCard()
        val maxTrumpCardRank = if (highestTrumpCard != null) KLAVERJASSEN.toRankNumberTrump(highestTrumpCard) else Int.MAX_VALUE

        return cardsList
            .filter { card -> (card.color == trumpColor) && KLAVERJASSEN.toRankNumberTrump(card) > maxTrumpCardRank }
            .ifEmpty { cardsList.filter { card -> card.color == trumpColor } }
    }


    override fun legalPlayableCards(cardsList: List<Card>): List<Card> {
        if (hasNotStarted())
            return cardsList

        if (cardsList.any {card -> isLeadColor(card.color)}) {
            if (isLeadColor(trumpColor)) {
                return legalTrumpCardsToPlay(cardsList).ifEmpty { cardsList }
            } else {
                return cardsList.filter { card -> isLeadColor(card.color) }.ifEmpty { cardsList }
            }
        }

        if (cardsList.any {card -> card.color == trumpColor}) {
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
        return if (getCardsPlayed().any { crd -> crd.card.color == trumpColor }) {
            getCardsPlayed()
                .filter { playerPlayedCard -> playerPlayedCard.card.color == trumpColor }
                .maxByOrNull { playerPlayedCard -> KLAVERJASSEN.toRankNumberTrump(playerPlayedCard.card) }
                ?.card
        } else {
            getCardsPlayed()
                .filter { playerPlayedCard -> isLeadColor(playerPlayedCard.card.color) }
                .maxByOrNull { playerPlayedCard -> KLAVERJASSEN.toRankNumberNoTrump(playerPlayedCard.card) }
                ?.card
        }
    }

    override fun getScore(): Score {
        return if (!isComplete()) {
            Score.ZERO
        } else {
            Score.scoreForPlayer(
                winner()!!,
                getCardsPlayed().sumOf { playerPlayedCard -> KLAVERJASSEN.cardValue(playerPlayedCard.card, trumpColor) }
            )
        }
    }

}