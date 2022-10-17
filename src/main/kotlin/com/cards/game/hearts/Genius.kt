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
        return HeartsRules.cardDeck.getCards().minus(getCardsPlayed().toSet()).minus(getCardsInHand().toSet())
    }

    fun getMetaCardList(): MetaCardList {
        val trick = game.getCurrentRound().getTrickOnTable()
        val leadColor = trick.leadColor()

        if ( trick.playerToMove() != this.player )
            return MetaCardList(getCardsInHand())

        if (trick.isLeadPLayer(this.player))
            return MetaCardList(getCardsInHand())

        if (hasColorInHand(leadColor!!)) {
            return followerAndCanFollowLeadColor(leadColor)
        } else {
            return followerButCannotFollowLeadColor(leadColor)
        }
    }

    private fun followerAndCanFollowLeadColor(leadColor: CardColor): MetaCardList {
        val trick = game.getCurrentRound().getTrickOnTable()
        val winningCard = trick.winningCard()!!
        val legalCards = HeartsRules.legalPlayableCards(getCardsInHand(), leadColor)
        val metaCardList = MetaCardList(legalCards, getCardsPlayed(), getCardsStillInPlay())
        if (hasOnlyLowerCardsThanLeader(winningCard)) {
            //throw highest card, but not QS or JC
            metaCardList
                .evaluateByRank(1)
                .evaluateSpecificCard(Card(CardColor.SPADES, CardRank.QUEEN), 200)
                .evaluateSpecificCard(Card(CardColor.CLUBS, CardRank.JACK), 200)
        } else if (hasOnlyHigherCardsThanLeader(winningCard)) {
            if (trick.isLastPlayerToMove() ) { //or 100% sure that players after you will not beat you
                //throw highest
                metaCardList.evaluateByRank(1)
            } else {
                //throw lowest
                metaCardList.evaluateByRank(-1)
            }
            //do not throw QS or JC, unless you;re sure that player after you must have only higher
            metaCardList
                .evaluateSpecificCard(Card(CardColor.SPADES, CardRank.QUEEN), -200)
                .evaluateSpecificCard(Card(CardColor.CLUBS, CardRank.JACK), -200)
        } else {
            if (trick.isLastPlayerToMove() ) {
                if (trick.getValue() == 0 && !hasAllCardsOfColor(leadColor) && canGetRidOfLeadPosition(leadColor)) {
                    // save to throw the highest card
                    metaCardList
                        .evaluateByRankHigherThanOtherCard(winningCard, 0, 1)
                        .evaluateSpecificCard(Card(CardColor.SPADES, CardRank.QUEEN), -200)
                        .evaluateSpecificCard(Card(CardColor.CLUBS, CardRank.JACK), -200)
                } else {
                    //throw the highest card that is lower
                    metaCardList
                        .evaluateByRankLowerThanOtherCard(winningCard, 0, 1)
                        .evaluateSpecificCard(Card(CardColor.SPADES, CardRank.QUEEN), 200)
                        .evaluateSpecificCard(Card(CardColor.CLUBS, CardRank.JACK), 200)
                }
            } else {
                //throw the highest card that is lower
                metaCardList
                    .evaluateByRankLowerThanOtherCard(winningCard, 0, 1)
                    .evaluateSpecificCard(Card(CardColor.SPADES, CardRank.QUEEN), 200)
                    .evaluateSpecificCard(Card(CardColor.CLUBS, CardRank.JACK), 200)
            }
            //laagste of een-na-laagste kaart, en ruiten en nog voldoende ruiten in omloop en nog niet eerder gespeeld dan hoge kaart opgooien
        }
        return metaCardList
    }

    private fun followerButCannotFollowLeadColor(leadColor: CardColor): MetaCardList {
        val legalCards = HeartsRules.legalPlayableCards(getCardsInHand(), leadColor)
        val metaCardList = MetaCardList(legalCards, getCardsPlayed(), getCardsStillInPlay())
        //'vrije' kaarten waarderen (hoeven niet noodzakelijk) als vuil te worden gezien
        // kale hoge kaart, eerder weg, dan gedekte schoppen aas (of hartenkaart) ==> bepaal hoe je kan duiken met een kleur
        metaCardList
            .evaluateByRank(rankStepValue = 1)
            .evaluateSpecificCard(Card(CardColor.SPADES, CardRank.QUEEN), 200)
            .evaluateSpecificCard(Card(CardColor.CLUBS, CardRank.JACK), 100)
            .evaluateSpecificColor(CardColor.HEARTS, 30)
            .evaluateHighestCardsInColor(50)
        // bovenstaande wordt overgewaardeerd als kaarten vrij zijn (geen kaarten meer van deze kleur)
        return metaCardList
    }

    private fun hasOnlyLowerCardsThanLeader(winningCard: Card): Boolean {
        return getCardsInHand().none { crd -> HeartsRules.toRankNumber(crd) > HeartsRules.toRankNumber(winningCard) }
    }
    private fun hasOnlyHigherCardsThanLeader(winningCard: Card): Boolean {
        return getCardsInHand().none { crd -> HeartsRules.toRankNumber(crd) < HeartsRules.toRankNumber(winningCard) }
    }
    private fun hasAllCardsOfColor(color: CardColor): Boolean {
        return (getCardsInHand().count { cp -> cp.color == color } + getCardsPlayed().count { cp -> cp.color == color } == 8)
    }
    private fun canGetRidOfLeadPosition(leadColor: CardColor): Boolean {
        return hasLowestCardOfColorInHand(leadColor)
    }

    private fun hasLowestCardOfColorInHand(color: CardColor): Boolean {
        val lowestCardInHand = lowestCardOfColorInCardList(getCardsInHand(), color)
        val lowestCardStillInPlay = lowestCardOfColorInCardList(getCardsStillInPlay(), color)
        if (lowestCardStillInPlay == null || lowestCardInHand == null)
            return false
        return HeartsRules.toRankNumber(lowestCardInHand) < HeartsRules.toRankNumber(lowestCardStillInPlay)
    }
    private fun lowestCardOfColorInCardList(cardList: List<Card>, color: CardColor): Card? {
        return cardList
            .filter { c -> c.color == color}
            .minByOrNull { c -> HeartsRules.toRankNumber(c) }
    }

}
