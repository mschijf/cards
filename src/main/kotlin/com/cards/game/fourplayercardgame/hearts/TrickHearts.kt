package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.basic.Trick

class TrickHearts(leadPosition: TablePosition): Trick(leadPosition) {

    override fun getWinner(): TablePosition? {
        return getPositionByCardPlayed(getWinningCard())
    }

    override fun getWinningCard(): Card? {
        return getCardsPlayed()
            .filter { card -> isLeadColor(card.color) }
            .maxByOrNull { card -> card.toRankNumber() }
    }

    fun getScore(): ScoreHearts {
        return if (!isComplete()) {
            ScoreHearts.ZERO
        } else {
            ScoreHearts.scoreForPlayer(getWinner()!!, getCardsPlayed().sumOf { card -> card.cardValue() })
        }
    }

}