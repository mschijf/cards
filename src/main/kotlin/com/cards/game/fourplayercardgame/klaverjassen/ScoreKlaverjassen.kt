package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Table

data class ScoreKlaverjassen(val westValue: Int, val northValue: Int, val eastValue: Int, val southValue: Int,
                             val westBonus: Int, val northBonus: Int, val eastBonus: Int, val southBonus: Int,
                             val scoreType: ScoreType = ScoreType.REGULAR) {

    fun plus(score: ScoreKlaverjassen): ScoreKlaverjassen {
        return ScoreKlaverjassen(
            westValue + score.westValue,
            northValue + score.northValue,
            eastValue + score.eastValue,
            southValue + score.southValue,
            westBonus + score.westBonus,
            northBonus + score.northBonus,
            eastBonus + score.eastBonus,
            southBonus + score.southBonus
        )
    }

    fun getNorthSouthPoints() = northValue + southValue
    fun getEastWestPoints() = eastValue + westValue
    fun getNorthSouthBonus() = northBonus + southBonus
    fun getEastWestBonus() = eastBonus + westBonus

    fun getNorthSouthTotal() = getNorthSouthPoints() + getNorthSouthBonus()
    fun getEastWestTotal() = getEastWestPoints() + getEastWestBonus()

    companion object {
        val ZERO = ScoreKlaverjassen(0,0,0,0, 0,0,0,0)

        fun scoreForPlayer(player: Player, value: Int, bonus: Int): ScoreKlaverjassen {
            return ScoreKlaverjassen(
                if (player.tablePosition == Table.WEST) value else 0,
                if (player.tablePosition == Table.NORTH) value else 0,
                if (player.tablePosition == Table.EAST) value else 0,
                if (player.tablePosition == Table.SOUTH) value else 0,
                if (player.tablePosition == Table.WEST) bonus else 0,
                if (player.tablePosition == Table.NORTH) bonus else 0,
                if (player.tablePosition == Table.EAST) bonus else 0,
                if (player.tablePosition == Table.SOUTH) bonus else 0,
            )
        }
    }
}

enum class ScoreType {
    REGULAR, NAT, PIT
}