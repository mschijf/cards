package com.cards.game.fourplayercardgame

import com.cards.game.card.Card

class Round(
    private val gameRules: GameRules,
    leadPlayer: Player) {

    private val completedTrickList = arrayListOf<Trick>()

    private var currentTrick = Trick(gameRules, leadPlayer)

    fun playCard(card: Card) {
        currentTrick.addCard(card)
        if (currentTrick.isComplete()) {
            val winner = currentTrick.winner()!!
            addTrick(currentTrick)
            currentTrick = Trick(gameRules, winner)
        }
    }

    private fun addTrick(trick: Trick) {
        if (!isComplete())
            completedTrickList.add(trick)
        else
            throw Exception("Trying to add more tricks to a Deal than the maximum allowed")
    }

    fun completedTricksPlayed() = completedTrickList.size
    fun isComplete(): Boolean = gameRules.roundIsComplete(this)
    fun isNew(): Boolean = completedTrickList.size == 0 && currentTrick.isNew()
    fun getTrickOnTable() = currentTrick
    fun getLastCompletedTrickWinner(): Player? = if (completedTrickList.isNotEmpty()) completedTrickList.last().winner() else null
    fun getCompletedTrickList() = completedTrickList
}