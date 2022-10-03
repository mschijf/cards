package com.cards.game.hearts

import com.cards.game.card.Card

object HeartsRulesBook {
    fun firstCardBeatsSecondCard(first: Card, second: Card) : Boolean {
        if (first.color != second.color)
            return true
        if (first.rank > second.rank)
            return true
        return false
    }
}