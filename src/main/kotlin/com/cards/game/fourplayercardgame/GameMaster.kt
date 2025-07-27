package com.cards.game.fourplayercardgame

import com.cards.game.card.Card
import com.cards.game.card.CardDeck
import com.cards.game.fourplayercardgame.basic.CardPlayer
import com.cards.game.fourplayercardgame.basic.Game

class GameMaster(
    val game : Game) {

    private val cardDeck = CardDeck()

    init {
        dealCards()
    }

    private fun dealCards() {
        cardDeck.shuffle()
        val cardsInHand = game.initialNumberOfCardsInHand()
        game.getPlayerList().forEachIndexed { i, player ->
            player.setCardsInHand(cardDeck.getCards(cardsInHand*i, cardsInHand))
        }
    }

    fun playCard(card: Card) {
        playCard(game.getCurrentRound().getTrickOnTable().playerToMove(), card)
    }

    private fun playCard(player: CardPlayer, card: Card) {
        if (isLegalCardToPlay(player, card)) {
            player.removeCard(card)
            game.playCard(card)
            if (game.roundCompleted()) {
                dealCards()
            }
        } else {
            throw Exception("trying to play an illegal card: Card($card)")
        }
    }

    fun isLegalCardToPlay(player: CardPlayer, card: Card): Boolean {
        val trickOnTable = game.getCurrentRound().getTrickOnTable()
        if (trickOnTable.playerToMove() != player)
            return false

        val cardsInHand = player.getCardsInHand()
        val legalCards = game.legalPlayableCardsForTrick(trickOnTable, cardsInHand)
        return legalCards.contains(card)
    }

}