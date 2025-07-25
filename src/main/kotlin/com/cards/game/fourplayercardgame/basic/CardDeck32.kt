package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import kotlin.random.Random

class CardDeck32 {

    private val baseDeck = CardColor.values()
        .flatMap{ clr ->
            CardRank.values()
                .filter { rnk -> rnk.rankNumber >= 7 }
                .map{ rnk -> Card(clr, rnk) }
        }

    private var deck = baseDeck

    fun shuffle(seed: Int) {
        deck = baseDeck.shuffled(Random(seed))
    }

    fun getAllCards() = deck
    fun getCards(from: Int, numberOfCards: Int) = deck.subList(from, from + numberOfCards)
    fun getCards(color: CardColor)= deck.filter { card -> card.color == color}

    companion object {
        private val cardDeck32 = CardDeck32()
        fun getCardDeck() = cardDeck32
    }

}