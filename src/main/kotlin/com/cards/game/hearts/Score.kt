package com.cards.game.hearts

import com.cards.game.fourplayercardgame.Player

class Score() {
    private val scorePerPlayer = Player.values().associateWith { p -> 0 }.toMutableMap()

    constructor(trick: Trick) : this() {
        if (trick.isComplete()) {
            val winner = trick.winner()
            Player.values().forEach { p ->  plusScorePerPlayer(winner!!, HeartsRulesBook.cardValue(trick.getCardPlayedBy(p)!!)) }
        }
    }

    fun plus(score: Score) {
        score.scorePerPlayer.forEach { spp -> this.plusScorePerPlayer(spp.key, spp.value) }
    }

    fun plusScorePerPlayer(player: Player, score: Int) {
        scorePerPlayer[player] = scorePerPlayer[player]!! + score
    }

    fun maxValue(): Int {
        return scorePerPlayer.maxOf { spp -> spp.value }
    }

    fun minValue(): Int {
        return scorePerPlayer.minOf { spp -> spp.value }
    }

    fun getSouthValue(): Int = scorePerPlayer[Player.SOUTH]!!
    fun getNorthValue(): Int = scorePerPlayer[Player.NORTH]!!
    fun getWestValue(): Int = scorePerPlayer[Player.WEST]!!
    fun getEastValue(): Int = scorePerPlayer[Player.EAST]!!

}