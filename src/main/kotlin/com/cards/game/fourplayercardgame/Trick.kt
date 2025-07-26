package com.cards.game.fourplayercardgame

import com.cards.game.card.Card

class Trick(
    private val gameRules: GameRules,
    private val leadPlayer: Player) {

    private var playerToMove = leadPlayer
    private val cardsPlayed = arrayListOf<PlayerPlayedCard>()

    fun getLeadPlayer() = leadPlayer

    fun isLeadPLayer(player: Player) = player == leadPlayer

    fun leadColor() = getCardPlayedBy(leadPlayer)?.color

    fun playerToMove() = playerToMove

    fun getCardsPlayed() = cardsPlayed

    fun isComplete(): Boolean = cardsPlayed.size >= Player.values().size

    fun isLastPlayerToMove() = (getCardsPlayed().size == Player.values().size-1)

    fun isNew(): Boolean = cardsPlayed.isEmpty()

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


    fun winner(): Player? = gameRules.winnerForTrick(this)
    fun winningCard(): Card? = gameRules.winningCardForTrick(this)
}