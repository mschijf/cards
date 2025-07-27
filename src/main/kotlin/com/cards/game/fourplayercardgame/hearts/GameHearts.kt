package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.Game
import com.cards.game.fourplayercardgame.Player
import com.cards.game.fourplayercardgame.Round
import com.cards.game.fourplayercardgame.Score
import com.cards.game.fourplayercardgame.Table
import com.cards.game.fourplayercardgame.Trick
import kotlin.collections.filter
import kotlin.collections.ifEmpty
import kotlin.math.max

private const val ALL_POINTS_FOR_PIT = 15
private const val VALUE_TO_GO_DOWN = 15
private const val VALUE_TO_FINISH = 0

class GameHearts(): Game() {

    private var goingDownFromRoundNumber = Int.MAX_VALUE

    fun getGoingDownFromRound() = goingDownFromRoundNumber

    fun getGoingUp() = completeRoundsPlayed().size < goingDownFromRoundNumber

    private fun toRankNumber (card: Card) : Int = card.rank.rankNumber - 7

    private fun cardValue(card: Card): Int {
        return when (card.color) {
            CardColor.HEARTS -> 1
            CardColor.CLUBS -> if (card.rank == CardRank.JACK) 2 else 0
            CardColor.SPADES -> if (card.rank == CardRank.QUEEN) 5 else 0
            CardColor.DIAMONDS -> 0
        }
    }



    //trick
    override fun winnerForTrick(trick: Trick) : Player? {
        return if (!trick.isComplete()) {
            null
        } else {
            trick.getCardsPlayed()
                .filter { f -> f.card.color == trick.leadColor() }
                .maxByOrNull { f -> toRankNumber(f.card) }
                ?.player
        }
    }

    override fun winningCardForTrick(trick: Trick) : Card? {
        return trick.getCardsPlayed()
            .filter { f -> f.card.color == trick.leadColor() }
            .maxByOrNull { f -> toRankNumber(f.card) }
            ?.card
    }

    override fun legalPlayableCardsForTrick(trickOnTable: Trick, cardsInHand: List<Card>): List<Card> {
        return cardsInHand
            .filter{ card -> card.color == trickOnTable.leadColor()}
            .ifEmpty { cardsInHand }
    }

    override fun getValueForTrick(trick: Trick)  = trick.getCardsPlayed().sumOf { c -> cardValue(c.card) }

    //round
    override fun roundIsComplete(round: Round): Boolean = round.completedTricksPlayed() >= Table.nTricksPerRound

    override fun doGameSpecificActionsAfterCompletedRoundPlayed() {
        if (getGoingUp() && getTotalScore().maxValue() >= VALUE_TO_GO_DOWN) {
            goingDownFromRoundNumber = completeRoundsPlayed().size
        }
    }

    //game
    override fun isFinished() = !getGoingUp() && (getTotalScore().minValue() <= VALUE_TO_FINISH)

    //score
    override fun getScoreForTrick(trick: Trick): Score {
        val score = Score()
        if (trick.isComplete()) {
            val winner = trick.winner()
            score.plusScorePerPlayer(
                winner!!,
                Player.values().sumOf {p -> cardValue(trick.getCardPlayedBy(p)!!)})
        }
        return score
    }

    override fun getScoreForRound(game: Game, round: Round): Score {
        val score = Score()
        if (!round.isComplete()) {
            return score
        }
        round.getCompletedTrickList().forEach { trick ->
            score.plus(getScoreForTrick(trick))
        }
        val roundNumber = max(0, game.completeRoundsPlayed().indexOf(round))

        val goingDown = roundNumber >= (game as GameHearts).getGoingDownFromRound()
        if (!goingDown) {
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
}
