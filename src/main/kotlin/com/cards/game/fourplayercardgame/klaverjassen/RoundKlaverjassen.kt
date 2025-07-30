package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Round
import com.cards.game.fourplayercardgame.basic.Trick


class RoundKlaverjassen(
    leadPlayer: Player,
    private val game: GameKlaverjassen) : Round(leadPlayer) {

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

    fun getScore(): ScoreKlaverjassen {
        var score = ScoreKlaverjassen.ZERO
        if (isComplete()) {
            getCompletedTrickList().forEach { trick ->
//                println((trick as TrickKlaverjassen).getScore())
                score = score.plus((trick as TrickKlaverjassen).getScore())
            }
        }
        return score
    }
}