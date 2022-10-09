package com.cards.game.fourplayercardgame

import com.cards.game.card.Card
import com.cards.game.hearts.HeartsRulesBook
import com.cards.game.hearts.Score

class Trick(
    private val leadPlayer: Player) {
    private val cardsPlayed = arrayListOf<PlayerPlayedCard>()
    private var playerToMove = leadPlayer

    fun leadColor() = getCardPlayedBy(leadPlayer)?.color

    fun playerToMove() = playerToMove

    fun getCardsPlayed() = cardsPlayed

    fun isComplete(): Boolean = cardsPlayed.size >= Player.values().size

    fun isNew(): Boolean = cardsPlayed.isEmpty()

    fun winner() = HeartsRulesBook.trickWinner(this)

    fun getScore() = Score(this)

    fun getCardPlayedBy(player: Player): Card? {
        return cardsPlayed
            .firstOrNull { p -> p.player == player }
            ?.card
    }

    fun addCard(aCard: Card) {
        if (isComplete())
            throw Exception("Adding a card to a completed trick")

        cardsPlayed.add(PlayerPlayedCard(playerToMove, aCard))
        playerToMove = playerToMove.nextPlayer()
    }
}

data class PlayerPlayedCard(val player: Player, val card: Card)