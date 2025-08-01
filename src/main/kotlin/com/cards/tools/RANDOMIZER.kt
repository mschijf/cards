package com.cards.tools

import kotlin.random.Random

object RANDOMIZER {
    private var seedList = mutableListOf<Int>()
    fun getShuffleRandomizer(): Random {
        seedList.add(Random.nextInt(0, Int.MAX_VALUE))
        return kotlin.random.Random(seedList.last())
    }
    fun getLastSeedUsed() = seedList.last()
}