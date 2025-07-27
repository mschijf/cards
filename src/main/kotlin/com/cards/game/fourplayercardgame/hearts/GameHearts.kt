package com.cards.game.fourplayercardgame.hearts

import com.cards.game.fourplayercardgame.Game
import com.cards.game.fourplayercardgame.GameRules

class GameHearts(): Game() {

    private var goingDownFromRoundNumber = Int.MAX_VALUE
    fun getGoingDownFromRound() = goingDownFromRoundNumber

    fun getGoingUp() = completeRoundsPlayed().size < goingDownFromRoundNumber

    override fun getGameRules(): GameRules {
        return GameRulesHearts()
    }

    override fun isFinished() = !getGoingUp() && (getTotalScore().minValue() <= VALUE_TO_FINISH)

    override fun doGameSpecificActionsAfterCompletedRound() {
        if (getGoingUp() && getTotalScore().maxValue() >= VALUE_TO_GO_DOWN) {
            goingDownFromRoundNumber = completeRoundsPlayed().size
        }
    }

}
