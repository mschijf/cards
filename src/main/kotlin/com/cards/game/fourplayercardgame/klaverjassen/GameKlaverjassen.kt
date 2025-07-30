package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.fourplayercardgame.basic.*

class GameKlaverjassen(): Game()  {

    override fun initialPlayerList(): List<Player> {
        return Table.values().map { p -> PlayerKlaverjassen(p, this) }
    }

    override fun createFirstRound(): Round {
        return RoundKlaverjassen(getCardPlayer(Table.WEST), this)
    }

    override fun createNextRound(previousRound: Round): Round {
        return RoundKlaverjassen(previousRound.getLeadPlayer().nextPlayer(), this)
    }

    override fun isFinished(): Boolean {
        return getCompleteRoundsPlayed().size == KLAVERJASSEN.NUMBER_OF_ROUNDS_PER_GAME
    }

    //score
    fun getCumulativeScorePerRound(): List<Score> {
        return getCompleteRoundsPlayed()
            .map { round ->  round.getScore()}
            .runningFold(Score.ZERO) { acc, sc -> acc.plus(sc) }.drop(1)
    }
}