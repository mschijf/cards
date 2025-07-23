package com.cards.game.fourplayercardgame

import com.cards.game.card.CardDeck

object Table {
    val cardDeck = CardDeck()
    val nCardsInHand = cardDeck.numberOfCards() / Player.values().size
    val nTricksPerRound = nCardsInHand
}