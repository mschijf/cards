package com.cards.game.fourplayercardgame.hearts

import com.cards.game.fourplayercardgame.basic.Game
import com.cards.game.fourplayercardgame.basic.GameRules
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.Score

private const val VALUE_TO_GO_DOWN = 60
private const val ALL_POINTS_FOR_PIT = 15
private const val VALUE_TO_FINISH = 0

class GameHearts(seed: Int): Game(seed) {

    private var goingDownFromRoundNumber = Int.MAX_VALUE

    fun getGoingUp() = completeRoundsPlayed() < goingDownFromRoundNumber

    override fun getGameRules(): GameRules {
        return GameRulesHearts()
    }

    override fun isFinished() = !getGoingUp() && (getTotalScore().minValue() <= VALUE_TO_FINISH)

    override fun determineRoundScore(roundNumber: Int, score: Score): Score {
        if (roundNumber < goingDownFromRoundNumber) {
            if (score.maxValue() == ALL_POINTS_FOR_PIT) {
                val newScore = Score()
                newScore.plusScorePerPlayer(Player.EAST, if (score.getEastValue() == 0) ALL_POINTS_FOR_PIT else 0)
                newScore.plusScorePerPlayer(Player.WEST, if (score.getWestValue() == 0) ALL_POINTS_FOR_PIT else 0)
                newScore.plusScorePerPlayer(Player.NORTH, if (score.getNorthValue() == 0) ALL_POINTS_FOR_PIT else 0)
                newScore.plusScorePerPlayer(Player.SOUTH, if (score.getSouthValue() == 0) ALL_POINTS_FOR_PIT else 0)
                return newScore
            }
            return score
        } else {
            val newScore = Score()
            newScore.plusScorePerPlayer(Player.EAST, -score.getEastValue())
            newScore.plusScorePerPlayer(Player.WEST, -score.getWestValue())
            newScore.plusScorePerPlayer(Player.NORTH, -score.getNorthValue())
            newScore.plusScorePerPlayer(Player.SOUTH, -score.getSouthValue())
            return newScore
        }
    }

    override fun doGameSpecificActionsAfterCompletedRound() {
        if (getGoingUp() && getTotalScore().maxValue() >= VALUE_TO_GO_DOWN) {
            goingDownFromRoundNumber = completeRoundsPlayed()
        }
    }

}
