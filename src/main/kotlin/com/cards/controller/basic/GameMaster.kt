package com.cards.controller.basic

import com.cards.game.card.CARDDECK
import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.Game
import com.cards.game.fourplayercardgame.basic.GameStatus
import com.cards.game.fourplayercardgame.basic.TableSide
import com.cards.game.fourplayercardgame.basic.player.Player
import com.cards.tools.RANDOMIZER

abstract class GameMaster {
    private var playerList: List<Player> = emptyList()

    abstract fun createGame(): Game
    abstract fun initialPlayerList(): List<Player>
    abstract fun initialStartSide(): TableSide
    abstract fun isLegalCardToPlay(player: Player, card: Card): Boolean

    private var game = createGame()
    open fun getGame(): Game = game

    fun startNewGame() {
        game = createGame()
        playerList = initialPlayerList()
        dealCards()
        getGame().start(initialStartSide())
    }

    fun getPlayerList() = playerList
    fun getCardPlayer(tableSide: TableSide) = getPlayerList().first { cardPlayer -> cardPlayer.tableSide == tableSide }
    fun getPlayerToMove(): Player = getCardPlayer(getGame().getSideToMove())

    private fun allPlayersHaveNoCards() = playerList.all { it.getCardsInHand().isEmpty()}

    private fun dealCards() {
        assert(allPlayersHaveNoCards())

        val cardDeck = CARDDECK.baseDeckCardsSevenAndHigher.shuffled(RANDOMIZER.getShuffleRandomizer())
        val cardPiles = cardDeck.chunked(cardDeck.size/ playerList.size)
        playerList.forEachIndexed { idx, player -> player.setCardsInHand(cardPiles[idx])}
    }

    fun playCard(card: Card): GameStatus {
        if (getGame().isFinished())
            throw Exception("Trying to play a card, but the game is already over")

        val playerToMove = getCardPlayer(getGame().getSideToMove())
        if (!isLegalCardToPlay(playerToMove, card))
            throw Exception("trying to play an illegal card: Card($card)")

        playerToMove.removeCard(card)
        val gameStatus = getGame().playCard(card)
        if (allPlayersHaveNoCards())
            dealCards()
        return gameStatus
    }

    fun isGameFinished() = getGame().isFinished()

    //==================================================================================================================

    private fun printLastRoundPlayed() {
        print("[Seed: ${RANDOMIZER.getLastSeedUsed()}]  Round: ")
        getGame().getRounds().last().getTrickList().forEach { trick->
            print("[")
            val sideToLead = trick.getSideToLead()
            TableSide.values().map { side -> Pair(side, trick.getCardPlayedBy(side))}.forEach { (sidePlayed, cardPlayed) ->
                if (sidePlayed == sideToLead)
                    print("(")

                print("${cardPlayed}")

                if (sidePlayed == sideToLead)
                    print(")")

                if (sidePlayed != TableSide.values().last())
                    print(",")
            }
            print("]  ")
        }
        println()
    }


}