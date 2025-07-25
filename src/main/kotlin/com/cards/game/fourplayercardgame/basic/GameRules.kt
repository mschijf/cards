package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.Score

interface GameRules {
    //deal related
    fun getInitialNumberOfCardsPerPlayer(): Int
    fun tricksPerRound(): Int

    //trick related
    fun winnerForTrick(trick: Trick) : Player?
    fun winningCardForTrick(trick: Trick) : Card?
    fun getScoreForTrick(trick: Trick): Score
    fun getValueForTrick(trick: Trick): Int

    //round related
    fun getScoreForRound(round: Round): Score
    fun roundIsComplete(round: Round): Boolean

    //other
    fun legalPlayableCardsForTrickOnTable(trickOnTable: Trick, cardsInHand: List<Card>): List<Card>

}