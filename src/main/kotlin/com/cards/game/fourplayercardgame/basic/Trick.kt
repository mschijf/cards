package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card

class Trick(
    private val game: Game,
    private val leadPlayer: Player
) {

    private var playerToMove = leadPlayer
    private val cardsPlayed = arrayListOf<PlayerPlayedCard>()

    fun getLeadPlayer() = leadPlayer
    fun isLeadPLayer(player: Player) = player == leadPlayer
    fun leadColor() = getCardPlayedBy(leadPlayer)?.color
    fun playerToMove() = playerToMove
    fun getCardsPlayed() = cardsPlayed
    fun isComplete(): Boolean = cardsPlayed.size >= TablePosition.values().size
    fun isLastPlayerToMove() = (getCardsPlayed().size == TablePosition.values().size-1)
    fun isNew(): Boolean = cardsPlayed.isEmpty()

    fun winner(): Player? = game.winnerForTrick(this)
    fun winningCard(): Card? = game.winningCardForTrick(this)

    fun getCardPlayedBy(player: Player): Card? {
        return cardsPlayed
            .firstOrNull { p -> p.player == player }
            ?.card
    }

    fun addCard(aCard: Card) {
        if (isComplete())
            throw Exception("Adding a card to a completed trick")

        cardsPlayed.add(PlayerPlayedCard(playerToMove, aCard))
        playerToMove = game.nextPlayer(playerToMove)
    }
}