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
        println("                       WIJ        ZIJ")
        val winsNS = serie.count { it.getNorthSouthTotal() > it.getEastWestTotal() }
        val winsEW = serie.count { it.getNorthSouthTotal() < it.getEastWestTotal() }
        println("number of wins: %10d %10d".format(winsNS,winsEW))
        val total = serie.reduce { acc, score -> acc.plus(score) }
        println("Points          %10d %10d".format(total.getNorthSouthTotal(), total.getEastWestTotal()))
    }

    private fun testOneGame(index: Int): ScoreKlaverjassen {
        val gameMaster = GameMasterKlaverjassen()
        val testGame = gameMaster.startNewGame() as GameKlaverjassen

        while (!testGame.isFinished()) {
            val playerToMove = gameMaster.getCardPlayer(testGame.getPositionToMove()) as PlayerKlaverjassen
            if (testGame.getCurrentRound().hasNotStarted()) {
                val trumpColor = playerToMove.chooseTrumpColor()
                testGame.setTrumpColorAndContractOwner(trumpColor, playerToMove.tablePosition)
            }
            val suggestedCardToPlay = playerToMove.chooseCard()
            gameMaster.playCard(suggestedCardToPlay)
        }
        return testGame.getAllScoresPerRound().reduce { acc, roundScore -> acc.plus(roundScore) }
    }

}