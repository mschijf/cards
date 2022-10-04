package com.cards.game.hearts

import com.cards.game.card.Card

class Trick(
    private val leadPlayer: Player) {

    private val cardsPlayed = arrayListOf<Pair<Player, Card>>()
    var playerToMove = leadPlayer
        private set

    fun getCardPlayedBy(player: Player): Card? = cardsPlayed.firstOrNull { p -> p.first == player }?.second

    fun isComplete(): Boolean = cardsPlayed.size >= 4

    fun winner(): Player {
        if (cardsPlayed.size == 0)
            return leadPlayer

        val leadingColor = cardsPlayed[0].second.color
        return cardsPlayed.filter { f -> f.second.color == leadingColor }.maxByOrNull { f -> HeartsRulesBook.toRankNumber(f.second) }!!.first
    }

    fun addCard(aCard: Card) {
        if (isComplete())
            throw Exception("Adding a card to a completed trick")

        cardsPlayed.add(Pair(playerToMove, aCard))
        playerToMove = playerToMove.nextPlayer()
    }

    fun leadColor() = getCardPlayedBy(leadPlayer)?.color

}