package com.cards.game

import kotlin.random.Random

object BASIC {
    private var seed = 0
    fun getShuffleRandomizer(): Random {
        seed = Random.nextInt(0, Int.MAX_VALUE)
        return Random(seed)
    }
    fun getLastSeedUsed() = seed
}