package com.cards.game.card

class CardDeck {
    private val deck = CardColor.values().flatMap{clr -> CardRank.values().map{rnk -> Card(clr, rnk)}}.toMutableList()

    fun shuffle() {
        deck.shuffle()
    }

    fun numberOfCards() = deck.size
    fun getCards(from: Int, numberOfCards: Int): List<Card> = deck.subList(from, from + numberOfCards)
}