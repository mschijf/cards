package com.cards.game.hearts

import com.cards.game.fourplayercardgame.Player
import com.cards.game.card.Card
import com.cards.game.card.CardDeck

class Game (
    private var leadPlayer: Player) {
    val cardDeck = CardDeck()
    private val maxTricksPerRound = cardDeck.numberOfCards() / Player.values().size
    private var goingDownFromRoundNumber = Int.MAX_VALUE

    private val completedRoundList = arrayListOf<Round>()
    private var currentRound = Round(leadPlayer, maxTricksPerRound)


    fun playCard(card: Card) {
        if (isFinished()) {
            throw Exception("Trying to play a card, but the game is already over")
        }

        currentRound.playCard(card)
        if (currentRound.isComplete()) {
            addRound(currentRound)
            currentRound = Round(leadPlayer.nextPlayer(), maxTricksPerRound)
            if (getGoingUp() && getTotalScore().maxValue() >= HeartsRulesBook.valueToGoDown) {
                goingDownFromRoundNumber = completedRoundList.size
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

    private fun isFinished() = !getGoingUp() && (getTotalScore().minValue() <= HeartsRulesBook.valueToFinish)

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
    fun getGoingUp() = completedRoundList.size < goingDownFromRoundNumber

    private fun getTotalScore(): Score {
        val score = Score()
        completedRoundList.forEachIndexed { index, r ->  score.plus(determineRoundScore(index, r.getScore()))}
        return score
    }

    private fun determineRoundScore(roundNumber: Int, score: Score): Score {
        if (roundNumber < goingDownFromRoundNumber) {
            if (score.maxValue() == HeartsRulesBook.allPointsForPit) {
                val newScore = Score()
                newScore.plusScorePerPlayer(Player.EAST, if (score.getEastValue() == 0) HeartsRulesBook.allPointsForPit else 0)
                newScore.plusScorePerPlayer(Player.WEST, if (score.getWestValue() == 0) HeartsRulesBook.allPointsForPit else 0)
                newScore.plusScorePerPlayer(Player.NORTH, if (score.getNorthValue() == 0) HeartsRulesBook.allPointsForPit else 0)
                newScore.plusScorePerPlayer(Player.SOUTH, if (score.getSouthValue() == 0) HeartsRulesBook.allPointsForPit else 0)
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

    private fun getScorePerRound(): List<Score> {
        return completedRoundList.mapIndexed { index, r ->  determineRoundScore(index, r.getScore())}
    }

    fun getCumulativeScorePerRound(): List<Score> {
        val list = getScorePerRound()
        list.forEachIndexed{ index, sc ->  sc.plus(if (index > 0) list[index-1] else Score()) }
        return list
    }
}

data class GameStatusAfterLastMove(
    val trickCompleted: Boolean, val trickWinner: Player?, val roundCompleted: Boolean, val gameFinished: Boolean)