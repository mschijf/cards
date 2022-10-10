package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.Player

class Round(
    leadPlayer: Player,
    private val maxTricks: Int) {
    private val completedTrickList = arrayListOf<Trick>()

    private var currentTrick = Trick(leadPlayer)

    fun playCard(card: Card) {
        currentTrick.addCard(card)
        if (currentTrick.isComplete()) {
            val winner = currentTrick.winner()!!
            addTrick(currentTrick)
            currentTrick = Trick(winner)
        }
    }

    private fun addTrick(trick: Trick) {
        if (!isComplete())
            completedTrickList.add(trick)
        else
            throw Exception("Trying to add more tricks to a Deal than the maximum ($maxTricks)")
    }

    fun isComplete(): Boolean = completedTrickList.size >= maxTricks
    fun isNew(): Boolean = completedTrickList.size == 0 && currentTrick.isNew()
    fun getTrickOnTable() = currentTrick
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