package com.cards.game.fourplayercardgame.hearts

import com.cards.game.fourplayercardgame.basic.Round

class RoundHearts() : Round() {

    override fun isComplete(): Boolean {
        return getTrickList().size == NUMBER_OF_TRICKS_PER_ROUND && getTrickList().last().isComplete()
    }

    //score
    fun getScore(): ScoreHearts {
        var score = ScoreHearts.ZERO
        if (isComplete()) {
            getTrickList().forEach { trick ->
                score = score.plus((trick as TrickHearts).getScore())
            }
        }
        return score
    }
}