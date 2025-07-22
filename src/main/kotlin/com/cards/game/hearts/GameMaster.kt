package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.Player

class GameMaster(
    val game : Game) {

    private val playerList = Player.values().map { p  -> Genius(p, game) }

    init {
        dealCards()
    }

    private fun dealCards() {
        HeartsRules.cardDeck.shuffle(game.getSeed())
        val cardsInHand = HeartsRules.nCardsInHand
        playerList.forEachIndexed { i, player -> player.setCardsInHand(HeartsRules.cardDeck.getCards(cardsInHand*i, cardsInHand)) }
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
        val legalCards = HeartsRules.legalPlayableCards(cardsInHand, leadColor)
        return legalCards.contains(card)
    }

}
