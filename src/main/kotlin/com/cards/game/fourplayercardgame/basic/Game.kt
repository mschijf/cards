package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card

abstract class Game() {

    private val roundList = mutableListOf<Round>()

    abstract fun createTrick(leadPosition: TablePosition): Trick
    abstract fun createRound(): Round

    fun start() {
        createNewRoundAndTrick(TablePosition.WEST)
    }

    //abstract game
    abstract fun isFinished(): Boolean

    fun trickCompleted() = getCurrentRound().getTrickOnTable().hasNotStarted()
    fun roundCompleted() = getCurrentRound().hasNotStarted()
    //todo: ^^^ beide functies niet echt mooi, omdat ze eigenlijk zeggen of een nieuwe ronde begonenn is.

    fun getLastTrickWinner(): TablePosition?  =
        if (getCurrentRound().hasNotStarted())
            getPreviousRound()?.getLastCompletedTrickWinner()
        else
            getCurrentRound().getLastCompletedTrickWinner()

    fun getRounds() = roundList.toList()
    fun getCurrentRound() = roundList.lastOrNull()?:throw Exception("We do not have a current round")
    fun getPreviousRound() = if (roundList.size >= 2) roundList[roundList.size - 2] else null
    open fun getPositionToMove() = getCurrentRound().getTrickOnTable().getPositionToMove()

    private fun createNewRoundAndTrick(leadPosition: TablePosition) {
        if (isFinished())
            throw Exception("Trying to add a round to a finished game")
        val round = createRound()
        roundList.add(round)
        createNewTrick(leadPosition)
    }

    private fun createNewTrick(leadPosition: TablePosition): Trick {
        val trick = createTrick(leadPosition)
        getCurrentRound().addTrick(trick)
        return trick
    }

    //play card
    fun playCard(card: Card) {
        if (isFinished())
            throw Exception("Trying to play a card, but the game is already over")

        val currentRound = getCurrentRound()
        val trickOnTable = currentRound.getTrickOnTable()

        trickOnTable.addCard(getPositionToMove(), card)

        if (isFinished()) {
            //nothing to do right now
        } else if (currentRound.isComplete()) {
            val previousLeadStart = currentRound.getTrickList().first().getLeadPosition()
            createNewRoundAndTrick(previousLeadStart.clockwiseNext())
        } else if (trickOnTable.isComplete()) {
            createNewTrick(trickOnTable.getWinner()!!)
        } else {
            //nothing to do
        }
    }
}

