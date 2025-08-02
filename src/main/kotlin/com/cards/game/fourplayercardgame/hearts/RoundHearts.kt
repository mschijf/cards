package com.cards.game.fourplayercardgame.hearts

import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Round
import com.cards.game.fourplayercardgame.basic.Trick

class RoundHearts(leadPlayer: Player) : Round(leadPlayer) {

    override fun createTrick(leadPlayer: Player): Trick {
        return TrickHearts(leadPlayer)
    }

    override fun isComplete(): Boolean {
        return completedTricksPlayed() >= NUMBER_OF_TRICKS_PER_ROUND
    }

    //score
    fun getScore(): ScoreHearts {
        var score = ScoreHearts.ZERO
        if (isComplete()) {
            getCompletedTrickList().forEach { trick ->
                score = score.plus((trick as TrickHearts).getScore())
            }
        }
        return score
    }
}