package com.cards.game.hearts

import com.cards.game.card.Card
import java.lang.Exception

class Trick(
    private val leadPlayer: Player) {

    private val cardsPlayed = arrayOfNulls<Card>(4)
    private var playerToMove = leadPlayer
    private var numberCardsPlayed = 0

    fun getCardPlayedByPlayer(player: Player): Card? {
        return cardsPlayed[player.index]
    }

    fun addCard(aCard: Card) {
        if (isComplete()) {
            throw Exception("Adding a card on a completed trick")
        }
        cardsPlayed[playerToMove.index] = aCard
        playerToMove = playerToMove.nextPlayer()
        numberCardsPlayed++
    }

    fun isComplete(): Boolean = numberCardsPlayed >= 4

    fun winner(): Player {
        if (numberCardsPlayed == 0)
            return leadPlayer
        var winningPlayer =  leadPlayer
        var otherPlayer = leadPlayer.nextPlayer()
        while (otherPlayer != leadPlayer && cardsPlayed[otherPlayer.index] != null) {
            if (HeartsRulesBook.firstCardBeatsSecondCard(cardsPlayed[otherPlayer.index]!!, cardsPlayed[winningPlayer.index]!!))
                winningPlayer = otherPlayer
            otherPlayer = otherPlayer.nextPlayer()
        }
        return winningPlayer
    }
}