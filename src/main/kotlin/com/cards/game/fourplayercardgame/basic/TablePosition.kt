package com.cards.game.fourplayercardgame.basic

enum class TablePosition {
    WEST,
    NORTH,
    EAST,
    SOUTH;

    fun clockwiseNext() : TablePosition =
        when (this) {
            WEST -> NORTH
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
        }

    fun opposite(): TablePosition = clockwiseNext().clockwiseNext()

    fun isOppositeOf(other: TablePosition?) = this.opposite() == other
}