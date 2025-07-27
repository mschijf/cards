package com.cards.game.fourplayercardgame.basic

enum class TablePosition {
    SOUTH,
    WEST,
    NORTH,
    EAST;

    fun neighbour() = when(this) {
        SOUTH -> WEST
        WEST -> NORTH
        NORTH -> EAST
        EAST -> SOUTH
    }
}