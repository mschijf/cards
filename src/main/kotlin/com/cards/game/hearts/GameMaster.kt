package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardDeck
import com.cards.game.card.CardRank
import java.lang.Exception

class GameMaster {
    private val cardDeck = CardDeck(lowestCardRank = CardRank.SEVEN).shuffle()

    private var leadPlayer = Player.SOUTH

    private val playerList = arrayListOf<HeartsPlayer>(
        HeartsPlayer(player = Player.SOUTH, cardsInHand = cardDeck.getCards(0,8), firstLeadPlayer = leadPlayer),
        HeartsPlayer(player = Player.WEST, cardsInHand = cardDeck.getCards(8,16), firstLeadPlayer = leadPlayer),
        HeartsPlayer(player = Player.NORTH, cardsInHand = cardDeck.getCards(16,24), firstLeadPlayer = leadPlayer),
        HeartsPlayer(player = Player.EAST, cardsInHand = cardDeck.getCards(24,32), firstLeadPlayer = leadPlayer),
    )

    var onTable = Trick(leadPlayer)

    fun getPlayer(player: Player): HeartsPlayer {
        for (heartsPlayer in playerList) {
            if (heartsPlayer.player == player)
                return heartsPlayer
        }
        throw Exception("Cannot Find HeartsPlayer in GameMaster for $player")
    }

    fun playCard(card: Card) {
        onTable.addCard(card)
        if (onTable.isComplete()) {
            leadPlayer = onTable.winner()
            onTable = Trick(leadPlayer)
        }
        //todo: what to do if all cards are played
    }
}