package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.GameStatusAfterLastMove
import com.cards.game.fourplayercardgame.Score
import kotlin.random.Random

abstract class Game(
    private val seed: Int = Random.Default.nextInt()) {

    private val completedRoundList = arrayListOf<Round>()

    val rules = getGameRules()
    private var leadPlayer = Player.WEST
    private var currentRound = Round(rules, leadPlayer)

    fun completeRoundsPlayed() = completedRoundList.size

    fun getSeed() = seed

    fun playCard(card: Card) {
        if (isFinished()) {
            throw Exception("Trying to play a card, but the game is already over")
        }

        currentRound.playCard(card)
        if (currentRound.isComplete()) {
            addRound(currentRound)
            leadPlayer = leadPlayer.nextPlayer()
            currentRound = Round(rules, leadPlayer)
            doGameSpecificActionsAfterCompletedRound()
        }
    }

    private fun addRound(round: Round) {
        if (!isFinished()) {
            completedRoundList.add(round)
        } else {
            throw Exception("Trying to add a round to a finished game")
        }
    }

    private fun getLastTrickWinner(): Player? {
        val lastTrick = if (!currentRound.isNew()) {
            currentRound.getLastCompletedTrick()
        } else {
            completedRoundList.last().getLastCompletedTrick()
        }
        return lastTrick?.getWinner()
    }

    fun getCurrentRound() = currentRound
    fun getPlayerToMove() = currentRound.getTrickOnTable().playerToMove()
    fun getStatusAfterLastMove() = GameStatusAfterLastMove(
        currentRound.getTrickOnTable().isNew(),
        getLastTrickWinner(),
        currentRound.isNew(),
        isFinished()
    )

    protected fun getTotalScore(): Score {
        val score = Score()
        completedRoundList.forEachIndexed { index, r -> score.plus(determineRoundScore(index, rules.getScoreForRound(r)))}
        return score
    }

    private fun getScorePerRound(): List<Score> {
        return completedRoundList.mapIndexed { index, r ->  determineRoundScore(index, rules.getScoreForRound(r))}
    }

    fun getCumulativeScorePerRound(): List<Score> {
        val list = getScorePerRound()
        list.forEachIndexed{ index, sc ->  sc.plus(if (index > 0) list[index-1] else Score()) }
        return list
    }

    protected abstract fun getGameRules(): GameRules
    protected abstract fun doGameSpecificActionsAfterCompletedRound()
    protected abstract fun isFinished(): Boolean
    protected abstract fun determineRoundScore(roundNumber: Int, score: Score): Score
}

