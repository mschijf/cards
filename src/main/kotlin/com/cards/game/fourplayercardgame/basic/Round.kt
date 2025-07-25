package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card

class Round(
    private val gameRules: GameRules,
    leadPlayer: Player) {

    private val completedTrickList = arrayListOf<Trick>()
    private var currentTrick = Trick(gameRules, leadPlayer)

    fun completedTricksPlayed() = completedTrickList.size
    fun isNew(): Boolean = completedTrickList.isEmpty() && currentTrick.isNew()
    fun getTrickOnTable() = currentTrick
    fun getCompletedTrickList() = completedTrickList
    fun getLastCompletedTrick() = completedTrickList.lastOrNull()

    fun isComplete(): Boolean = gameRules.roundIsComplete(this)


    private fun addTrick(trick: Trick) {
        if (!isComplete())
            completedTrickList.add(trick)
        else
            throw Exception("Trying to add more tricks to a Deal than the maximum allowed")
    }

    fun playCard(card: Card) {
        currentTrick.addCard(card)
        if (currentTrick.isComplete()) {
            val winner = gameRules.winnerForTrick(currentTrick)!!
            addTrick(currentTrick)
            currentTrick = Trick(gameRules, winner)
        }
    }
}