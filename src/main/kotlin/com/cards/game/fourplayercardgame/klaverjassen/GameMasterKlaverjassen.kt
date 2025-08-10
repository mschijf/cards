package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.GameMaster
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.klaverjassen.ai.GeniusPlayerKlaverjassen

class GameMasterKlaverjassen: GameMaster() {


    override fun createGame() = GameKlaverjassen()

    override fun initialPlayerList(): List<Player> {
        return listOf(
            PlayerKlaverjassen(TablePosition.WEST, getGame() as GameKlaverjassen),
            GeniusPlayerKlaverjassen(TablePosition.NORTH, getGame() as GameKlaverjassen),
            PlayerKlaverjassen(TablePosition.EAST, getGame() as GameKlaverjassen),
            GeniusPlayerKlaverjassen(TablePosition.SOUTH, getGame() as GameKlaverjassen),
        )
    }

    override fun isLegalCardToPlay(player: Player, card: Card): Boolean {
        val trickOnTable = getGame().getCurrentRound().getTrickOnTable()

        val legalCards = player
            .getCardsInHand()
            .legalPlayable(
                trickOnTable,
                (getGame().getCurrentRound() as RoundKlaverjassen).getTrumpColor()
            )
        return legalCards.contains(card)
    }
}