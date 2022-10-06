package com.cards.game.hearts

import com.cards.game.Player
import com.cards.game.card.Card

class Round(
    private var leadPlayer: Player
) {
    private val maxTricks = 8  //todo: get dynamic number of maxTricks
    private val completedTrickList = arrayListOf<Trick>()

    private var trickOnTable = Trick(leadPlayer)

    fun playCard(card: Card): Player? {
        trickOnTable.addCard(card)
        if (trickOnTable.isComplete()) {
            addTrick(trickOnTable)

            leadPlayer = trickOnTable.winner()
            trickOnTable = Trick(leadPlayer)
            return leadPlayer
        }
        return null
    }

    private fun addTrick(trick: Trick) {
        if (!isComplete())
            completedTrickList.add(trick)
        else
            throw Exception("Trying to add more tricks to a Deal than the maximum ($maxTricks)")
    }

    fun isComplete(): Boolean = completedTrickList.size >= maxTricks
    fun isNew(): Boolean = completedTrickList.size == 0
    fun getTrickOnTable() = trickOnTable
}