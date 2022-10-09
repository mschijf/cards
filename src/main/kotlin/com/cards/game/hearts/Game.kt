package com.cards.game.hearts

import com.cards.game.fourplayercardgame.Player
import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.Round
import com.cards.game.fourplayercardgame.Score

class Game (
    private var leadPlayer: Player,
    private val maxTricksPerRound: Int) {
    private val completedRoundList = arrayListOf<Round>()
    private var currentRound = Round(leadPlayer, maxTricksPerRound)
    private var goingUp = true

    fun playCard(card: Card) {
        if (isFinished()) {
            throw Exception("Trying to play a card, but the game is already over")
        }

        currentRound.playCard(card)
        if (currentRound.isComplete()) {
            addRound(currentRound)
            if (!isFinished()) {
                currentRound = Round(leadPlayer.nextPlayer(), maxTricksPerRound)
                goingUp = goingUp && (getTotalScore().maxValue() < HeartsRulesBook.valueToGoDown)
            }
        }
    }

    private fun addRound(round: Round) {
        if (!isFinished()) {
            completedRoundList.add(round)
        } else {
            throw Exception("Trying to add a deals to a finished game")
        }
    }

    fun isFinished() = (!goingUp) && (getTotalScore().minValue() < HeartsRulesBook.valueToFinish)

    private fun getLastTrickWinner(): Player? {
        if (!currentRound.isNew()) {
            return currentRound.getLastCompletedTrickWinner()
        }
        return completedRoundList.last().getLastCompletedTrickWinner()
    }

    fun getCurrentRound() = currentRound
    fun getPlayerToMove() = currentRound.getTrickOnTable().playerToMove()
    fun getStatusAfterLastMove() = GameStatusAfterLastMove(
        currentRound.getTrickOnTable().isNew(),
        getLastTrickWinner(),
        currentRound.isNew(),
        isFinished()
        )
    fun getGoingUp() = goingUp

    private fun getTotalScore(): Score {
        val score = Score()
        completedRoundList.forEach { r ->  score.plus(determineRoundScore(r.getScore()))}
        return score
    }

    private fun determineRoundScore(score: Score): Score {
        if (score.maxValue() == HeartsRulesBook.allPointsForPit) {
            val newScore = Score()
            newScore.plusScorePerPlayer(Player.EAST, if (score.getEastValue() == 0) HeartsRulesBook.allPointsForPit else 0)
            newScore.plusScorePerPlayer(Player.WEST, if (score.getWestValue() == 0) HeartsRulesBook.allPointsForPit else 0)
            newScore.plusScorePerPlayer(Player.NORTH, if (score.getNorthValue() == 0) HeartsRulesBook.allPointsForPit else 0)
            newScore.plusScorePerPlayer(Player.SOUTH, if (score.getSouthValue() == 0) HeartsRulesBook.allPointsForPit else 0)
            return newScore
        }
        return score
    }

    private fun getScorePerRound(): List<Score> {
        return completedRoundList.map { r ->  determineRoundScore(r.getScore())}
    }

    fun getCumulativeScorePerRound(): List<Score> {
        val list = getScorePerRound()
        list.forEachIndexed{ index, sc ->  sc.plus(if (index > 0) list[index-1] else Score()) }
        return list
    }
}

data class GameStatusAfterLastMove(
    val trickCompleted: Boolean, val trickWinner: Player?, val roundCompleted: Boolean, val gameFinished: Boolean)