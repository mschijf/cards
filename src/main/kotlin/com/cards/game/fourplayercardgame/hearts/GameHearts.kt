package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.Game
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.basic.Round
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Trick
import com.cards.game.fourplayercardgame.hearts.HeartsConstants.ALL_POINTS_FOR_PIT
import com.cards.game.fourplayercardgame.hearts.HeartsConstants.VALUE_TO_FINISH
import com.cards.game.fourplayercardgame.hearts.HeartsConstants.VALUE_TO_GO_DOWN
import com.cards.game.fourplayercardgame.hearts.ai.GeniusPlayerHearts
import kotlin.math.max

class GameHearts(): Game() {

    fun isGoingUp() = getCompleteRoundsPlayed().size < goingDownFromRoundNumber()

    private fun cardValue(card: Card): Int {
        return when (card.color) {
            CardColor.HEARTS -> 1
            CardColor.CLUBS -> if (card.rank == CardRank.JACK) 2 else 0
            CardColor.SPADES -> if (card.rank == CardRank.QUEEN) 5 else 0
            CardColor.DIAMONDS -> 0
        }
    }

    //player
    override fun initialPlayerList(): List<Player> {
        return TablePosition.values().map { p -> GeniusPlayerHearts(p, this) }
    }

    //round
    override fun createFirstRound(): Round {
        return RoundHearts(getCardPlayer(TablePosition.WEST))
    }

    override fun createNextRound(previousRound: Round): Round {
        return RoundHearts(previousRound.getLeadPlayer().nextPlayer())
    }

    //game
    override fun isFinished() = !isGoingUp() && (getTotalScore().minValue() <= VALUE_TO_FINISH)

    //score
    fun getValueForTrick(trick: Trick)  = trick.getCardsPlayed().sumOf { c -> cardValue(c.card) }

    fun getScoreForTrick(trick: Trick): ScoreHearts {
        return if (!trick.isComplete()) {
            ScoreHearts.ZERO
        } else {
            ScoreHearts.scoreForPlayer(
                trick.winner()!!,
                getPlayerList().sumOf { player -> cardValue(trick.getCardPlayedBy(player)!!) }
            )
        }
    }

    private fun getBasicScoreForRound(round: Round): ScoreHearts {
        var score = ScoreHearts.ZERO
        if (round.isComplete()) {
            round.getCompletedTrickList().forEach { trick ->
                score = score.plus(getScoreForTrick(trick))
            }
        }
        return score
    }

    private var goingDownRoundNumber: Int? = null
    private fun goingDownFromRoundNumber(): Int {
        if (goingDownRoundNumber != null)
            return goingDownRoundNumber!!

        var score = ScoreHearts.ZERO
        getCompleteRoundsPlayed().forEachIndexed { idx, round ->
            score = score.plus(getBasicScoreForRound(round))
            if (score.maxValue() >= VALUE_TO_GO_DOWN) {
                goingDownRoundNumber = idx+1
                return idx + 1
            }
        }
        return Int.MAX_VALUE
    }

    private fun getScoreForRound(game: Game, round: Round): ScoreHearts {
        val score = getBasicScoreForRound(round)
        val roundNumber = max(0, game.getCompleteRoundsPlayed().indexOf(round))

        val goingDown = roundNumber >= goingDownFromRoundNumber()
        if (!goingDown) {
            if (score.maxValue() == ALL_POINTS_FOR_PIT) {
                return ScoreHearts(
                    westValue = if (score.westValue == 0) ALL_POINTS_FOR_PIT else 0,
                    northValue = if (score.northValue == 0) ALL_POINTS_FOR_PIT else 0,
                    eastValue = if (score.eastValue == 0) ALL_POINTS_FOR_PIT else 0,
                    southValue = if (score.southValue == 0) ALL_POINTS_FOR_PIT else 0
                )
            }
            return score
        } else {
            return ScoreHearts.ZERO.minus(score)
        }
    }

    fun getTotalScore(): ScoreHearts {
        return getCumulativeScorePerRound().lastOrNull()?: ScoreHearts.ZERO
    }

    private fun getScorePerRound(): List<ScoreHearts> {
        return getCompleteRoundsPlayed().mapIndexed { index, round ->  getScoreForRound(this, round)}
    }

    fun getCumulativeScorePerRound(): List<ScoreHearts> {
        val list = getScorePerRound()
        val x = list.runningFold(ScoreHearts.ZERO) { acc, sc -> acc.plus(sc) }.drop(1)
        return x
    }



}
