package com.cards.game.fourplayercardgame

import com.cards.game.card.Card

abstract class Game() {

    abstract fun getGameRules(): GameRules

    val rules = getGameRules()

    private var leadPlayer = Player.WEST

    private val completedRoundList = arrayListOf<Round>()
    private var currentRound = Round(rules, leadPlayer)

    fun completeRoundsPlayed() = completedRoundList

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

    abstract fun doGameSpecificActionsAfterCompletedRound()

    private fun addRound(round: Round) {
        if (!isFinished()) {
            completedRoundList.add(round)
        } else {
            throw Exception("Trying to add a round to a finished game")
        }
    }

    protected abstract fun isFinished(): Boolean

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

    protected fun getTotalScore(): Score {
        return getCumulativeScorePerRound().lastOrNull()?: Score()
    }

    private fun getScorePerRound(): List<Score> {
        return completedRoundList.mapIndexed { index, round ->  rules.getScoreForRound(this, round)}
    }

    fun getCumulativeScorePerRound(): List<Score> {
        val list = getScorePerRound()
        list.forEachIndexed{ index, sc ->  sc.plus(if (index > 0) list[index-1] else Score()) }
        return list
    }

}

