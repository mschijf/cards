package com.cards.game.fourplayercardgame.klaverjassen.ai

import com.cards.game.card.CARDDECK
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Table
import com.cards.game.fourplayercardgame.klaverjassen.GameKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.PlayerKlaverjassen

class GeniusPlayerKlaverjassen(
    tablePosition: Table,
    game: GameKlaverjassen) : PlayerKlaverjassen(tablePosition, game) {

    private val trumpChoiceAnalyzer = TrumpChoiceAnalyzer(this)
    private val chooseCardAnalyzer = ChooseCardAnalyzer(this)

    fun getCurrentRound() = game.getCurrentRound()
    fun getOtherPlayers() = game.getPlayerList() - this

    override fun chooseCard(): Card {
//        chooseCardAnalyzer.determinePlayerCanHaveCards().forEach {
//            println("${it.key.tablePosition}: ${it.value}")
//        }
        return super.chooseCard()
    }

    override fun chooseTrumpColor(cardColorOptions: List<CardColor>): CardColor {
        return cardColorOptions.maxBy { cardColor ->
            trumpChoiceAnalyzer.trumpChoiceValue(cardColor)
        }
    }

    private fun getCardsPlayed(): List<Card> {
        val round = game.getCurrentRound()
        return round
            .getCompletedTrickList()
            .flatMap { trick -> trick.getCardsPlayed() }
            .union(round.getTrickOnTable().getCardsPlayed())
            .map { cardPlayed -> cardPlayed.card }
    }

    private fun getCardsStillInPlay(): List<Card> {
        return CARDDECK
            .baseDeckCardsSevenAndHigher
            .minus(getCardsPlayed().toSet())
            .minus(getCardsInHand().toSet())
    }

//    fun getMetaCardList(): HeartsAnalyzer {
//        val trick = game.getCurrentRound().getTrickOnTable()
//        val leadColor = trick.getLeadColor()
//
//        if ( trick.getPlayerToMove() != this )
//            return zeroValued()
//
//        if (trick.isLeadPLayer(this))
//            return evaluateLeadPLayer()
//
//        if (hasColorInHand(leadColor!!))
//            return evaluateFollowerAndCanFollowLeadColor()
//
//        return evaluateFollowerButCannotFollowLeadColor()
//    }
//
//    private fun zeroValued(): HeartsAnalyzer {
//        return HeartsAnalyzer(getCardsInHand())
//    }
//
//    private fun evaluateLeadPLayer(): HeartsAnalyzer {
//        val analyzer = HeartsAnalyzer(getCardsInHand(), getCardsPlayed(), getCardsStillInPlay())
//
//        analyzer
//            .evaluateLeadPlayerByColor(CardColor.HEARTS)
//            .evaluateLeadPlayerByColor(CardColor.DIAMONDS)
//            .evaluateLeadPlayerByColor(CardColor.SPADES)
//            .evaluateLeadPlayerByColor(CardColor.CLUBS)
//        return analyzer
//    }
//
//    private fun evaluateFollowerAndCanFollowLeadColor(): HeartsAnalyzer {
//        val trick = game.getCurrentRound().getTrickOnTable()
//        val leadColor = trick.getLeadColor() ?: throw Exception("Trick on table does not have a lead color")
//        val winningCard = trick.getWinningCard()!!
//        val legalCards = trick.getLegalPlayableCards(getCardsInHand())
//        val analyzer = HeartsAnalyzer(legalCards, getCardsPlayed(), getCardsStillInPlay())
//        if (analyzer.hasOnlyLowerCardsThanLeader(winningCard)) {
//            //throw highest card, especially QS or JC
//            analyzer
//                .evaluateByRank(1)
//                .evaluateSpecificCard(Card(CardColor.SPADES, CardRank.QUEEN), 200)
//                .evaluateSpecificCard(Card(CardColor.CLUBS, CardRank.JACK), 200)
//        } else if (analyzer.hasOnlyHigherCardsThanLeader(winningCard)) {
//            if (trick.isLastPlayerToMove() ) { //or 100% sure that players after you will not beat you
//                //throw highest
//                analyzer.evaluateByRank(1)
//            } else {
//                //throw lowest
//                analyzer.evaluateByRank(-1)
//            }
//            //do not throw QS or JC, unless you're sure that player after you must have only higher
//            analyzer
//                .evaluateSpecificCard(Card(CardColor.SPADES, CardRank.QUEEN), -200)
//                .evaluateSpecificCard(Card(CardColor.CLUBS, CardRank.JACK), -200)
//        } else {
//            if (trick.isLastPlayerToMove() ) {
//                val trickValue = trick.getCardsPlayed().sumOf { cardPlayed -> HEARTS.cardValue(cardPlayed.card) }
//                if (trickValue == 0 && !analyzer.hasAllCardsOfColor(leadColor) && analyzer.canGetRidOfLeadPosition(leadColor)) {
//                    // save to throw the highest card
//                    analyzer
//                        .evaluateByRankHigherThanOtherCard(winningCard, 0, 1)
//                        .evaluateSpecificCard(Card(CardColor.SPADES, CardRank.QUEEN), -200)
//                        .evaluateSpecificCard(Card(CardColor.CLUBS, CardRank.JACK), -200)
//                } else {
//                    //throw the highest card that is lower
//                    analyzer
//                        .evaluateByRankLowerThanOtherCard(winningCard, 0, 1)
//                        .evaluateSpecificCardLowerThanOtherCard(Card(CardColor.SPADES, CardRank.QUEEN), 200, winningCard)
//                        .evaluateSpecificCardLowerThanOtherCard(Card(CardColor.CLUBS, CardRank.JACK), 200, winningCard)
//                }
//            } else {
//                //throw the highest card that is lower
//                analyzer
//                    .evaluateByRankLowerThanOtherCard(winningCard, 0, 1)
//                    .evaluateSpecificCardLowerThanOtherCard(Card(CardColor.SPADES, CardRank.QUEEN), 200, winningCard)
//                    .evaluateSpecificCardLowerThanOtherCard(Card(CardColor.CLUBS, CardRank.JACK), 200, winningCard)
//            }
//            //todo: laagste of een-na-laagste kaart, en ruiten en nog voldoende ruiten in omloop en nog niet eerder gespeeld dan hoge kaart opgooien
//            //todo: kaarten die nooit een slag kunnen halen lager waarderen - bijv. 7,8,9 in hand, dan zijn die weinig waard (vooal bij harten)
//        }
//        return analyzer
//    }
//
//    private fun evaluateFollowerButCannotFollowLeadColor(): HeartsAnalyzer {
//        val trick = game.getCurrentRound().getTrickOnTable()
//        val legalCards = trick.getLegalPlayableCards(getCardsInHand())
//        val analyzer = HeartsAnalyzer(legalCards, getCardsPlayed(), getCardsStillInPlay())
//        analyzer
//            .evaluateByRank(rankStepValue = 1)
//            .evaluateHighestCardsInColor(50)
//            .evaluateSingleCardOfColor(100, CardColor.DIAMONDS)
//            .evaluateSingleCardOfColor(200, CardColor.HEARTS)
//            .evaluateSingleCardOfColor(200, Card(CardColor.SPADES, CardRank.QUEEN))
//            .evaluateSingleCardOfColor(100, CardColor.SPADES)
//            .evaluateSingleCardOfColor(200, Card(CardColor.CLUBS, CardRank.JACK))
//            .evaluateSingleCardOfColor(100, CardColor.CLUBS)
//            .evaluateFreeCards(-300)
//            .evaluateSpecificColor(CardColor.HEARTS, 30)
//            .evaluateSpecificCard(Card(CardColor.SPADES, CardRank.QUEEN), 100)
//            .evaluateSpecificCard(Card(CardColor.CLUBS, CardRank.JACK), 100)
//        return analyzer
//    }

    //------------------------------------------------------------------------------------------------------------------
}
