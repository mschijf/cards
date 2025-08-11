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
    open fun getGame(): Game = game

    fun startNewGame() {
        game = createGame()
        playerList = initialPlayerList()
        dealCards()
        getGame().start()
    }

    fun getPlayerList() = playerList
    fun getCardPlayer(tablePosition: TablePosition) = getPlayerList().first { cardPlayer -> cardPlayer.tablePosition == tablePosition }
    fun getPlayerToMove(): Player = getCardPlayer(getGame().getPositionToMove())

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

    fun isGameFinished() = getGame().isFinished()

    //==================================================================================================================

    private fun printLastRoundPlayed() {
        print("[Seed: ${RANDOMIZER.getLastSeedUsed()}]  Round: ")
        getGame().getRounds().last().getTrickList().forEach { trick->
            print("[")
            val leadPosition = trick.getCardsPlayed().first().position
            getPlayerList().map { pl -> trick.getCardsPlayed().first{ cp -> cp.position == pl.tablePosition }}.forEach { cardPlayed ->
                if (cardPlayed.position == leadPosition)
                    print("(")
                print("${cardPlayed.card}")
                if (cardPlayed.position == leadPosition)
                    print(")")
                if (getPlayerList().last().tablePosition != cardPlayed.position)
                    print(",")
            }
            print("]  ")
        }
        println()
    }


}