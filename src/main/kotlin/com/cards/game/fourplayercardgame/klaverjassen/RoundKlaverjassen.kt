package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Round
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.basic.Trick


class RoundKlaverjassen() : Round() {

    private var trumpColor: CardColor = CardColor.CLUBS
    private var contractOwner: TablePosition = TablePosition.WEST

    fun getTrumpColor() = trumpColor
    fun getContractOwner() = contractOwner
    fun isContractOwner(tablePosition: TablePosition) = (contractOwner == tablePosition)

    fun setTrumpColorAndContractOwner(trumpColor: CardColor, contractOwner: TablePosition) {
        this.trumpColor= trumpColor
        this.contractOwner = contractOwner
    }

    override fun isComplete(): Boolean {
        return getTrickList().size == NUMBER_OF_TRICKS_PER_ROUND && getTrickList().last().isComplete()
    }

    fun isLastTrick(trick: Trick): Boolean {
        return if (getTrickList().size == NUMBER_OF_TRICKS_PER_ROUND && getTrickList().last() == trick) {
            true
        } else {
            false
        }
    }

    private fun allTricksWonByTeam(team: Set<TablePosition>): Boolean {
        return getTrickList().all { trick -> trick.getWinner()!! in team }
    }

    fun getScore(): ScoreKlaverjassen {
        val roundScore= getTrickList()
            .fold(ScoreKlaverjassen.ZERO){acc, trick -> acc.plus((trick as TrickKlaverjassen).getScore())}

        if (!isComplete())
            return roundScore

        return if (getContractOwner() in setOf(TablePosition.NORTH, TablePosition.SOUTH)) {
            if (roundScore.getNorthSouthTotal() <= roundScore.getEastWestTotal())
                roundScore.changeNorthSouthToNat()
            else if (allTricksWonByTeam(setOf(TablePosition.NORTH, TablePosition.SOUTH)))
                roundScore.plusPitBonus()
            else
                roundScore
        } else {
            if (roundScore.getEastWestTotal() <= roundScore.getNorthSouthTotal())
                roundScore.changeEastWestToNat()
            else if (allTricksWonByTeam(setOf(TablePosition.EAST, TablePosition.WEST)))
                roundScore.plusPitBonus()
            else
                roundScore
        }
    }
}