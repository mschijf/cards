package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardDeck
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.Player

object HeartsRules {
    val valueToGoDown = 60
    val allPointsForPit = 15
    val valueToFinish = 0

    val cardDeck = CardDeck()
    val nCardsInHand = cardDeck.numberOfCards() / Player.values().size
    val nTricksPerRound = nCardsInHand

    fun toRankNumber (card: Card) : Int = card.rank.rankNumber - 7
    fun higher (card1: Card, card2: Card) = toRankNumber(card1) > toRankNumber(card2)
    fun lower (card1: Card, card2: Card) = toRankNumber(card1) < toRankNumber(card2)

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