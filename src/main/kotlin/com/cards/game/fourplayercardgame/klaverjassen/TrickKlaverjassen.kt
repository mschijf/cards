package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Score
import com.cards.game.fourplayercardgame.basic.Trick

class TrickKlaverjassen(leadPlayer: Player, val trumpColor: CardColor): Trick(leadPlayer) {

    override fun legalPlayableCards(cardsList: List<Card>): List<Card> {
        return if (hasNotStarted()) {
            cardsList
        } else {
            cardsList
                .filter { card -> isLeadColor(card.color) }
                .ifEmpty {
                    cardsList.filter { card -> card.color == trumpColor }.ifEmpty { cardsList }
                }
        }
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