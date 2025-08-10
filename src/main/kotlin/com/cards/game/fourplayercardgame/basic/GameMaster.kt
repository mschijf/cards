package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.CARDDECK
import com.cards.game.card.Card
import com.cards.tools.RANDOMIZER

abstract class GameMaster {
    private var playerList: List<Player> = emptyList()

    abstract fun createGame(): Game
    abstract fun initialPlayerList(): List<Player>
    abstract fun isLegalCardToPlay(player: Player, card: Card): Boolean

    private var game = createGame()
    protected fun getGame(): Game = game

    init {
        startNewGame()
    }

    fun startNewGame(): Game {
        game = createGame()
        playerList = initialPlayerList()
        dealCards()
        getGame().start()
        return getGame()
    }

    fun getPlayerList() = playerList
    fun getCardPlayer(tablePosition: TablePosition) = getPlayerList().first { cardPlayer -> cardPlayer.tablePosition == tablePosition }

    private fun allPlayersHaveNoCards() = playerList.all { it.getCardsInHand().isEmpty()}

    private fun dealCards() {
        assert(allPlayersHaveNoCards())

        val cardDeck = CARDDECK.baseDeckCardsSevenAndHigher.shuffled(RANDOMIZER.getShuffleRandomizer())
        val cardPiles = cardDeck.chunked(cardDeck.size/ playerList.size)
        playerList.forEachIndexed { idx, player -> player.setCardsInHand(cardPiles[idx])}
    }

    fun playCard(card: Card) {
        if (getGame().isFinished())
            throw Exception("Trying to play a card, but the game is already over")

        val playerToMove = getCardPlayer(getGame().getPositionToMove())
        if (isLegalCardToPlay(playerToMove, card)) {
            playerToMove.removeCard(card)
            getGame().playCard(card)
            if (allPlayersHaveNoCards())
                dealCards()
        } else {
            throw Exception("trying to play an illegal card: Card($card)")
        }
    }

    //==================================================================================================================

    private fun printLastRoundPlayed() {
        print("[Seed: ${RANDOMIZER.getLastSeedUsed()}]  Round: ")
        getGame().getRounds().last().getTrickList().forEach { trick->
            print("[")
            val leadPosition = trick.getCardsPlayed().first().tablePosition
            getPlayerList().map { pl -> trick.getCardsPlayed().first{ cp -> cp.tablePosition == pl.tablePosition }}.forEach { cardPlayed ->
                if (cardPlayed.tablePosition == leadPosition)
                    print("(")
                print("${cardPlayed.card}")
                if (cardPlayed.tablePosition == leadPosition)
                    print(")")
                if (getPlayerList().last().tablePosition != cardPlayed.tablePosition)
                    print(",")
            }
            print("]  ")
        }
        println()
    }


}