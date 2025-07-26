package com.cards.game.fourplayercardgame

import com.cards.game.card.Card

interface GameRules {
    //trick
    fun winnerForTrick(trick: Trick) : Player?
    fun winningCardForTrick(trick: Trick) : Card?
    fun getScoreForTrick(trick: Trick): Score
    fun getValueForTrick(trick: Trick): Int

    //round
    fun getScoreForRound(round: Round): Score
    fun roundIsComplete(round: Round): Boolean

    //other
    fun legalPlayableCardsForTrickOnTable(trickOnTable: Trick, cardsInHand: List<Card>): List<Card>

}