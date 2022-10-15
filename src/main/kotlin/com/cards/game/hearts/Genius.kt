package com.cards.game.hearts

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.Player

class Genius(
    player: Player,
    game: Game) : CardPlayer(player, game) {

    override fun chooseCard(): Card {
        return getMetaCardList()
            .metaCardList
            .shuffled()
            .maxByOrNull { metaCard -> metaCard.value }!!
            .card
    }

    private fun getCardsPlayed(): List<Card> {
        val round = game.getCurrentRound()
        return round
            .getCompletedTrickList()
            .flatMap{ trick -> trick.getCardsPlayed()}
            .union (round.getTrickOnTable().getCardsPlayed())
            .map { cardPlayed -> cardPlayed.card}
    }

    private fun getCardsStillInPlay(): List<Card> {
        return game.cardDeck.getCards().minus(getCardsPlayed().toSet()).minus(getCardsInHand())
    }

    fun getMetaCardList(): MetaCardList {
        val trick = game.getCurrentRound().getTrickOnTable()
        val leadColor = trick.leadColor()
        val legalCards = HeartsRulesBook.legalPlayableCards(getCardsInHand(), leadColor)
        val trickLeadCard = game.getCurrentRound().getTrickOnTable().winningCard()
        val metaCardList = MetaCardList(legalCards, getCardsPlayed(), getCardsStillInPlay())

        if (trick.isLeadPLayer(this.player)) {
            if (!trick.isNew())
                return metaCardList
            return metaCardList
        } else {
            if (trick.isNew())
                return metaCardList
            if (hasColorInHand(leadColor!!)) {
                metaCardList
                    .evaluateGivenCardLowerThanOtherCard(Card(CardColor.SPADES, CardRank.QUEEN), trickLeadCard!!, 200)
                    .evaluateGivenCardLowerThanOtherCard(Card(CardColor.CLUBS, CardRank.JACK), trickLeadCard, 100)
                    .evaluateByRankLowerThanOtherCard(trickLeadCard, baseValue = 30, rankStepValue = 1)
                    .evaluateByRankHigherThanOtherCard(trickLeadCard, baseValue = 10, rankStepValue = -1)
                    .evaluateByRankHigherThanOtherCardLastTrickPlayer(trick, trickLeadCard, baseValue = 50, rankStepValue = 1)
                //hasLowCard of een-na-laageste kaart, en ruiten en nog voldoende ruiten in omloop en nog niet eerder gespeeld
                // dan hoge kaart opgooien
                // note: evaluateByRankHigherThanOtherCardLastTrickPlayer en bonvestaande regel moeten er ook voorzorgen
                // dat de lage kaart een hoge waarde krijgt in de 'als leadplayer' fase

                //als ik m moet pakken, dan maar met de hoogste (achterhand)
                // als schoppen vrouw en er is nog geen hogere in de trick, dan vrouw afwaarderen
            } else {
                //'vrije' kaarten waarderen (hoeven niet noodzakelijk) als vuil te worden gezien
                // kale hoge kaart, eerder weg, dan gedekte schoppen aas (of hartenkaart) ==> bepaal hoe je kan duiken met een kleur
                metaCardList
                    .evaluateByRank(rankStepValue = 1)
                    .evaluateSpecificCard(Card(CardColor.SPADES, CardRank.QUEEN), 200)
                    .evaluateSpecificCard(Card(CardColor.CLUBS, CardRank.JACK), 100)
                    .evaluateSpecificColor(CardColor.HEARTS, 30)
                    .evaluateHighestCardsInColor(50)
                    // bovenstaande wordt overgewaardeerd als kaarten vrij zijn (geen kaarten meer van deze kleur)
            }
        }
        return metaCardList
    }

}
