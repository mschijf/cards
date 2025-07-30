package com.cards.game.fourplayercardgame.hearts

import com.cards.game.fourplayercardgame.basic.Game
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Round
import com.cards.game.fourplayercardgame.basic.Table
import com.cards.game.fourplayercardgame.hearts.HEARTS.ALL_POINTS_FOR_PIT
import com.cards.game.fourplayercardgame.hearts.HEARTS.VALUE_TO_FINISH
import com.cards.game.fourplayercardgame.hearts.HEARTS.VALUE_TO_GO_DOWN
import com.cards.game.fourplayercardgame.hearts.ai.GeniusPlayerHearts
import kotlin.math.max

class GameHearts(): Game() {

    fun isGoingUp() = getCompleteRoundsPlayed().size < goingDownFromRoundNumber()

    //player
    override fun initialPlayerList(): List<Player> {
        return Table.values().map { p -> GeniusPlayerHearts(p, this) }
    }

    //round
    override fun createFirstRound(): Round {
        return RoundHearts(getCardPlayer(HEARTS.VERY_FIRST_START_PLAYER))
    }

    override fun createNextRound(previousRound: Round): Round {
        return RoundHearts(previousRound.getLeadPlayer().nextPlayer())
    }

    //game
    override fun isFinished() = !isGoingUp() && (getTotalScore().minValue() <= VALUE_TO_FINISH)

    //score
    fun getCumulativeScorePerRound(): List<ScoreHearts> {
        return getCompleteRoundsPlayed()
            .mapIndexed { index, round ->  getGameScoreForRound(round)}
            .runningFold(ScoreHearts.ZERO) { acc, sc -> acc.plus(sc) }.drop(1)
    }

    private fun getTotalScore(): ScoreHearts {
        return getCumulativeScorePerRound().lastOrNull()?: ScoreHearts.ZERO
    }

    private var goingDownRoundNumber: Int? = null
    private fun goingDownFromRoundNumber(): Int {
        if (goingDownRoundNumber != null)
            return goingDownRoundNumber!!

        var score = ScoreHearts.ZERO
        getCompleteRoundsPlayed().forEachIndexed { idx, round ->
            score = score.plus((round as RoundHearts).getScore())
            if (score.maxValue() >= VALUE_TO_GO_DOWN) {
                goingDownRoundNumber = idx+1
                return idx + 1
            }
        }
        return Int.MAX_VALUE
    }

    private fun getGameScoreForRound(round: Round): ScoreHearts {
        val score = (round as RoundHearts).getScore()
        val roundNumber = max(0, getCompleteRoundsPlayed().indexOf(round))

        val goingUp = roundNumber < goingDownFromRoundNumber()
        return if (goingUp) {
            if (score.maxValue() == ALL_POINTS_FOR_PIT) {
                ScoreHearts(
                    westValue = if (score.westValue == 0) ALL_POINTS_FOR_PIT else 0,
                    northValue = if (score.northValue == 0) ALL_POINTS_FOR_PIT else 0,
                    eastValue = if (score.eastValue == 0) ALL_POINTS_FOR_PIT else 0,
                    southValue = if (score.southValue == 0) ALL_POINTS_FOR_PIT else 0
                )
            } else {
                score
            }
        } else {
            ScoreHearts.ZERO.minus(score)
        }
    }
}
