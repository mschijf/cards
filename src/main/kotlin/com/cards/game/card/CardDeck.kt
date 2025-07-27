package com.cards.game.card

class CardDeck {
    private val baseDeck = CardColor.values().flatMap{clr -> CardRank.values().map{rnk -> Card(clr, rnk)}}

    private var deck = baseDeck

    fun shuffle() {
        deck = baseDeck.shuffled()
    }

    fun numberOfCards() = deck.size
    fun getCards() = deck.toList()
    fun getCards(from: Int, numberOfCards: Int) = deck.subList(from, from + numberOfCards)
    fun getCards(color: CardColor)= deck.filter { card -> card.color == color}
}