package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card
import com.cards.game.card.CardDeck
import com.cards.game.fourplayercardgame.hearts.HeartsConstants.INITIAL_NUMBER_OF_CARDS_IN_HAND

abstract class Game() {

    private val playerList: List<Player> = initialPlayerList()

    private val completedRoundList = mutableListOf<Round>()
    private var currentRound = createRound(getCardPlayer(TablePosition.WEST))
    private val cardDeck = CardDeck()

    init {
        dealCards()
    }

    //abstract player
    abstract fun initialPlayerList(): List<Player>

    //abstract trick
    abstract fun legalPlayableCardsForTrick(trickOnTable: Trick, cardsInHand: List<Card>): List<Card>
    abstract fun getScoreForTrick(trick: Trick): Score
    abstract fun getValueForTrick(trick: Trick): Int

    //abstract round
    protected abstract fun createRound(leadPlayer: Player): Round
    abstract fun getScoreForRound(game: Game, round: Round): Score

    //abstract game
    abstract fun isFinished(): Boolean

    fun getPlayerList() = playerList
    fun getCardPlayer(tablePosition: TablePosition) = getPlayerList().first { cardPlayer -> cardPlayer.tablePosition == tablePosition }
    fun completeRoundsPlayed() = completedRoundList.toList()
    fun trickCompleted() = currentRound.getTrickOnTable().hasNotStarted()
    fun roundCompleted() = currentRound.hasNotStarted()
    fun getCurrentRound() = currentRound
    fun getPlayerToMove() = currentRound.getTrickOnTable().playerToMove()
    fun getLastTrickWinner(): Player? =
        if (!currentRound.hasNotStarted())
            currentRound.getLastCompletedTrickWinner()
        else
            completedRoundList.lastOrNull()?.getLastCompletedTrickWinner()

    //round
    private fun addRound(round: Round) {
        if (!isFinished()) {
            completedRoundList.add(round)
        } else {
            throw Exception("Trying to add a round to a finished game")
        }
    }

    //deal cards
    private fun dealCards() {
        cardDeck.shuffle()
        val cardsInHand = INITIAL_NUMBER_OF_CARDS_IN_HAND
        getPlayerList().forEachIndexed { i, player ->
            player.setCardsInHand(cardDeck.getCards(cardsInHand*i, cardsInHand))
        }
    }

    //play card
    fun playCard(card: Card) {
        if (isFinished()) {
            throw Exception("Trying to play a card, but the game is already over")
        }

        val playerToMove = currentRound.getTrickOnTable().playerToMove()
        if (isLegalCardToPlay(playerToMove, card)) {
            playerToMove.removeCard(card)

            currentRound.playCard(card)
            if (currentRound.isComplete()) {
                addRound(currentRound)
                currentRound = createRound(currentRound.getLeadPlayer().nextPlayer())
            }

            if (roundCompleted()) {
                dealCards()
            }
        } else {
            throw Exception("trying to play an illegal card: Card($card)")
        }
    }

    fun isLegalCardToPlay(player: Player, card: Card): Boolean {
        val trickOnTable = getCurrentRound().getTrickOnTable()
        if (trickOnTable.playerToMove() != player)
            return false

        val cardsInHand = player.getCardsInHand()
        val legalCards = legalPlayableCardsForTrick(trickOnTable, cardsInHand)
        return legalCards.contains(card)
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

