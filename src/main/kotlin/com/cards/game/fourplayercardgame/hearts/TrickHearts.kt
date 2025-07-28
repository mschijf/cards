package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.Trick

class TrickHearts(leadPlayer: Player): Trick(leadPlayer) {

    private fun toRankNumber (card: Card) : Int = card.rank.rankNumber - 7

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
                .maxByOrNull { playerPlayedCard -> toRankNumber(playerPlayedCard.card) }
                ?.player
        }
    }

    override fun winningCard(): Card? {
        return getCardsPlayed()
            .filter { playerPlayedCard -> isLeadColor(playerPlayedCard.card.color) }
            .maxByOrNull { playerPlayedCard -> toRankNumber(playerPlayedCard.card) }
            ?.card
    }
}