package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.Game
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.basic.Round
import com.cards.game.fourplayercardgame.basic.Score
import com.cards.game.fourplayercardgame.basic.CardPlayer
import com.cards.game.fourplayercardgame.basic.Trick
import kotlin.collections.filter
import kotlin.collections.ifEmpty
import kotlin.math.max

private const val ALL_POINTS_FOR_PIT = 15
private const val VALUE_TO_GO_DOWN = 14
private const val VALUE_TO_FINISH = 0

class GameHearts(): Game() {

    fun isGoingUp() = completeRoundsPlayed().size < goingDownFromRoundNumber()


    private fun toRankNumber (card: Card) : Int = card.rank.rankNumber - 7

    private fun cardValue(card: Card): Int {
        return when (card.color) {
            CardColor.HEARTS -> 1
            CardColor.CLUBS -> if (card.rank == CardRank.JACK) 2 else 0
            CardColor.SPADES -> if (card.rank == CardRank.QUEEN) 5 else 0
            CardColor.DIAMONDS -> 0
        }
    }

    //player
    override fun initialPlayerList(): List<CardPlayer> {
        return TablePosition.values().map { p -> GeniusHeartsPlayer(p, this) }
    }

    override fun nextPlayer(player: CardPlayer): CardPlayer {
        return getCardPlayer(player.tablePosition.neighbour())
    }

    //trick
    override fun winnerForTrick(trick: Trick) : CardPlayer? {
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
    override fun roundIsComplete(round: Round): Boolean = round.completedTricksPlayed() >= numberOfTricksPerRound()

    //game
    override fun isFinished() = !isGoingUp() && (getTotalScore().minValue() <= VALUE_TO_FINISH)

    //score
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
                var newScore = Score.ZERO
                newScore = newScore.plusForPlayer(getCardPlayer(TablePosition.WEST), if (score.westValue == 0) ALL_POINTS_FOR_PIT else 0)
                newScore = newScore.plusForPlayer(getCardPlayer(TablePosition.NORTH), if (score.northValue == 0) ALL_POINTS_FOR_PIT else 0)
                newScore = newScore.plusForPlayer(getCardPlayer(TablePosition.EAST), if (score.eastValue == 0) ALL_POINTS_FOR_PIT else 0)
                newScore = newScore.plusForPlayer(getCardPlayer(TablePosition.SOUTH), if (score.southValue == 0) ALL_POINTS_FOR_PIT else 0)
                return newScore
            }
            return score
        } else {
            var newScore = Score.ZERO
            newScore = newScore.plusForPlayer(getCardPlayer(TablePosition.WEST), -score.westValue)
            newScore = newScore.plusForPlayer(getCardPlayer(TablePosition.NORTH), -score.northValue)
            newScore = newScore.plusForPlayer(getCardPlayer(TablePosition.EAST), -score.eastValue)
            newScore = newScore.plusForPlayer(getCardPlayer(TablePosition.SOUTH), -score.southValue)
            return newScore
        }
    }
}
