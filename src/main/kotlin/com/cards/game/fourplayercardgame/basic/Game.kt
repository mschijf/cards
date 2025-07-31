package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.CARDDECK
import com.cards.game.card.Card

abstract class Game() {

    private val playerList: List<Player> = initialPlayerList()

    private val completedRoundList = mutableListOf<Round>()
    private var currentRound = createFirstRound()

    init {
        dealCards()
    }

    //abstract player
    abstract fun initialPlayerList(): List<Player>

    //abstract round
    protected abstract fun createFirstRound(): Round
    protected abstract fun createNextRound(previousRound: Round): Round

    //abstract game
    abstract fun isFinished(): Boolean

    fun getPlayerList() = playerList
    fun getCardPlayer(tablePosition: Table) = getPlayerList().first { cardPlayer -> cardPlayer.tablePosition == tablePosition }
    fun getCompleteRoundsPlayed() = completedRoundList.toList()
    fun trickCompleted() = currentRound.getTrickOnTable().hasNotStarted()
    fun roundCompleted() = currentRound.hasNotStarted()
    fun getCurrentRound() = currentRound
    fun getPlayerToMove() = currentRound.getTrickOnTable().getPlayerToMove()
    fun getLastTrickWinner(): Player? =
        if (currentRound.hasNotStarted())
            completedRoundList.lastOrNull()?.getLastCompletedTrickWinner()
        else
            currentRound.getLastCompletedTrickWinner()

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
        val cardDeck = CARDDECK.baseDeckCardsSevenAndHigher.shuffled()
        val cardPiles = cardDeck.chunked(cardDeck.size/ playerList.size)
        playerList.forEachIndexed { idx, player -> player.setCardsInHand(cardPiles[idx])}
    }

    //play card
    fun playCard(card: Card) {
        if (isFinished()) {
            throw Exception("Trying to play a card, but the game is already over")
        }

        val playerToMove = currentRound.getTrickOnTable().getPlayerToMove()
        if (isLegalCardToPlay(playerToMove, card)) {
            playerToMove.removeCard(card)

            currentRound.playCard(card)
            if (currentRound.isComplete()) {
                addRound(currentRound)
                currentRound = createNextRound(currentRound)
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
        if (trickOnTable.getPlayerToMove() != player)
            return false

        val cardsInHand = player.getCardsInHand()
        val legalCards = trickOnTable.getLegalPlayableCards(cardsInHand)
        return legalCards.contains(card)
    }
}

