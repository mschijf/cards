package com.cards.game.hearts

class Deal(val maxTricks: Int) {
    private val completedTrickList = arrayListOf<Trick>()

    fun addTrick(trick: Trick) {
        if (!isComplete())
            completedTrickList.add(trick)
        throw Exception("Trying to add more tricks to a Deal than the maximum ($maxTricks)")
    }

    fun isComplete(): Boolean = completedTrickList.size >= maxTricks
}