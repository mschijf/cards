package com.cards.game.hearts

import com.cards.game.Player
import com.cards.game.card.Card

class Game (
    private var leadPlayer: Player
) {
    private val completedRoundList = arrayListOf<Round>()
    private var currentRound = Round(leadPlayer)

    fun playCard(card: Card) {
        currentRound.playCard(card)
        if (currentRound.isComplete()) {
            addRound(currentRound)
            currentRound = Round(leadPlayer.nextPlayer())
        }
    }

    private fun addRound(round: Round) {
        if (!isFinished()) {
            completedRoundList.add(round)
        } else {
            throw Exception("Trying to add a deals to a finsihed game")
        }
    }

    private fun isFinished(): Boolean = completedRoundList.size == 4 //todo: check on 60 and thenn go down
    fun getCurrentRound() = currentRound
}