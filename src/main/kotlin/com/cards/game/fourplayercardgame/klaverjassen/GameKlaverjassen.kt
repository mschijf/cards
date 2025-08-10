package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Game
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Round
import com.cards.game.fourplayercardgame.basic.Table
import com.cards.game.fourplayercardgame.klaverjassen.ai.GeniusPlayerKlaverjassen
import com.cards.tools.RANDOMIZER

class GameKlaverjassen(): Game()  {

    override fun initialPlayerList(): List<Player> {
//        return Table.values().map { p -> GeniusPlayerKlaverjassen(p, this) }
        return listOf(
            PlayerKlaverjassen(Table.WEST, this),
            GeniusPlayerKlaverjassen(Table.NORTH, this),
            PlayerKlaverjassen(Table.EAST, this),
            GeniusPlayerKlaverjassen(Table.SOUTH, this),
        )
    }

    override fun createFirstRound(): Round {
        return RoundKlaverjassen(getCardPlayer(VERY_FIRST_START_PLAYER))
    }

    override fun createNextRound(previousRound: Round): Round {
//        printLastRoundPlayed()
        return RoundKlaverjassen(previousRound.getLeadPlayer().nextPlayer())
    }

    override fun isFinished(): Boolean {
        return getCompleteRoundsPlayed().size == NUMBER_OF_ROUNDS_PER_GAME
    }

    private fun printLastRoundPlayed() {
        print("[Seed: ${RANDOMIZER.getLastSeedUsed()}]  Round: ")
        getCompleteRoundsPlayed().last().getCompletedTrickList().forEach { trick->
            print("[")
            getPlayerList().map { pl -> trick.getCardsPlayed().first{ cp -> cp.player == pl }}.forEach { cardPlayed ->
                    if (cardPlayed.player == trick.getLeadPlayer())
                        print("(")
                    print("${cardPlayed.card}")
                    if (cardPlayed.player == trick.getLeadPlayer())
                        print(")")
                    if (getPlayerList().last() != cardPlayed.player)
                        print(",")
                }
            print("]  ")
        }
        println()
    }

    fun setTrumpColorAndContractOwner(trumpColor: CardColor, contractOwner: Player) {
        (getCurrentRound() as RoundKlaverjassen).setTrumpColorAndContractOwner(trumpColor, contractOwner)
    }

    //score
    fun getAllScoresPerRound(): List<ScoreKlaverjassen> {
        return (getCompleteRoundsPlayed() + getCurrentRound())
            .map { round ->  (round as RoundKlaverjassen).getScore()}
    }
}