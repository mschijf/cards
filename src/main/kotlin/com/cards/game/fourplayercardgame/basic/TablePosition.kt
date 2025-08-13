package com.cards.game.fourplayercardgame.basic

enum class TablePosition {
    WEST,
    NORTH,
    EAST,
    SOUTH;

    fun clockwiseNext(n: Int=1) = values()[(this.ordinal + n) % values().size]
    fun opposite(): TablePosition = clockwiseNext(2)
    fun isOppositeOf(other: TablePosition?) = this.opposite() == other
}