package com.cards.game.card

data class Card(val color: CardColor, val rank: CardRank) {
    override fun toString(): String {
        return rank.rankString + color.colorString
    }

    companion object {
        fun of(cardString: String): Card {
            if (cardString.length != 2 && cardString.length != 3)
                throw IllegalArgumentException("The cardString must have exactly 2 or 3 characters")
            val cardRank = CardRank.values().firstOrNull { it.rankString == cardString.dropLast(1) }
            val cardColor = CardColor.values().firstOrNull { it.colorString == cardString.takeLast(1) }
            if (cardRank == null || cardColor == null)
                throw IllegalArgumentException("Illegal card string")
            return Card(cardColor, cardRank)
        }
    }

}