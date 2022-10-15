package com.cards.game.card

data class Card(val color: CardColor, val rank: CardRank) {
    override fun toString(): String {
        return rank.rankString + color.colorString
    }
}