package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.*
import com.cards.game.fourplayercardgame.basic.GameRules
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Round
import com.cards.game.fourplayercardgame.basic.Trick

class GameRulesHearts: GameRules {

    fun toRankNumber (card: Card) : Int = card.rank.rankNumber - 7

    override fun getInitialNumberOfCardsPerPlayer() = 8
    override fun tricksPerRound() = 8

    override fun winnerForTrick(trick: Trick) : Player? {
        return if (!trick.isComplete()) {
            return null
        } else {
            trick.getCardsPlayed()
                .filter { f -> f.card.color == trick.leadColor() }
                .maxByOrNull { f -> toRankNumber(f.card) }!!
                .player
        }
    }

    override fun winningCardForTrick(trick: Trick) : Card? {
        return trick.getCardsPlayed()
            .filter { f -> f.card.color == trick.leadColor() }
            .maxByOrNull { f -> toRankNumber(f.card) }?.card
    }

    private fun cardValue(card: Card): Int {
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

    override fun getScoreForTrick(trick: Trick): Score {
        val score = Score()
        if (trick.isComplete()) {
            val winner = winnerForTrick(trick)
            score.plusScorePerPlayer(
                winner!!,
                Player.values().sumOf { p -> cardValue(trick.getCardPlayedBy(p)!!)})
        }
        return score
    }

    override fun getScoreForRound(round: Round): Score {
        val score = Score()
        if (!round.isComplete()) {
            return score
        }
        round.getCompletedTrickList().forEach { t -> score.plus(getScoreForTrick(t))}
        return score
    }


    override fun getValueForTrick(trick: Trick)  = trick.getCardsPlayed().sumOf { c -> cardValue(c.card) }

    override fun roundIsComplete(round: Round): Boolean = round.completedTricksPlayed() >= tricksPerRound()

    override fun legalPlayableCardsForTrickOnTable(trickOnTable: Trick, cardsInHand: List<Card>): List<Card> {
        val leadColor = trickOnTable.leadColor()
        val legalCards = legalPlayableCards(cardsInHand, leadColor)
        return legalCards
    }

    private fun legalPlayableCards (cardList: List<Card>, leadColor: CardColor?) : List<Card> {
        if (leadColor == null)
            return cardList

        return cardList.filter{ c -> c.color == leadColor}.ifEmpty { cardList }
    }

}
