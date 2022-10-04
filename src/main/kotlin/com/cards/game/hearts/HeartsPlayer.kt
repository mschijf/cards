package com.cards.game.hearts

import com.cards.game.card.Card
import java.lang.Exception

class HeartsPlayer(
    val player: Player,
    val cardsInHand: MutableList<Card>,
    val game: Game) {

    fun removeCard(card: Card) {
        if (!cardsInHand.remove(card)) {
            throw Exception("cannot remove card $card from hand of player $player ")
        }
    }

    fun chooseCard() : Card {
        val legalCards = HeartsRulesBook.legalPlayableCards(cardsInHand, game.trickOnTable.leadColor())
        return legalCards.random()
    }
}