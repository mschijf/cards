package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardDeck

class GameMaster {
    private val cardDeck = CardDeck().shuffle()
    private var leadPlayer = Player.SOUTH
    val maxCardsInHand = cardDeck.numberOfCards() / Player.values().size

    private val playerList = Player.values().mapIndexed { i, p  ->
        HeartsPlayer(p, cardDeck.getCards(maxCardsInHand*i, maxCardsInHand).toMutableList(), leadPlayer) }
    var trickOnTable = Trick(leadPlayer)

    private var currentDeal = Deal(maxCardsInHand)
    private val game = Game()

    fun getHeartsPlayer(player: Player) = playerList.first { p -> p.player == player }

    fun playCard(card: Card) {
        playCard(trickOnTable.playerToMove, card)
    }

    fun playCard(player: Player, card: Card) {
        //todo --> send notification to all players that a card has been played

        if (!legalCardToPlay(player, card)) {
            throw Exception("trying to play an illegal card: Card($card)")
        }
        trickOnTable.addCard(card)
        getHeartsPlayer(player).removeCard(card)
        if (trickOnTable.isComplete()) {
            currentDeal.addTrick(trickOnTable)
            leadPlayer = trickOnTable.winner()
            trickOnTable = Trick(leadPlayer)

            if (currentDeal.isComplete()) {
                game.addDeal(currentDeal)
                currentDeal = Deal(maxCardsInHand)
            }
        }
    }

    fun legalCardToPlay(player: Player, card: Card): Boolean {
        if (trickOnTable.playerToMove != player)
            return false

        if (trickOnTable.leadColor() == null)
            return true
        val leadColor = trickOnTable.leadColor()
        if (leadColor == card.color)
            return true
        return getHeartsPlayer(player).cardsInHand.none { c -> c.color == leadColor }
    }
}