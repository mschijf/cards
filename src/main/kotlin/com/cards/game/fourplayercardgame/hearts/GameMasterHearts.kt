package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.GameMaster
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.hearts.ai.GeniusPlayerHearts

class GameMasterHearts: GameMaster() {

    override fun createGame() = GameHearts()
    override fun getGame() = super.getGame() as GameHearts

    override fun initialPlayerList(): List<Player> {
        return listOf(
            GeniusPlayerHearts(TablePosition.WEST, getGame()),
            GeniusPlayerHearts(TablePosition.NORTH, getGame()),
            GeniusPlayerHearts(TablePosition.EAST, getGame()),
            GeniusPlayerHearts(TablePosition.SOUTH, getGame()),
        )
    }

    override fun isLegalCardToPlay(player: Player, card: Card): Boolean {
        val trickOnTable = getGame().getCurrentRound().getTrickOnTable()

        val cardsInHand = player.getCardsInHand()
        val legalCards = cardsInHand.legalPlayable(trickOnTable)
        return legalCards.contains(card)
    }
}