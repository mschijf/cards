package com.cards.game.hearts

import com.cards.game.Player
import com.cards.game.card.Card

class Score {
    private val scorePerPlayer = Player.values().associateWith { p -> 0 }.toMutableMap()

    fun plusCardValueForPlayer(player: Player, card: Card) {
        plusScorePerPlayer(player, HeartsRulesBook.cardValue(card))
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