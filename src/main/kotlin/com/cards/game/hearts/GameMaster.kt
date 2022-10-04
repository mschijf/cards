package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardDeck

class GameMaster {
    private val cardDeck = CardDeck().shuffle()
    val maxCardsInHand = cardDeck.numberOfCards() / Player.values().size

    val game = Game()

    private val playerList = Player.values().mapIndexed { i, p  ->
        HeartsPlayer(p, cardDeck.getCards(maxCardsInHand*i, maxCardsInHand).toMutableList(), game) }

    fun getHeartsPlayer(player: Player) = playerList.first { p -> p.player == player }

    fun playCard(card: Card) {
        playCard(game.trickOnTable.playerToMove(), card)
    }

    fun playCard(player: Player, card: Card) {
        //todo --> send notification to all players that a card has been played
        if (!legalCardToPlay(player, card)) {
            throw Exception("trying to play an illegal card: Card($card)")
        }
        getHeartsPlayer(player).removeCard(card)
        game.playCard(card)
    }

    fun legalCardToPlay(player: Player, card: Card): Boolean {
        if (game.trickOnTable.playerToMove() != player)
            return false

        val legalCards = HeartsRulesBook.legalPlayableCards(getHeartsPlayer(player).cardsInHand, game.trickOnTable.leadColor())
        return legalCards.contains(card)
    }


}