package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.Score

abstract class Game() {

    private val playerList: List<CardPlayer> = initialPlayerList()

    private val completedRoundList = mutableListOf<Round>()
    private var currentRound = startNewRound(getCardPlayer(TablePosition.WEST))

    //abstract player
    abstract fun initialPlayerList(): List<CardPlayer>
    abstract fun nextPlayer(player: CardPlayer): CardPlayer

    //abstract trick
    abstract fun winnerForTrick(trick: Trick) : CardPlayer?
    abstract fun winningCardForTrick(trick: Trick) : Card?
    abstract fun legalPlayableCardsForTrick(trickOnTable: Trick, cardsInHand: List<Card>): List<Card>
    abstract fun getScoreForTrick(trick: Trick): Score
    abstract fun getValueForTrick(trick: Trick): Int

    //abstract round
    abstract fun roundIsComplete(round: Round): Boolean
    abstract fun getScoreForRound(game: Game, round: Round): Score

    //abstract game
    abstract fun isFinished(): Boolean

    open fun initialNumberOfCardsInHand() = 8
    open fun numberOfTricksPerRound() = 8

    fun getPlayerList() = playerList
    fun getCardPlayer(tablePosition: TablePosition) = getPlayerList().first { cardPlayer -> cardPlayer.tablePosition == tablePosition }
    fun startNewRound(leadPlayer: CardPlayer) = Round(this, leadPlayer)
    fun completeRoundsPlayed() = completedRoundList.toList()
    fun trickCompleted() = currentRound.getTrickOnTable().isNew()
    fun roundCompleted() = currentRound.isNew()
    fun getCurrentRound() = currentRound
    fun getPlayerToMove() = currentRound.getTrickOnTable().playerToMove()
    fun getLastTrickWinner(): CardPlayer? =
        if (!currentRound.isNew())
            currentRound.getLastCompletedTrickWinner()
        else
            completedRoundList.lastOrNull()?.getLastCompletedTrickWinner()


    fun playCard(card: Card) {
        if (isFinished()) {
            throw Exception("Trying to play a card, but the game is already over")
        }

        currentRound.playCard(card)
        if (currentRound.isComplete()) {
            addRound(currentRound)
            currentRound = startNewRound(nextPlayer(currentRound.getLeadPLayer()))
        }
    }

    private fun addRound(round: Round) {
        if (!isFinished()) {
            completedRoundList.add(round)
        } else {
            throw Exception("Trying to add a round to a finished game")
        }
    }

    // score
    fun getTotalScore(): Score {
        return getCumulativeScorePerRound().lastOrNull()?: Score.ZERO
    }

    private fun getScorePerRound(): List<Score> {
        return completedRoundList.mapIndexed { index, round ->  getScoreForRound(this, round)}
    }

    fun getCumulativeScorePerRound(): List<Score> {
        val list = getScorePerRound()
        val x = list.runningFold(Score.ZERO) { acc, sc -> acc.plus(sc) }.drop(1)
        return x
    }
}

