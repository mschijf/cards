package com.cards.game.fourplayercardgame

import com.cards.game.card.Card

interface GameRules {
    fun winnerForTrick(trick: Trick) : Player?
    fun winningCardForTrick(trick: Trick) : Card?
    fun getScoreForTrick(trick: Trick): Score
    fun getValueForTrick(trick: Trick): Int

    fun roundIsComplete(round: Round): Boolean

    fun legalPlayableCardsForTrickOnTable(trickOnTable: Trick, cardsInHand: List<Card>): List<Card>

}