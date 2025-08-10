package com.cards.game.fourplayercardgame.basic

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Table.*

abstract class Player(
    val tablePosition: Table,
    protected val game: Game) {

    private var cardsInHand: MutableList<Card> = mutableListOf()

    abstract fun chooseCard(): Card

    fun getCardsInHand() = cardsInHand.toList()
    fun getNumberOfCardsInHand() = getCardsInHand().size
    fun hasColorInHand(color: CardColor) = cardsInHand.any { card -> card.color == color }

    fun setCardsInHand(cardsFromDealer: List<Card>) {
        cardsInHand = cardsFromDealer.toMutableList()
    }

    fun removeCard(card: Card) {
        if (!cardsInHand.remove(card)) {
            throw Exception("cannot remove card $card from hand of player $tablePosition ")
        }
    }

    fun getOtherPlayers() = game.getPlayerList() - this

    fun nextPlayer(): Player {
        return when(this.tablePosition) {
            SOUTH -> game.getCardPlayer(WEST)
            WEST -> game.getCardPlayer(NORTH)
            NORTH -> game.getCardPlayer(EAST)
            EAST -> game.getCardPlayer(SOUTH)
        }
    }

    fun previousPlayer(): Player {
        return when(this.tablePosition) {
            SOUTH -> game.getCardPlayer(EAST)
            WEST -> game.getCardPlayer(SOUTH)
            NORTH -> game.getCardPlayer(WEST)
            EAST -> game.getCardPlayer(NORTH)
        }
    }

    override fun toString() = "pl-$tablePosition"
}