package com.cards.game.hearts

class Deal() {
    private val maxTricks = 8  //todo: get dynamic number of maxTricks
    private val completedTrickList = arrayListOf<Trick>()

    fun addTrick(trick: Trick) {
        if (!isComplete())
            completedTrickList.add(trick)
        else
            throw Exception("Trying to add more tricks to a Deal than the maximum ($maxTricks)")
    }

    fun isComplete(): Boolean = completedTrickList.size >= maxTricks
}