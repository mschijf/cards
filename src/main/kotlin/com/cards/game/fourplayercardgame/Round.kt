package com.cards.game.fourplayercardgame

import com.cards.game.card.Card

class Round(
    private val game: Game,
    private val leadPlayer: Player) {

    private val completedTrickList = arrayListOf<Trick>()

    private var currentTrick = Trick(game, leadPlayer)

    fun playCard(card: Card) {
        currentTrick.addCard(card)
        if (currentTrick.isComplete()) {
            val winner = currentTrick.winner()!!
            addTrick(currentTrick)
            currentTrick = Trick(game, winner)
        }
    }

    private fun addTrick(trick: Trick) {
        if (!isComplete())
            completedTrickList.add(trick)
        else
            throw Exception("Trying to add more tricks to a Deal than the maximum allowed")
    }

    fun getLeadPLayer() = leadPlayer
    fun completedTricksPlayed() = completedTrickList.size
    fun isComplete(): Boolean = game.roundIsComplete(this)
    fun isNew(): Boolean = completedTrickList.isEmpty() && currentTrick.isNew()
    fun getTrickOnTable() = currentTrick
    fun getLastCompletedTrickWinner(): Player? = if (completedTrickList.isNotEmpty()) completedTrickList.last().winner() else null
    fun getCompletedTrickList() = completedTrickList
}