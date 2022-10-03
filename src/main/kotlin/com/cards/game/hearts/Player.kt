package com.cards.game.hearts

enum class Player(val index: Int) {
    SOUTH(0), WEST(1), NORTH(2), EAST(3);
    fun nextPlayer() = values()[this.index + 1]
}