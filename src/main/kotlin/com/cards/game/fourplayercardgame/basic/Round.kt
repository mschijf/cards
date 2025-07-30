package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card

abstract class Round(
    private val leadPlayer: Player) {

    private val completedTrickList = mutableListOf<Trick>()
    private var currentTrick: Trick = createTrick(leadPlayer)

    abstract fun createTrick(leadPlayer: Player): Trick
    abstract fun isComplete(): Boolean

    fun getLeadPlayer() = leadPlayer
    fun completedTricksPlayed() = completedTrickList.size
    fun hasNotStarted(): Boolean = completedTrickList.isEmpty() && currentTrick.hasNotStarted()
    fun getTrickOnTable() = currentTrick
    fun getLastCompletedTrickWinner(): Player? = completedTrickList.lastOrNull()?.winner()
    fun getCompletedTrickList() = completedTrickList.toList()

    fun playCard(card: Card) {
        currentTrick.addCard(card)
        if (currentTrick.isComplete()) {
            val winner = currentTrick.winner()!!
            addTrick(currentTrick)
            currentTrick = createTrick(winner)
        }
    }

    private fun addTrick(trick: Trick) {
        if (!isComplete())
            completedTrickList.add(trick)
        else
            throw Exception("Trying to add more tricks to a Deal than the maximum allowed")
    }
}