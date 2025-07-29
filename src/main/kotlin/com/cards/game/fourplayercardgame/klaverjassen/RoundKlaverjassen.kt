package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Round
import com.cards.game.fourplayercardgame.basic.Score
import com.cards.game.fourplayercardgame.basic.Trick


class RoundKlaverjassen(leadPlayer: Player, val trumpColor: CardColor) : Round(leadPlayer) {

    override fun createTrick(leadPlayer: Player): Trick {
        return TrickKlaverjassen(leadPlayer, CardColor.CLUBS)
    }

    override fun isComplete(): Boolean {
        return completedTricksPlayed() >= KLAVERJASSEN.NUMBER_OF_TRICKS_PER_ROUND
    }

    override fun getScore(): Score {
        var score = Score.ZERO
        if (isComplete()) {
            getCompletedTrickList().forEach { trick ->
                score = score.plus(trick.getScore())
            }
        }
        return score
    }
}