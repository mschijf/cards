package com.cards.game.hearts

import com.cards.game.card.Card
import java.lang.Exception

class HeartsPlayer(
    val player: Player,
    val cardsInHand: MutableList<Card>,
    firstLeadPlayer: Player) {

    fun removeCard(card: Card) {
        if (!cardsInHand.remove(card)) {
            throw Exception("cannot remove card $card from hand of player $player ")
        }
    }
}