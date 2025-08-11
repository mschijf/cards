package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.basic.Trick

class TrickHearts(leadPosition: TablePosition): Trick(leadPosition) {

    override fun getWinner(): TablePosition? {
        return if (!isComplete()) {
            null
        } else {
            getCardsPlayed()
                .filter { playerPlayedCard -> isLeadColor(playerPlayedCard.card.color) }
                .maxByOrNull { playerPlayedCard -> playerPlayedCard.card.toRankNumber() }
                ?.position
        }
    }

    override fun getWinningCard(): Card? {
        return getCardsPlayed()
            .filter { playerPlayedCard -> isLeadColor(playerPlayedCard.card.color) }
            .maxByOrNull { playerPlayedCard -> playerPlayedCard.card.toRankNumber() }
            ?.card
    }

    fun getScore(): ScoreHearts {
        return if (!isComplete()) {
            ScoreHearts.ZERO
        } else {
            ScoreHearts.scoreForPlayer(getWinner()!!, getCardsPlayed().sumOf { cp -> cp.card.cardValue() })
        }
    }

}