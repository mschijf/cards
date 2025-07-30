package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Game
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Round
import com.cards.game.fourplayercardgame.basic.Table

class GameKlaverjassen(): Game()  {

    override fun initialPlayerList(): List<Player> {
        return Table.values().map { p -> PlayerKlaverjassen(p, this) }
    }

    override fun createFirstRound(): Round {
        return RoundKlaverjassen(getCardPlayer(KLAVERJASSEN.VERY_FIRST_START_PLAYER), this)
    }

    override fun createNextRound(previousRound: Round): Round {
        return RoundKlaverjassen(previousRound.getLeadPlayer().nextPlayer(), this)
    }

    override fun isFinished(): Boolean {
        return getCompleteRoundsPlayed().size == KLAVERJASSEN.NUMBER_OF_ROUNDS_PER_GAME
    }

    fun setTrumpColorAndContractOwner(trumpColor: CardColor, contractOwner: Player) {
        (getCurrentRound() as RoundKlaverjassen).setTrumpColorAndContractOwner(trumpColor, contractOwner)
    }

    //score
    fun getAllScoresPerRound(): List<ScoreKlaverjassen> {
        return getCompleteRoundsPlayed()
            .map { round ->  (round as RoundKlaverjassen).getScore()}
    }
}