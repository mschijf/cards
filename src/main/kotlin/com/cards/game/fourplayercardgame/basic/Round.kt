package com.cards.game.fourplayercardgame.basic

abstract class Round() {

    private val trickList = mutableListOf<Trick>()

    abstract fun isComplete(): Boolean

    private fun getLastTrick() = trickList.lastOrNull()?:throw Exception("We do not have a last trick")
    fun hasNotStarted(): Boolean = trickList.size == 1 && trickList.first().hasNotStarted()
    fun getTrickOnTable() = if (getLastTrick().isActive()) getLastTrick() else throw Exception("We do not have a current trick on table")
    fun getLastCompletedTrickWinner(): TablePosition? = getLastCompletedTrick()?.getWinner()
    fun getTrickList() = trickList.toList()

    private fun getLastCompletedTrick(): Trick? {
        if (trickList.isEmpty())
            return null
        if (!trickList.last().isActive())
            return trickList.last()
        if (trickList.size <= 1)
            return null
        return trickList[trickList.size - 2]
    }

    fun addTrick(trick: Trick) {
        if (isComplete())
            throw Exception("Trying to add more tricks to a round than the maximum allowed")
        trickList.add(trick)
    }
}