package com.cards.game.hearts

import com.cards.game.Player
import com.cards.game.card.Card

class Round(
    private var leadPlayer: Player,
    private val maxTricks: Int) {
    private val completedTrickList = arrayListOf<Trick>()

    private var trickOnTable = Trick(leadPlayer)

    fun playCard(card: Card) {
        trickOnTable.addCard(card)
        if (trickOnTable.isComplete()) {
            addTrick(trickOnTable)
            leadPlayer = trickOnTable.winner()
            trickOnTable = Trick(leadPlayer)
        }
    }

    private fun addTrick(trick: Trick) {
        if (!isComplete())
            completedTrickList.add(trick)
        else
            throw Exception("Trying to add more tricks to a Deal than the maximum ($maxTricks)")
    }

    fun isComplete(): Boolean = completedTrickList.size >= maxTricks
    fun isNew(): Boolean = completedTrickList.size == 0 && trickOnTable.isNew()
    fun getTrickOnTable() = trickOnTable
    fun getLastCompletedTrickWinner(): Player? = if (completedTrickList.size > 0) completedTrickList.last().winner() else null

    fun getScore(): Score {
        val score = Score()
        if (!isComplete()) {
            return score
        }
        completedTrickList.forEach { t -> score.plus(t.getScore())}
        return score
    }
}