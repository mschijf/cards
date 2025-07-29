package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Score
import com.cards.game.fourplayercardgame.basic.Trick

class TrickHearts(leadPlayer: Player): Trick(leadPlayer) {

    override fun legalPlayableCards(cardsList: List<Card>): List<Card> {
        return cardsList
            .filter{ card -> isLeadColor(card.color)}
            .ifEmpty { cardsList }
    }

    override fun winner(): Player? {
        return if (!isComplete()) {
            null
        } else {
            getCardsPlayed()
                .filter { playerPlayedCard -> isLeadColor(playerPlayedCard.card.color) }
                .maxByOrNull { playerPlayedCard -> HEARTS.toRankNumber(playerPlayedCard.card) }
                ?.player
        }
    }

    override fun winningCard(): Card? {
        return getCardsPlayed()
            .filter { playerPlayedCard -> isLeadColor(playerPlayedCard.card.color) }
            .maxByOrNull { playerPlayedCard -> HEARTS.toRankNumber(playerPlayedCard.card) }
            ?.card
    }

    //score
    override fun getScore(): Score {
        return if (!isComplete()) {
            Score.ZERO
        } else {
            Score.scoreForPlayer(winner()!!, getCardsPlayed().sumOf { playerPlayedCard -> HEARTS.cardValue(playerPlayedCard.card) })
        }
    }

}