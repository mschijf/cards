package com.cards.controller.hearts

import com.cards.controller.basic.GameMaster
import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.TableSide
import com.cards.player.Player
import com.cards.game.fourplayercardgame.hearts.GAME_START_PLAYER
import com.cards.game.fourplayercardgame.hearts.GameHearts
import com.cards.game.fourplayercardgame.hearts.legalPlayable
import com.cards.player.hearts.ai.GeniusPlayerHearts

class GameMasterHearts: GameMaster() {

    override fun createAndStartNewGame() = GameHearts.startNewGame(GAME_START_PLAYER)
    override fun getGame() = super.getGame() as GameHearts

    override fun initialPlayerList(): List<Player> {
        return listOf(
            GeniusPlayerHearts(TableSide.WEST, getGame()),
            GeniusPlayerHearts(TableSide.NORTH, getGame()),
            GeniusPlayerHearts(TableSide.EAST, getGame()),
            GeniusPlayerHearts(TableSide.SOUTH, getGame()),
        )
    }

    override fun isLegalCardToPlay(player: Player, card: Card): Boolean {
        val trickOnTable = getGame().getCurrentRound().getTrickOnTable()

        val cardsInHand = player.getCardsInHand()
        val legalCards = cardsInHand.legalPlayable(trickOnTable)
        return legalCards.contains(card)
    }
}