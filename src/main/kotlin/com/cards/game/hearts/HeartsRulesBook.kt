package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank

object HeartsRulesBook {
    val valueToGoDown = 15
    val allPointsForPit = 15
    val valueToFinish = 0

    fun toRankNumber (card: Card) : Int = card.rank.rankNumber

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