package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Round
import com.cards.game.fourplayercardgame.basic.Table
import com.cards.game.fourplayercardgame.basic.Trick


class RoundKlaverjassen(leadPlayer: Player) : Round(leadPlayer) {

    private var trumpColor: CardColor = CardColor.CLUBS
    private var contractOwner: Player = getLeadPlayer()

    fun getTrumpColor() = trumpColor
    fun getContractOwner() = contractOwner

    fun setTrumpColorAndContractOwner(trumpColor: CardColor, contractOwner: Player) {
        this.trumpColor= trumpColor
        this.contractOwner = contractOwner
    }

    override fun createTrick(leadPlayer: Player): Trick {
        return TrickKlaverjassen(leadPlayer, this)
    }

    override fun isComplete(): Boolean {
        return completedTricksPlayed() >= KLAVERJASSEN.NUMBER_OF_TRICKS_PER_ROUND
    }

    fun isLastTrick(trick: Trick): Boolean {
        return if (getCompletedTrickList().size == KLAVERJASSEN.NUMBER_OF_TRICKS_PER_ROUND && getCompletedTrickList().last() == trick) {
            true
        } else if (getCompletedTrickList().size == KLAVERJASSEN.NUMBER_OF_TRICKS_PER_ROUND-1 && getTrickOnTable() == trick) {
            true
        } else {
            false
        }
    }

    private fun allTricksWonByTeam(team: Set<Table>): Boolean {
        return getCompletedTrickList().all { trick -> trick.getWinner()!!.tablePosition in team }
    }

//    private fun natScore(roundScore: ScoreKlaverjassen, northSouth: Boolean): ScoreKlaverjassen {
//        return ScoreKlaverjassen(
//            roundScore.northSouthPoints,0,roundScore.eastWest,0,
//            roundScore.northSouthBonus, 0, roundScore.eastWestBonus, 0,
//            scoreType = ScoreType.NAT
//        )
//    }
//
//    private fun pitScore(roundScore: ScoreKlaverjassen): ScoreKlaverjassen {
//    }
//
    fun getScore(): ScoreKlaverjassen {
        if (!isComplete())
            return ScoreKlaverjassen.ZERO

        val roundScore= getCompletedTrickList()
            .fold(ScoreKlaverjassen.ZERO){acc, trick -> acc.plus((trick as TrickKlaverjassen).getScore())}

        if (getContractOwner().tablePosition in setOf(Table.NORTH, Table.SOUTH)) {

            if (roundScore.getNorthSouthTotal() <= roundScore.getEastWestTotal()) //Nat
                return ScoreKlaverjassen(
                    roundScore.northSouthPoints + roundScore.eastWestPoints,
                    0,
                    roundScore.northSouthBonus + roundScore.eastWestBonus,
                    0,
                    scoreType = ScoreType.NAT
                )

            if (allTricksWonByTeam(setOf(Table.NORTH, Table.SOUTH))) {
                return ScoreKlaverjassen(
                    roundScore.eastWestPoints,
                    roundScore.northSouthPoints,
                    roundScore.eastWestBonus,
                    roundScore.northSouthBonus + KLAVERJASSEN.PIT_BONUS,
                    scoreType = ScoreType.PIT
                )
            }
            return roundScore

        } else {

            if (roundScore.getEastWestTotal() <= roundScore.getNorthSouthTotal()) //Nat
                return ScoreKlaverjassen(
                    0,
                    roundScore.northSouthPoints + roundScore.eastWestPoints,
                    0,
                    roundScore.northSouthBonus + roundScore.eastWestBonus,
                    scoreType = ScoreType.NAT
                )

            if (allTricksWonByTeam(setOf(Table.EAST, Table.WEST))) {
                return ScoreKlaverjassen(
                    roundScore.eastWestPoints,
                    roundScore.northSouthPoints,
                    roundScore.eastWestBonus + KLAVERJASSEN.PIT_BONUS,
                    roundScore.northSouthBonus,
                    scoreType = ScoreType.PIT
                )
            }

            return roundScore
        }
    }
}