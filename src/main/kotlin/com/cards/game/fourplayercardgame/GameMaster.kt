package com.cards.game.fourplayercardgame

import com.cards.game.card.Card
import com.cards.game.card.CardDeck

class GameMaster(
    val game : Game,
    private val playerList: List<CardPlayer>) {

    private val cardDeck = CardDeck()

    init {
        dealCards()
    }

    private fun dealCards() {
        cardDeck.shuffle(game.getSeed())
        val cardsInHand = cardDeck.numberOfCards() / Player.values().size
        playerList.forEachIndexed { i, player ->
            player.setCardsInHand(cardDeck.getCards(cardsInHand*i, cardsInHand))
        }
    }

    fun getCardPlayer(player: Player) = playerList.first { cardPlayer -> cardPlayer.player == player }

    fun playCard(card: Card) {
        playCard(game.getCurrentRound().getTrickOnTable().playerToMove(), card)
    }

    private fun playCard(player: Player, card: Card) {
        if (legalCardToPlay(player, card)) {
            getCardPlayer(player).removeCard(card)
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