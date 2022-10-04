package com.cards.game.hearts

import com.cards.game.card.Card

class Game {

    private var leadPlayer = Player.SOUTH
    var trickOnTable = Trick(leadPlayer)
    private var currentDeal = Deal()
    private val completedDealList = arrayListOf<Deal>()

    fun playCard(card: Card) {
        trickOnTable.addCard(card)
        if (trickOnTable.isComplete()) {
            currentDeal.addTrick(trickOnTable)
            leadPlayer = trickOnTable.winner()
            trickOnTable = Trick(leadPlayer)

            if (currentDeal.isComplete()) {
                addDeal(currentDeal)
                currentDeal = Deal()
            }
        }
    }

    private fun addDeal(deal: Deal) {
        if (!isFinished())
            completedDealList.add(deal)
        throw Exception("Trying to add a deals to a finsihed game")
    }

    private fun isFinished(): Boolean = completedDealList.size == 4 //of course, not according to hearts rules.
}