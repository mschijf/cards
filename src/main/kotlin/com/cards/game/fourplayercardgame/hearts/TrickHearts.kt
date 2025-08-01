package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Trick

class TrickHearts(leadPlayer: Player): Trick(leadPlayer) {

    override fun getLegalPlayableCards(cardsList: List<Card>): List<Card> {
        return cardsList
            .filter{ card -> isLeadColor(card.color)}
            .ifEmpty { cardsList }
    }

    override fun getWinner(): Player? {
        return if (!isComplete()) {
            null
        } else {
            getCardsPlayed()
                .filter { playerPlayedCard -> isLeadColor(playerPlayedCard.card.color) }
                .maxByOrNull { playerPlayedCard -> playerPlayedCard.card.toRankNumber() }
                ?.player
        }
    }

    override fun getWinningCard(): Card? {
        return getCardsPlayed()
            .filter { playerPlayedCard -> isLeadColor(playerPlayedCard.card.color) }
            .maxByOrNull { playerPlayedCard -> playerPlayedCard.card.toRankNumber() }
            ?.card
    }

    //score
    fun getScore(): ScoreHearts {
        return if (!isComplete()) {
            ScoreHearts.ZERO
        } else {
            ScoreHearts.scoreForPlayer(getWinner()!!, getCardsPlayed().sumOf { playerPlayedCard -> playerPlayedCard.card.cardValue() })
        }
    }

}