package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.Player

class Trick(
    private val leadPlayer: Player
) {
    private val cardsPlayed = arrayListOf<PlayerPlayedCard>()
    private var playerToMove = leadPlayer

    fun isLeadPLayer(player: Player) = player == leadPlayer
    fun leadColor() = getCardPlayedBy(leadPlayer)?.color

    fun playerToMove() = playerToMove

    fun getCardsPlayed() = cardsPlayed

    fun isComplete(): Boolean = cardsPlayed.size >= Player.values().size
    fun isLastPlayerToMove() = (getCardsPlayed().size == Player.values().size-1)

    fun isNew(): Boolean = cardsPlayed.isEmpty()

    fun winner() : Player? {
        return if (!isComplete()) {
            return null
        } else {
            getCardsPlayed()
                .filter { f -> f.card.color == leadColor() }
                .maxByOrNull { f -> HeartsRulesBook.toRankNumber(f.card) }!!
                .player
        }
    }

    fun winningCard() : Card? {
        return getCardsPlayed()
            .filter { f -> f.card.color == leadColor() }
            .maxByOrNull { f -> HeartsRulesBook.toRankNumber(f.card) }?.card
    }

    fun getScore() = Score(this)
    fun getValue()  = getCardsPlayed().sumOf { c -> HeartsRulesBook.cardValue(c.card) }

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