package com.cards.game.hearts

class Game {
    private val completedDealList = arrayListOf<Deal>()

    fun addDeal(deal: Deal) {
        if (!isFinished())
            completedDealList.add(deal)
        throw Exception("Trying to add a deals to a finsihed game")
    }

    fun isFinished(): Boolean = completedDealList.size == 4 //of course, not according to hearts rules.
}