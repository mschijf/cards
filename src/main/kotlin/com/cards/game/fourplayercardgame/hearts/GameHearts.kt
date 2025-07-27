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
import kotlin.math.max

private const val ALL_POINTS_FOR_PIT = 15
const val VALUE_TO_GO_DOWN = 15
const val VALUE_TO_FINISH = 0

class GameHearts(): Game() {

    private var goingDownFromRoundNumber = Int.MAX_VALUE
    fun getGoingDownFromRound() = goingDownFromRoundNumber

    fun getGoingUp() = completeRoundsPlayed().size < goingDownFromRoundNumber

    override fun isFinished() = !getGoingUp() && (getTotalScore().minValue() <= VALUE_TO_FINISH)

    override fun doGameSpecificActionsAfterCompletedRound() {
        if (getGoingUp() && getTotalScore().maxValue() >= VALUE_TO_GO_DOWN) {
            goingDownFromRoundNumber = completeRoundsPlayed().size
        }
    }

    private fun toRankNumber (card: Card) : Int = card.rank.rankNumber - 7

    override fun winnerForTrick(trick: Trick) : Player? {
        return if (!trick.isComplete()) {
            return null
        } else {
            trick.getCardsPlayed()
                .filter { f -> f.card.color == trick.leadColor() }
                .maxByOrNull { f -> toRankNumber(f.card) }!!
                .player
        }
    }

    override fun winningCardForTrick(trick: Trick) : Card? {
        return trick.getCardsPlayed()
            .filter { f -> f.card.color == trick.leadColor() }
            .maxByOrNull { f -> toRankNumber(f.card) }?.card
    }

    override fun legalPlayableCardsForTrick(trickOnTable: Trick, cardsInHand: List<Card>): List<Card> {
        val leadColor = trickOnTable.leadColor()
        val legalCards = legalPlayableCards(cardsInHand, leadColor)
        return legalCards
    }

    private fun legalPlayableCards (cardList: List<Card>, leadColor: CardColor?) : List<Card> {
        if (leadColor == null)
            return cardList

        return cardList.filter{ c -> c.color == leadColor}.ifEmpty { cardList }
    }


    override fun roundIsComplete(round: Round): Boolean = round.completedTricksPlayed() >= Table.nTricksPerRound

    private fun cardValue(card: Card): Int {
        return if (card.color == CardColor.HEARTS) {
            1
        } else if (card.color == CardColor.SPADES) {
            if (card.rank == CardRank.QUEEN) 5 else 0
        } else if (card.color == CardColor.CLUBS) {
            if (card.rank == CardRank.JACK) 2 else 0
        } else {
            0
        }
    }

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

    override fun getValueForTrick(trick: Trick)  = trick.getCardsPlayed().sumOf { c -> cardValue(c.card) }

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
