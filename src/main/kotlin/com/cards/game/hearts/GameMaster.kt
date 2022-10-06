package com.cards.game.hearts

import com.cards.game.Player
import com.cards.game.card.Card
import com.cards.game.card.CardDeck

class GameMaster {
    private val cardDeck = CardDeck().shuffle()
    val maxCardsInHand = cardDeck.numberOfCards() / Player.values().size

    val game = Game(Player.SOUTH)

    private val playerList = Player.values().mapIndexed { i, p  ->
        CardPlayer(p, cardDeck.getCards(maxCardsInHand*i, maxCardsInHand).toMutableList(), game) }

    fun getCardPlayer(player: Player) = playerList.first { p -> p.player == player }

    fun playCard(card: Card):Player? {
        return playCard(game.getCurrentRound().getTrickOnTable().playerToMove(), card)
    }

    private fun playCard(player: Player, card: Card): Player? {
        //todo --> send notification to all players that a card has been played
        if (legalCardToPlay(player, card)) {
            getCardPlayer(player).removeCard(card)
            return game.playCard(card)
        } else {
            throw Exception("trying to play an illegal card: Card($card)")
        }
    }

    private fun legalCardToPlay(player: Player, card: Card): Boolean {
        if (game.getCurrentRound().getTrickOnTable().playerToMove() != player)
            return false

        val leadColor = game.getCurrentRound().getTrickOnTable().leadColor()
        val cardsInHand = getCardPlayer(player).cardsInHand
        val legalCards = HeartsRulesBook.legalPlayableCards(cardsInHand, leadColor)
        return legalCards.contains(card)
    }


}