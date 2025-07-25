package com.cards.game.fourplayercardgame

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.CardDeck32
import com.cards.game.fourplayercardgame.basic.CardPlayer
import com.cards.game.fourplayercardgame.basic.Game
import com.cards.game.fourplayercardgame.basic.Player

class GameMaster(
    val game : Game,
    private val playerList: List<CardPlayer>) {

    private val cardDeck32 = CardDeck32()

    init {
        dealCards()
    }

    private fun dealCards() {
        cardDeck32.shuffle(game.getSeed())
        val cardsInHand = game.rules.getInitialNumberOfCardsPerPlayer()
        playerList.forEachIndexed { i, player ->
            player.setCardsInHand(cardDeck32.getCards(cardsInHand*i, cardsInHand))
        }
    }

    fun getCardPlayer(player: Player) = playerList.first { cardPlayer -> cardPlayer.player == player }

    fun playCard(card: Card) {
        val playerToMove = game.getCurrentRound().getTrickOnTable().playerToMove()
        if (legalCardToPlay(playerToMove, card)) {
            getCardPlayer(playerToMove).removeCard(card)
            game.playCard(card)
            if (game.getStatusAfterLastMove().roundCompleted) {
                dealCards()
            }
        } else {
            throw Exception("trying to play an illegal card: Card($card)")
        }
    }

    fun legalCardToPlay(player: Player, card: Card): Boolean {
        val trickOnTable = game.getCurrentRound().getTrickOnTable()
        if (trickOnTable.playerToMove() != player)
            return false

        val cardsInHand = getCardPlayer(player).getCardsInHand()
        val legalCards = game.rules.legalPlayableCardsForTrickOnTable(trickOnTable, cardsInHand)
        return legalCards.contains(card)
    }

}