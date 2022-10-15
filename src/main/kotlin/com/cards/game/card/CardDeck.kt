package com.cards.game.card

class CardDeck {
    private val deck = CardColor.values().flatMap{clr -> CardRank.values().map{rnk -> Card(clr, rnk)}}.toMutableList()

    fun shuffle() {
        deck.shuffle()
    }

    fun numberOfCards() = deck.size
    fun getCards() = deck.toList()
    fun getCards(from: Int, numberOfCards: Int) = deck.subList(from, from + numberOfCards)
    fun getCards(color: CardColor)= deck.filter { card -> card.color == color}
}