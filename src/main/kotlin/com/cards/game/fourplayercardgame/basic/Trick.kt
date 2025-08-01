package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card
import com.cards.game.card.CardColor

abstract class Trick(
    private val leadPlayer: Player) {

    private var playerToMove = leadPlayer
    private val cardsPlayed = mutableListOf<PlayerPlayedCard>()

    fun getLeadPlayer() = leadPlayer
    fun isLeadPLayer(player: Player) = player == leadPlayer
    fun getLeadColor() = getCardPlayedBy(leadPlayer)?.color
    fun isLeadColor(color: CardColor) = color == getLeadColor()
    fun getPlayerToMove() = playerToMove
    fun getCardsPlayed() = cardsPlayed
    fun isComplete(): Boolean = cardsPlayed.size >= Table.values().size
    fun isLastPlayerToMove() = (getCardsPlayed().size == Table.values().size)
    fun hasNotStarted(): Boolean = cardsPlayed.isEmpty()

    abstract fun getLegalPlayableCards(cardsList: List<Card>): List<Card>
    abstract fun getWinner(): Player?
    abstract fun getWinningCard(): Card?

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