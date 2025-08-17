package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card

interface CardPlayedListener {
    fun signalCardPlayed(side: TableSide, card: Card)
}