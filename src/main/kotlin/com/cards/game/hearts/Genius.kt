package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.Player

class Genius(
    player: Player,
    game: Game) : CardPlayer(player, game) {

    override fun chooseCard(): Card {
        return measureCardValues()
            .metaCardList
            .shuffled()
            .maxByOrNull { metaCard -> metaCard.value }!!
            .card
    }

    private val round = game.getCurrentRound()
    private val cardsPlayed  = round
        .getCompletedTrickList()
        .flatMap{ trick -> trick.getCardsPlayed()}
        .union (round.getTrickOnTable().getCardsPlayed())
        .map { cardPlayed -> cardPlayed.card}

    private fun measureCardValues(): MetaCardList {
        val leadColor = game.getCurrentRound().getTrickOnTable().leadColor()
        val legalCards = HeartsRulesBook.legalPlayableCards(getCardsInHand(), leadColor)
        val trickLeadCard = game.getCurrentRound().getTrickOnTable().winningCard()
        val metaCardList = MetaCardList(legalCards, cardsPlayed)

        if (leadColor != null) {
            if (leadColor != legalCards.first().color) {
                metaCardList
                    .evaluateByRank(rankStepValue = 1)
                    .evaluateSpecificCard(Card(CardColor.SPADES, CardRank.QUEEN), 200)
                    .evaluateSpecificCard(Card(CardColor.CLUBS, CardRank.JACK), 100)
                    .evaluateSpecificColor(CardColor.HEARTS, 30)
                    .evaluateHighestCardsInColor(50)
            } else {
                metaCardList
                    .evaluateGivenCardLowerThanOtherCard(Card(CardColor.SPADES, CardRank.QUEEN), trickLeadCard!!, 200 )
                    .evaluateGivenCardLowerThanOtherCard(Card(CardColor.CLUBS, CardRank.JACK), trickLeadCard, 100 )
                    .evaluateByRankLowerThanOtherCard(trickLeadCard, baseValue = 51, rankStepValue = 1)
                    .evaluateByRankHigherThanOtherCard(trickLeadCard, baseValue = 50, rankStepValue = -1)
            }
        }
        return metaCardList
    }
}
