package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.Table

const val NUMBER_OF_TRICKS_PER_ROUND = 8
const val NUMBER_OF_ROUNDS_PER_GAME = 16
val VERY_FIRST_START_PLAYER = Table.WEST
const val PIT_BONUS = 100

fun Card.beats(other: Card?, trumpColor: CardColor): Boolean {
    if (other == null)
        return true
    return if (this.color == other.color) {
        if (this.color == trumpColor) {
            this.toRankNumberTrump() > other.toRankNumberTrump()
        } else {
            this.toRankNumberNoTrump() > other.toRankNumberNoTrump()
        }
    } else {
        (other.color != trumpColor)
    }
}

fun Card.cardValue(trump: CardColor): Int {
    return when (this.rank) {
        CardRank.ACE -> 11
        CardRank.TEN -> 10
        CardRank.KING -> 4
        CardRank.QUEEN -> 3
        CardRank.JACK -> if (this.color == trump) 20 else 2
        CardRank.NINE -> if (this.color == trump) 14 else 0
        CardRank.EIGHT -> 0
        CardRank.SEVEN -> 0
        else -> 0
    }
}

fun Card.toRankNumberNoTrump () : Int {
    return when(this.rank) {
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

fun Card.toRankNumberTrump () : Int {
    return when(this.rank) {
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

fun Card.toBonusRankNumber() : Int {
    return this.rank.rankNumber
}

fun List<Card>.bonusValue(trumpColor: CardColor): Int {
    assert(this.size == 4)
    return CardColor.values().sumOf { color -> bonusValueForColor(this, color, trumpColor) } +
            bonusValueForfourEqualRanks(this)
}

private fun bonusValueForfourEqualRanks(cardList: List<Card>): Int {
    //see for rules: https://www.spelregels.eu/wp-content/uploads/2021/01/spelregels-klaverjassen.pdf
    return if (cardList.map {it.rank}.distinct().size == 1)
        100
    else
        0
}

private fun bonusValueForColor(cardList: List<Card>, forCardColor: CardColor, trumpColor: CardColor): Int {
    val checkList = cardList.filter { card -> card.color == forCardColor }.sortedBy { card -> card.rank }
    val stuk = if (forCardColor == trumpColor && checkList.any { card -> card.rank == CardRank.QUEEN } && checkList.any { card -> card.rank == CardRank.KING }) {
        20
    } else {
        0
    }
    val bonus = when (checkList.size) {
        3 -> if (checkList[2].toBonusRankNumber() - checkList[0].toBonusRankNumber() == 2) 20 else 0
        4 ->
            if (checkList[3].toBonusRankNumber() - checkList[0].toBonusRankNumber() == 3)
                50
            else if (checkList[2].toBonusRankNumber() - checkList[0].toBonusRankNumber() == 2)
                20
            else if (checkList[3].toBonusRankNumber() - checkList[1].toBonusRankNumber() == 2)
                20
            else
                0
        else -> 0
    }
    return bonus + stuk
}