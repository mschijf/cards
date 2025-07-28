package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card
import com.cards.game.card.CardDeck32

abstract class Game() {

    private val playerList: List<Player> = initialPlayerList()

    private val completedRoundList = mutableListOf<Round>()
    private var currentRound = createFirstRound()
    private val cardDeck = CardDeck32()

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
    fun getCardPlayer(tablePosition: TablePosition) = getPlayerList().first { cardPlayer -> cardPlayer.tablePosition == tablePosition }
    fun getCompleteRoundsPlayed() = completedRoundList.toList()
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
        val cardPiles = cardDeck.getCards().chunked(cardDeck.numberOfCards()/ playerList.size)
        playerList.forEachIndexed { idx, player -> player.setCardsInHand(cardPiles[idx])}
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
        if (trickOnTable.playerToMove() != player)
            return false

        val cardsInHand = player.getCardsInHand()
        val legalCards = trickOnTable.legalPlayableCards(cardsInHand)
        return legalCards.contains(card)
    }
}

