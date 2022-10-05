package com.cards.game.hearts

import com.cards.game.Player
import com.cards.game.card.Card
import java.lang.Exception

class CardPlayer(
    val player: Player,
    val cardsInHand: MutableList<Card>,
    val game: Game) {

    fun removeCard(card: Card) {
        if (!cardsInHand.remove(card)) {
            throw Exception("cannot remove card $card from hand of player $player ")
        }
    }

    fun chooseCard() : Card {
        val leadColor = game.getCurrentRound().getTrickOnTable().leadColor()
        val legalCards = HeartsRulesBook.legalPlayableCards(cardsInHand, leadColor)
        return legalCards.random()
    }
}