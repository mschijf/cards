package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor

object HeartsRulesBook {
    fun toRankNumber (card: Card) : Int = card.rank.rankNumber

    fun legalPlayableCards (cardList: List<Card>, leadColor: CardColor?) : List<Card> {
        if (leadColor == null)
            return cardList

        return cardList.filter{ c -> c.color == leadColor}.ifEmpty { cardList }
    }
}