package com.cards.game.fourplayercardgame

import com.cards.game.card.Card
import com.cards.game.hearts.HeartsRulesBook

class Trick(
    private val leadPlayer: Player
) {
    private val cardsPlayed = arrayListOf<PlayerPlayedCard>()
    private var playerToMove = leadPlayer

    fun leadColor() = getCardPlayedBy(leadPlayer)?.color

    fun playerToMove() = playerToMove

    fun getCardPlayedBy(player: Player): Card? {
        return cardsPlayed
            .firstOrNull { p -> p.player == player }
            ?.card
    }

    fun isComplete(): Boolean = cardsPlayed.size >= 4
    fun isNew(): Boolean = cardsPlayed.size == 0

    fun winner(): Player {
        if (cardsPlayed.size == 0)
            return leadPlayer

        val leadingColor = cardsPlayed[0].card.color
        return cardsPlayed
            .filter { f -> f.card.color == leadingColor }
            .maxByOrNull { f -> HeartsRulesBook.toRankNumber(f.card) }!!
            .player
    }

    fun addCard(aCard: Card) {
        if (isComplete())
            throw Exception("Adding a card to a completed trick")

        cardsPlayed.add(PlayerPlayedCard(playerToMove, aCard))
        playerToMove = playerToMove.nextPlayer()
    }

    fun getScore(): Score {
        val score = Score()
        if (!isComplete()) {
            return score
        }
        val winner = winner()
        cardsPlayed.forEach { cardPlayed -> score.plusCardValueForPlayer(winner, cardPlayed.card) }
        return score
    }
}

data class PlayerPlayedCard(val player: Player, val card: Card)