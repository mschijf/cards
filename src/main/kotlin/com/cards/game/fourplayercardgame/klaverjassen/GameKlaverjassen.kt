package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Game
import com.cards.game.fourplayercardgame.basic.Round
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.basic.Trick

class GameKlaverjassen(): Game()  {

    override fun createTrick(leadPosition: TablePosition) =
        TrickKlaverjassen(
            leadPosition,
            getCurrentRound() as RoundKlaverjassen
        )
    override fun createRound() = RoundKlaverjassen()

    override fun isFinished(): Boolean {
        return getRounds().size == NUMBER_OF_ROUNDS_PER_GAME && getRounds().last().isComplete()
    }

    //score
    fun getAllScoresPerRound(): List<ScoreKlaverjassen> {
        return getRounds()
            .map { round ->  (round as RoundKlaverjassen).getScore()}
    }
}