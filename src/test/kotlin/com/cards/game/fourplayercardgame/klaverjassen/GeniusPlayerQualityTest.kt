package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.tools.RANDOMIZER
import org.junit.jupiter.api.Test

class GeniusPlayerQualityTest {
    @Test
    fun runTest() {
        RANDOMIZER.setFixedSequence(true)
        val numberOfTests = 1000
        val serie = (1..numberOfTests).map { testOneGame(it) }
        println()
        println("----------------------------------------------------------------")
        println("%7d runs           WIJ        ZIJ".format(numberOfTests))
        val winsNS = serie.count { it.getNorthSouthTotal() > it.getEastWestTotal() }
        val winsEW = serie.count { it.getNorthSouthTotal() < it.getEastWestTotal() }
        println("number of wins: %10d %10d".format(winsNS,winsEW))
        val total = serie.reduce { acc, score -> acc.plus(score) }
        println("Points          %10d %10d".format(total.getNorthSouthTotal(), total.getEastWestTotal()))

        println()
        println("----------------------------------------------------------------")
        println("EXPECTED:")
        println("----------------------------------------------------------------")
        println("%7d runs           WIJ        ZIJ".format(1000))
        println("number of wins: %10d %10d".format(973, 27))
        println("Points          %10d %10d".format(2164702, 1031998))
    }

    private fun testOneGame(index: Int): ScoreKlaverjassen {
        val gameMaster = GameMasterKlaverjassen()
        gameMaster.startNewGame()

        while (!gameMaster.isGameFinished()) {
            if (gameMaster.isNewTrumpNeeded()) {
                gameMaster.determineNewTrump()
            }
            val playerToMove = gameMaster.getPlayerToMove() as PlayerKlaverjassen
            val suggestedCardToPlay = playerToMove.chooseCard()
            gameMaster.playCard(suggestedCardToPlay)
        }
        return gameMaster.getGame().getAllScoresPerRound().reduce { acc, roundScore -> acc.plus(roundScore) }
    }

}