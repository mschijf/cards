package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.basic.Trick

//todo: heb ik round nodig? Kan het ook anders?
//todo: round ook op basic nivo meegeven?

class TrickKlaverjassen(
    leadPosition: TablePosition,
    private val round: RoundKlaverjassen): Trick(leadPosition) {


    override fun getWinner(): TablePosition? {
        val winningCard = getWinningCard()
        return if (!isComplete()) {
            null
        } else {
            getCardsPlayed().firstOrNull{ playerPlayedCard -> playerPlayedCard.card == winningCard }?.tablePosition
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