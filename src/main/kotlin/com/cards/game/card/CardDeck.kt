package com.cards.game.card

class CardDeck(lowestCardRank: CardRank) {
    val deck: ArrayList<Card>

    init {
        val tmpDeck = arrayListOf<Card>()
        for (cardColor in CardColor.values()) {
            for (cardRank in CardRank.values()) {
                if (cardRank.higherOrEqual(lowestCardRank)) {
                    tmpDeck.add(Card(cardColor, cardRank))
                }
            }
        }
        deck = tmpDeck
    }

    fun shuffle(): CardDeck {
        for (i in 1..1000) {
            val posA = (0 until deck.size).random()
            val posB = (0 until deck.size).random()
            swapCards(posA, posB)
        }
        return this
    }

    private fun swapCards(posA: Int, posB: Int) {
        val tmp = deck[posA]
        deck[posA] = deck[posB]
        deck[posB] = tmp
    }

    fun getCards(from: Int, to: Int): ArrayList<Card> {
        val result = arrayListOf<Card>()
        for (i in from until to)
            result.add(deck[i])
        return result
    }
}