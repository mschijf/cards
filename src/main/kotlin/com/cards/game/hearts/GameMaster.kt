package com.cards.game.hearts

import com.cards.game.fourplayercardgame.Player
import com.cards.game.card.Card
import com.cards.game.card.CardDeck

class GameMaster {
    private val cardDeck = CardDeck()
    val maxCardsInHand = cardDeck.numberOfCards() / Player.values().size
    val game = Game(Player.SOUTH, maxCardsInHand)
    private val playerList = Player.values().map { p  -> Genius(p, game) }

    init {
        dealCards()
    }

    private fun dealCards() {
        cardDeck.shuffle()
        playerList.forEachIndexed { i, player -> player.setCardsInHand(cardDeck.getCards(maxCardsInHand*i, maxCardsInHand)) }
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
        if (game.getCurrentRound().getTrickOnTable().playerToMove() != player)
            return false

        val leadColor = game.getCurrentRound().getTrickOnTable().leadColor()
        val cardsInHand = getCardPlayer(player).getCardsInHand()
        val legalCards = HeartsRulesBook.legalPlayableCards(cardsInHand, leadColor)
        return legalCards.contains(card)
    }
}
