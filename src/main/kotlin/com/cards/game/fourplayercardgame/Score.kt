package com.cards.game.fourplayercardgame

import com.cards.game.fourplayercardgame.basic.TablePosition

class Score() {
    private val scorePerPlayer = TablePosition.values().associateWith { p -> 0 }.toMutableMap()

    fun plus(score: Score) {
        score.scorePerPlayer.forEach { spp -> this.plusScorePerPlayer(spp.key, spp.value) }
    }

    fun plusScorePerPlayer(player: TablePosition, score: Int) {
        scorePerPlayer[player] = scorePerPlayer[player]!! + score
    }

    fun maxValue(): Int {
        return scorePerPlayer.maxOf { spp -> spp.value }
    }

    fun minValue(): Int {
        return scorePerPlayer.minOf { spp -> spp.value }
    }

    fun getSouthValue(): Int = scorePerPlayer[TablePosition.SOUTH]!!
    fun getNorthValue(): Int = scorePerPlayer[TablePosition.NORTH]!!
    fun getWestValue(): Int = scorePerPlayer[TablePosition.WEST]!!
    fun getEastValue(): Int = scorePerPlayer[TablePosition.EAST]!!

}