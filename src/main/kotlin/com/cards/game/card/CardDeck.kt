package com.cards.game.card

import kotlin.random.Random

class CardDeck {
    private val baseDeck = CardColor.values().flatMap{clr -> CardRank.values().map{rnk -> Card(clr, rnk)}}

    private var deck = baseDeck

    fun shuffle(seed: Int) {
        deck = baseDeck.shuffled(Random(seed))
    }

    fun numberOfCards() = deck.size
    fun getCards() = deck.toList()
    fun getCards(from: Int, numberOfCards: Int) = deck.subList(from, from + numberOfCards)
    fun getCards(color: CardColor)= deck.filter { card -> card.color == color}
}