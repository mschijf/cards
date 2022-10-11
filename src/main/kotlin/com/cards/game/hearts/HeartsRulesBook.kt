package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardDeck
import com.cards.game.card.CardRank

object HeartsRulesBook {
    val valueToGoDown = 15
    val allPointsForPit = 60
    val valueToFinish = 0
    val cardDeck = CardDeck()

    fun toRankNumber (card: Card) : Int = card.rank.rankNumber

    fun higherCardsThen(card: Card): List<Card> {
        return cardDeck
            .getCards(card.color)
            .filter { crd -> toRankNumber(crd) > toRankNumber(card) }
    }


    fun legalPlayableCards (cardList: List<Card>, leadColor: CardColor?) : List<Card> {
        if (leadColor == null)
            return cardList

        return cardList.filter{ c -> c.color == leadColor}.ifEmpty { cardList }
    }

    fun cardValue(card: Card): Int {
        return if (card.color == CardColor.HEARTS) {
            1
        } else if (card.color == CardColor.SPADES) {
            if (card.rank == CardRank.QUEEN) 5 else 0
        } else if (card.color == CardColor.CLUBS) {
            if (card.rank == CardRank.JACK) 2 else 0
        } else {
            0
        }
    }
}