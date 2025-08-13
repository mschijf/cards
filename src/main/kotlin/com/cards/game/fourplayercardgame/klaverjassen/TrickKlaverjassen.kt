package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.basic.Trick

class TrickKlaverjassen(
    leadPosition: TablePosition,
    private val round: RoundKlaverjassen): Trick(leadPosition) {

    override fun getWinner(): TablePosition? {
        return getPositionByCardPlayed(getWinningCard())
    }

    override fun getWinningCard(): Card? {
        return if (getCardsPlayed().any { card -> card.color == round.getTrumpColor() }) {
            getCardsPlayed()
                .filter { card -> card.color == round.getTrumpColor() }
                .maxByOrNull { card -> card.toRankNumberTrump() }
        } else {
            getCardsPlayed()
                .filter { card -> isLeadColor(card.color) }
                .maxByOrNull { card -> card.toRankNumberNoTrump() }
        }
    }

    fun getScore(): ScoreKlaverjassen {
        val lastTrickPoints = if (round.isLastTrick(this)) 10 else 0
        return if (!isComplete()) {
            ScoreKlaverjassen.ZERO
        } else {
            ScoreKlaverjassen.scoreForPlayer(
                getWinner()!!,
                lastTrickPoints + getCardsPlayed().sumOf { card -> card.cardValue(round.getTrumpColor()) },
                getCardsPlayed().bonusValue(trumpColor = round.getTrumpColor())
            )
        }
    }

}