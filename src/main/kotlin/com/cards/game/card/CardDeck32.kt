package com.cards.game.card

class CardDeck32 {

    private var deck = baseDeck

    fun shuffle() {
        deck = baseDeck.shuffled()
    }

    fun numberOfCards() = deck.size
    fun getCards() = deck.toList()

    companion object {
        private val baseDeck = CardColor.values()
            .flatMap{color ->
                CardRank.values()
                    .filter{cardRank -> cardRank.rankNumber >= 7}
                    .map{rank -> Card(color, rank)}
            }

        fun getBaseDeckCards() = baseDeck
    }
}