package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.Table

object HEARTS {
    const val NUMBER_OF_TRICKS_PER_ROUND = 8

    const val ALL_POINTS_FOR_PIT = 15
    const val VALUE_TO_GO_DOWN = 14
    const val VALUE_TO_FINISH = 0

    val VERY_FIRST_START_PLAYER = Table.WEST

    fun cardValue(card: Card): Int {
        return when (card.color) {
            CardColor.HEARTS -> 1
            CardColor.CLUBS -> if (card.rank == CardRank.JACK) 2 else 0
            CardColor.SPADES -> if (card.rank == CardRank.QUEEN) 5 else 0
            CardColor.DIAMONDS -> 0
        }
    }

    fun toRankNumber (card: Card) : Int = card.rank.rankNumber - 7
}