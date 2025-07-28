package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.Game
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.basic.Round
import com.cards.game.fourplayercardgame.basic.Score
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Trick
import com.cards.game.fourplayercardgame.hearts.HeartsConstants.ALL_POINTS_FOR_PIT
import com.cards.game.fourplayercardgame.hearts.HeartsConstants.VALUE_TO_FINISH
import com.cards.game.fourplayercardgame.hearts.HeartsConstants.VALUE_TO_GO_DOWN
import com.cards.game.fourplayercardgame.hearts.ai.GeniusPlayerHearts
import kotlin.math.max

class GameHearts(): Game() {

    fun isGoingUp() = completeRoundsPlayed().size < goingDownFromRoundNumber()

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
    override fun getValueForTrick(trick: Trick)  = trick.getCardsPlayed().sumOf { c -> cardValue(c.card) }

    override fun getScoreForTrick(trick: Trick): Score {
        return if (!trick.isComplete()) {
            Score.ZERO
        } else {
            Score.scoreForPlayer(
                trick.winner()!!,
                getPlayerList().sumOf { player -> cardValue(trick.getCardPlayedBy(player)!!) }
            )
        }
    }

    private fun getBasicScoreForRound(round: Round): Score {
        var score = Score.ZERO
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

        var score = Score.ZERO
        completeRoundsPlayed().forEachIndexed { idx, round ->
            score = score.plus(getBasicScoreForRound(round))
            if (score.maxValue() >= VALUE_TO_GO_DOWN) {
                goingDownRoundNumber = idx+1
                return idx + 1
            }
        }
        return Int.MAX_VALUE
    }

    override fun getScoreForRound(game: Game, round: Round): Score {
        val score = getBasicScoreForRound(round)
        val roundNumber = max(0, game.completeRoundsPlayed().indexOf(round))

        val goingDown = roundNumber >= goingDownFromRoundNumber()
        if (!goingDown) {
            if (score.maxValue() == ALL_POINTS_FOR_PIT) {
                return Score(
                    westValue = if (score.westValue == 0) ALL_POINTS_FOR_PIT else 0,
                    northValue = if (score.northValue == 0) ALL_POINTS_FOR_PIT else 0,
                    eastValue = if (score.eastValue == 0) ALL_POINTS_FOR_PIT else 0,
                    southValue = if (score.southValue == 0) ALL_POINTS_FOR_PIT else 0
                )
            }
            return score
        } else {
            return Score.ZERO.minus(score)
        }
    }
}
