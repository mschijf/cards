package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.Player

class GameMaster {
    val game = Game(Player.WEST)
    private val playerList = Player.values().map { p  -> Genius(p, game) }
    val maxCardsInHand = game.cardDeck.numberOfCards() / Player.values().size

    init {
        dealCards()
    }

    private fun dealCards() {
        game.cardDeck.shuffle()
        playerList.forEachIndexed { i, player -> player.setCardsInHand(game.cardDeck.getCards(maxCardsInHand*i, maxCardsInHand)) }
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
