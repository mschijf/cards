package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.fourplayercardgame.basic.TablePosition

data class ScoreKlaverjassen(val eastWestPoints: Int, val northSouthPoints: Int,
                             val eastWestBonus: Int, val northSouthBonus: Int,
                             val scoreType: ScoreType = ScoreType.REGULAR) {

    fun plus(score: ScoreKlaverjassen): ScoreKlaverjassen {
        return ScoreKlaverjassen(
            eastWestPoints + score.eastWestPoints,
            northSouthPoints + score.northSouthPoints,
            eastWestBonus + score.eastWestBonus,
            northSouthBonus + score.northSouthBonus,
        )
    }

    fun plusPitBonus(): ScoreKlaverjassen {
        return ScoreKlaverjassen(
            eastWestPoints,
            northSouthPoints,
            if (eastWestPoints == 0) eastWestBonus else eastWestBonus + PIT_BONUS ,
            if (northSouthPoints == 0) northSouthBonus else northSouthBonus + PIT_BONUS,
            scoreType = ScoreType.PIT
        )
    }

    fun changeEastWestToNat(): ScoreKlaverjassen {
        return ScoreKlaverjassen(
            0,
            northSouthPoints+eastWestPoints,
            0,
            northSouthBonus+eastWestBonus,
            scoreType = ScoreType.NAT
        )
    }

    fun changeNorthSouthToNat(): ScoreKlaverjassen {
        return ScoreKlaverjassen(
            eastWestPoints+northSouthPoints,
            0,
            eastWestBonus+northSouthBonus,
            0,
            scoreType = ScoreType.NAT
        )
    }


    fun getNorthSouthTotal() = northSouthPoints + northSouthBonus
    fun getEastWestTotal() = eastWestPoints + eastWestBonus

    fun getPointsForPLayer(tablePosition: TablePosition) = if (tablePosition in setOf(TablePosition.WEST, TablePosition.EAST)) eastWestPoints else northSouthPoints
    fun getBonusForPlayer(tablePosition: TablePosition) = if (tablePosition in setOf(TablePosition.WEST, TablePosition.EAST)) eastWestBonus else  northSouthBonus
    fun getTotalForPlayer(tablePosition: TablePosition) = getPointsForPLayer(tablePosition) + getBonusForPlayer(tablePosition)

    fun getDeltaForPlayer(tablePosition: TablePosition) = getTotalForPlayer(tablePosition) - getTotalForPlayer(tablePosition.clockwiseNext())

    companion object {
        val ZERO = ScoreKlaverjassen(0,0,0,0)

        fun scoreForPlayer(tablePosition: TablePosition, value: Int, bonus: Int): ScoreKlaverjassen {
            return ScoreKlaverjassen(
                if (tablePosition in setOf(TablePosition.WEST, TablePosition.EAST)) value else 0,
                if (tablePosition in setOf(TablePosition.NORTH, TablePosition.SOUTH)) value else 0,
                if (tablePosition in setOf(TablePosition.WEST, TablePosition.EAST)) bonus else 0,
                if (tablePosition in setOf(TablePosition.NORTH, TablePosition.SOUTH)) bonus else 0,
            )
        }
    }
}

enum class ScoreType {
    REGULAR, NAT, PIT
}