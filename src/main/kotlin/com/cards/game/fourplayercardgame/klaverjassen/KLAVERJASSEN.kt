package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.Table

object KLAVERJASSEN {

    const val NUMBER_OF_TRICKS_PER_ROUND = 8
    const val NUMBER_OF_ROUNDS_PER_GAME = 16
    val VERY_FIRST_START_PLAYER = Table.WEST

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

    fun toBonusRankNumber(card: Card) : Int {
        return card.rank.rankNumber
    }


    fun bonusValue(cardList: List<Card>, trumpColor: CardColor): Int {
        assert(cardList.size == 4)
        return CardColor.values().sumOf { color -> bonusValueForColor(cardList, color, trumpColor) }
    }

    private fun bonusValueForColor(cardList: List<Card>, forCardColor: CardColor, trumpColor: CardColor): Int {
        val checkList = cardList.filter { card -> card.color == forCardColor }.sortedBy { card -> card.rank }
        val stuk = if (forCardColor == trumpColor && checkList.any { card -> card.rank == CardRank.QUEEN } && checkList.any { card -> card.rank == CardRank.KING }) {
            20
        } else {
            0
        }
        val bonus = when (checkList.size) {
            3 -> if (toBonusRankNumber(checkList[2]) - toBonusRankNumber(checkList[0]) == 2) 20 else 0
            4 ->
                if (toBonusRankNumber(checkList[3]) - toBonusRankNumber(checkList[0]) == 3)
                    50
                else if (toBonusRankNumber(checkList[2]) - toBonusRankNumber(checkList[0]) == 2)
                    20
                else if (toBonusRankNumber(checkList[3]) - toBonusRankNumber(checkList[1]) == 2)
                    20
                else
                    0
            else -> 0
        }
        return bonus + stuk
    }

}