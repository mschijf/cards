package com.cards.tools

import kotlin.random.Random

object RANDOMIZER {
    private var fixedSeed : Int? = null
    private var lastSeedUsed = 0
    private var fixedSequence = true

    fun setSeed(seed: Int) {
        fixedSeed = seed
    }
    fun unsetSeed() {
        fixedSeed = null
    }

    fun getShuffleRandomizer(): Random {
        lastSeedUsed =  if (fixedSequence) {
            Random(lastSeedUsed).nextInt(0, Int.MAX_VALUE)
        } else {
            if (fixedSeed != null) fixedSeed!! else Random.nextInt(0, Int.MAX_VALUE)
        }
        return kotlin.random.Random(lastSeedUsed)
    }

    fun getLastSeedUsed() = lastSeedUsed
}