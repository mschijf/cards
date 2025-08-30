package com.cards.game.hearts

import com.cards.game.card.Card
import kotlin.math.max

class Game() {

    private val roundList = mutableListOf<Round>()

    fun start(startSide: TableSide ) {
        createNewRoundAndTrick(startSide)
    }

    fun getLastTrickWinner(): TableSide?  =
        if (getCurrentRound().hasNotStarted())
            getPreviousRound()?.getLastCompletedTrickWinner()
        else
            getCurrentRound().getLastCompletedTrickWinner()

    fun getRounds() = roundList.toList()
    fun getCurrentRound() = roundList.lastOrNull()?:throw Exception("We do not have a current round")
    fun getPreviousRound() = if (roundList.size >= 2) roundList[roundList.size - 2] else null
    fun getSideToMove() = getCurrentRound().getTrickOnTable().getSideToPlay()

    private fun createNewRoundAndTrick(sideToLead: TableSide) {
        if (isFinished())
            throw Exception("Trying to add a round to a finished game")
        roundList.add(Round())
        createNewTrick(sideToLead)
    }

    private fun createNewTrick(sideToLead: TableSide) {
        getCurrentRound().addTrick(Trick(sideToLead))
    }

    fun playCard(card: Card): GameStatus {
        if (isFinished())
            throw Exception("Trying to play a card, but the game is already over")

        val currentRound = getCurrentRound()
        val trickOnTable = currentRound.getTrickOnTable()
        trickOnTable.addCard(card)

        val gameStatus = if (isFinished()) {
            GameStatus(gameFinished = true, roundFinished = true, trickFinished = true)
        } else if (currentRound.isComplete()) {
            val previousLeadStart = currentRound.getTrickList().first().getSideToLead()
            createNewRoundAndTrick(previousLeadStart.clockwiseNext())
            GameStatus(gameFinished = false, roundFinished = true, trickFinished = true)
        } else if (trickOnTable.isComplete()) {
            createNewTrick(trickOnTable.getWinningSide()!!)
            GameStatus(gameFinished = false, roundFinished = false, trickFinished = true)
        } else {
            GameStatus(gameFinished = false, roundFinished = false, trickFinished = false)
        }
        return gameStatus
    }

    fun isGoingUp() = getRounds().size < goingDownFromRoundNumber()

    fun isFinished() = !isGoingUp() && (getTotalScore().minValue() <= VALUE_TO_FINISH)

    fun getCumulativeScorePerRound(): List<ScoreHearts> {
        return getRounds()
            .filter { it.isComplete() }
            .map { round ->  getGameScoreForRound(round)}
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
        getRounds().forEachIndexed { idx, round ->
            score = score.plus(round.getScore())
            if (score.maxValue() >= VALUE_TO_GO_DOWN) {
                goingDownRoundNumber = idx+1
                return idx + 1
            }
        }
        return Int.MAX_VALUE
    }

    private fun getGameScoreForRound(round: Round): ScoreHearts {
        val score = round.getScore()
        val roundNumber = max(0, getRounds().indexOf(round))

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

    companion object {
        fun startNewGame(startSide: TableSide = GAME_START_PLAYER): Game {
            val game = Game()
            game.start(startSide)
            return game
        }
    }

}





