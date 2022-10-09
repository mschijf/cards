package com.cards.game.hearts

import com.cards.game.Player
import com.cards.game.card.Card

class Game (
    private var leadPlayer: Player,
    private val maxTricksPerRound: Int) {
    private val completedRoundList = arrayListOf<Round>()
    private var currentRound = Round(leadPlayer, maxTricksPerRound)

    fun playCard(card: Card) {
        currentRound.playCard(card)
        if (currentRound.isComplete()) {
            addRound(currentRound)
            currentRound = Round(leadPlayer.nextPlayer(), maxTricksPerRound)
        }
    }

    private fun addRound(round: Round) {
        if (!isFinished()) {
            completedRoundList.add(round)
        } else {
            throw Exception("Trying to add a deals to a finished game")
        }
    }

    private fun isFinished(): Boolean = completedRoundList.size == 4 //todo: check on 60 and then go down

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
        currentRound.isNew()
        )
}

data class GameStatusAfterLastMove(val trickCompleted: Boolean, val trickWinner: Player?, val roundCompleted: Boolean)