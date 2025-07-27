package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.TablePosition

class Round(
    private val game: Game,
    private val leadPlayer: CardPlayer,
) {

    private val completedTrickList = arrayListOf<Trick>()
    private var currentTrick = Trick(game, leadPlayer)

    fun getLeadPLayer() = leadPlayer
    fun completedTricksPlayed() = completedTrickList.size
    fun isNew(): Boolean = completedTrickList.isEmpty() && currentTrick.isNew()
    fun getTrickOnTable() = currentTrick
    fun getLastCompletedTrickWinner(): CardPlayer? = completedTrickList.lastOrNull()?.winner()
    fun getCompletedTrickList() = completedTrickList

    fun isComplete(): Boolean = game.roundIsComplete(this)

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
}