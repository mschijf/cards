package com.cards.game.hearts

import com.cards.game.card.Card

class HeartsPlayer(
    val player: Player,
    val cardsInHand: ArrayList<Card>,
    firstLeadPlayer: Player) {

    val tricksPlayed = emptyArray<Trick>()
    var currentTrick = Trick(firstLeadPlayer)

    //
}