package com.cards.game.fourplayercardgame.klaverjassen

import org.junit.jupiter.api.Test

class GeniusPlayerQualityTest {
    @Test
    fun runTest() {
        val numberOfTests = 100
        val serie = (1..numberOfTests).map { testOneGame() }
        println()
        println("----------------------------------------------------------------")
        println("                       WIJ        ZIJ")
        val winsNS = serie.count { it.getNorthSouthTotal() > it.getEastWestTotal() }
        val winsEW = serie.count { it.getNorthSouthTotal() < it.getEastWestTotal() }
        println("number of wins: %10d %10d".format(winsNS,winsEW))
        val total = serie.reduce { acc, score -> acc.plus(score) }
        println("Points          %10d %10d".format(total.getNorthSouthTotal(), total.getEastWestTotal()))
    }

    private fun testOneGame(): ScoreKlaverjassen {
        val testGame = GameKlaverjassen()

        while (!testGame.isFinished()) {
            val playerToMove = testGame.getPlayerToMove() as PlayerKlaverjassen
            if (testGame.getCurrentRound().hasNotStarted()) {
                val trumpColor = playerToMove.chooseTrumpColor()
                testGame.setTrumpColorAndContractOwner(trumpColor, playerToMove)
            }
            val suggestedCardToPlay = playerToMove.chooseCard()
            testGame.playCard(suggestedCardToPlay)
        }
        return testGame.getAllScoresPerRound().reduce { acc, roundScore -> acc.plus(roundScore) }
    }

}