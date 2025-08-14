package com.cards.controller.klaverjassen

import com.cards.controller.basic.GameMaster
import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.TableSide
import com.cards.game.fourplayercardgame.basic.player.Player
import com.cards.game.fourplayercardgame.klaverjassen.GameKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.RoundKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.legalPlayable
import com.cards.game.fourplayercardgame.klaverjassen.player.PlayerKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.player.ai.GeniusPlayerKlaverjassen

class GameMasterKlaverjassen: GameMaster() {

    override fun createGame() = GameKlaverjassen()
    override fun getGame() = super.getGame() as GameKlaverjassen

    override fun initialPlayerList(): List<Player> {
//        RANDOMIZER.setSeed(1611283605)
        return listOf(
            PlayerKlaverjassen(TableSide.WEST, getGame()),
            GeniusPlayerKlaverjassen(TableSide.NORTH, getGame()),
            PlayerKlaverjassen(TableSide.EAST, getGame()),
            GeniusPlayerKlaverjassen(TableSide.SOUTH, getGame()),
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

    fun isNewTrumpNeeded(): Boolean {
        return getGame().getCurrentRound().hasNotStarted()
    }

    fun determineNewTrump() {
        val playerToMove = getCardPlayer(getGame().getSideToMove()) as PlayerKlaverjassen
        val trumpColor = playerToMove.chooseTrumpColor()
        (getGame().getCurrentRound() as RoundKlaverjassen).setTrumpColorAndContractOwner(trumpColor, playerToMove.tableSide)
    }


}