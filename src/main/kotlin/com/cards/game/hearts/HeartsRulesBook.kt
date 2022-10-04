package com.cards.game.hearts

import com.cards.game.card.Card

object HeartsRulesBook {
    fun toRankNumber (card: Card) : Int = card.rank.rankNumber
}