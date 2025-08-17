package com.cards.player

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.CardPlayedListener
import com.cards.game.fourplayercardgame.basic.Game
import com.cards.game.fourplayercardgame.basic.TableSide

abstract class Player(
    val tableSide: TableSide,
    protected val game: Game
): CardPlayedListener {

    private var cardsInHand: MutableList<Card> = mutableListOf()

    init {
        game.addCardPlayedListener(this)
    }

    abstract fun chooseCard(): Card

    fun getCardsInHand() = cardsInHand.toList()
    fun getNumberOfCardsInHand() = getCardsInHand().size

    override fun signalCardPlayed(side: TableSide, card: Card) {
        if (!game.isFinished()) {
            if (!game.getCurrentRound().getTrickOnTable().hasNotStarted()) {
                assert (game.getCurrentRound().getTrickOnTable().getCardsPlayed().last() == card)
                assert (game.getCurrentRound().getTrickOnTable().getCardPlayedBy(side) == card)
            } else {
                assert(game.getLastCompletedTrick()!!.getCardsPlayed().last() == card)
                assert(game.getLastCompletedTrick()!!.getCardPlayedBy(side) == card)
            }
        }
    }

    fun setCardsInHand(cardsFromDealer: List<Card>) {
        cardsInHand = cardsFromDealer.toMutableList()
    }

    fun removeCard(card: Card) {
        if (!cardsInHand.remove(card)) {
            throw Exception("cannot remove card $card from hand of player $tableSide ")
        }
    }
    override fun toString() = "pl-$tableSide"
}