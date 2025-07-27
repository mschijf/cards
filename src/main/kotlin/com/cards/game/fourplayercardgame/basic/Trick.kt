package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.basic.PlayerPlayedCard

class Trick(
    private val game: Game,
    private val leadPlayer: CardPlayer
) {

    private var playerToMove = leadPlayer
    private val cardsPlayed = arrayListOf<PlayerPlayedCard>()

    fun getLeadPlayer() = leadPlayer
    fun isLeadPLayer(player: CardPlayer) = player == leadPlayer
    fun leadColor() = getCardPlayedBy(leadPlayer)?.color
    fun playerToMove() = playerToMove
    fun getCardsPlayed() = cardsPlayed
    fun isComplete(): Boolean = cardsPlayed.size >= TablePosition.values().size
    fun isLastPlayerToMove() = (getCardsPlayed().size == TablePosition.values().size-1)
    fun isNew(): Boolean = cardsPlayed.isEmpty()

    fun winner(): CardPlayer? = game.winnerForTrick(this)
    fun winningCard(): Card? = game.winningCardForTrick(this)

    fun getCardPlayedBy(player: CardPlayer): Card? {
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