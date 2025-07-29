package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank

object KLAVERJASSEN {

    const val NUMBER_OF_TRICKS_PER_ROUND = 8
    const val NUMBER_OF_ROUNDS_PER_GAME = 16

    fun cardValue(card: Card, trump: CardColor): Int {
        return when (card.rank) {
            CardRank.ACE -> 11
            CardRank.TEN -> 10
            CardRank.KING -> 4
            CardRank.QUEEN -> 3
            CardRank.JACK -> if (card.color == trump) 20 else 2
            CardRank.NINE -> if (card.color == trump) 14 else 0
            CardRank.EIGHT -> 0
            CardRank.SEVEN -> 0
            else -> 0
        }
    }

    fun toRankNumberNoTrump (card: Card) : Int {
        return when(card.rank) {
            CardRank.ACE -> 111
            CardRank.TEN -> 110
            CardRank.KING -> 104
            CardRank.QUEEN -> 103
            CardRank.JACK -> 102
            CardRank.NINE -> 9
            CardRank.EIGHT -> 8
            CardRank.SEVEN -> 7
            else -> 0
        }
    }

    fun toRankNumberTrump (card: Card) : Int {
        return when(card.rank) {
            CardRank.JACK -> 220
            CardRank.NINE -> 214
            CardRank.ACE -> 111
            CardRank.TEN -> 110
            CardRank.KING -> 104
            CardRank.QUEEN -> 103
            CardRank.EIGHT -> 8
            CardRank.SEVEN -> 7
            else -> 0
        }
    }

}