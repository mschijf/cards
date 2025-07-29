package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Game
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Round
import com.cards.game.fourplayercardgame.basic.Score
import com.cards.game.fourplayercardgame.basic.Table

class GameKlaverjassen(): Game()  {

    //todo: trump setting in Round (no longer as parameter)?
    private var trumpColor = CardColor.CLUBS

    fun getTrumpColor() = trumpColor
    fun getContractOwner() = getCardPlayer(Table.WEST)

    override fun initialPlayerList(): List<Player> {
        return Table.values().map { p -> PlayerKlaverjassen(p, this) }
    }

    override fun createFirstRound(): Round {
        return RoundKlaverjassen(getCardPlayer(Table.WEST), CardColor.CLUBS)
    }

    override fun createNextRound(previousRound: Round): Round {
        return RoundKlaverjassen(previousRound.getLeadPlayer().nextPlayer(), trumpColor)
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