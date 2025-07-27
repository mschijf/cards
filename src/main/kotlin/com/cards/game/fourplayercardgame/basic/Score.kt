package com.cards.game.fourplayercardgame.basic

import com.github.jknack.handlebars.internal.lang3.math.NumberUtils

data class Score(val westValue: Int, val northValue: Int, val eastValue: Int, val southValue: Int) {
    fun plus(score: Score): Score {
        return Score(
            westValue + score.westValue,
            northValue + score.northValue,
            eastValue + score.eastValue,
            southValue + score.southValue
        )
    }

    fun plusForPlayer(player: CardPlayer, value: Int): Score {
        return this.plus(scoreForPlayer(player, value))
    }

    fun minValue() = NumberUtils.min(westValue, northValue, eastValue, southValue)
    fun maxValue() = NumberUtils.max(westValue, northValue, eastValue, southValue)

    companion object {
        val ZERO = Score(0,0,0,0)

        fun scoreForPlayer(player: CardPlayer, value: Int): Score {
            return Score(
                if (player.tablePosition == TablePosition.WEST) value else 0,
                if (player.tablePosition == TablePosition.NORTH) value else 0,
                if (player.tablePosition == TablePosition.EAST) value else 0,
                if (player.tablePosition == TablePosition.SOUTH) value else 0
            )
        }
    }
}