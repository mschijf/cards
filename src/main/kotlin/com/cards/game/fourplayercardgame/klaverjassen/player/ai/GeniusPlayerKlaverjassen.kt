package com.cards.game.fourplayercardgame.klaverjassen.player.ai

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.TableSide
import com.cards.game.fourplayercardgame.klaverjassen.*
import com.cards.game.fourplayercardgame.klaverjassen.player.PlayerKlaverjassen

class GeniusPlayerKlaverjassen(
    tableSide: TableSide,
    game: GameKlaverjassen
) : PlayerKlaverjassen(tableSide, game) {

    private val chooseCardAnalyzer = ChooseCardAnalyzer(this)

    fun printAnalyzer() {
//        chooseCardAnalyzer.refreshAnalysis()
//
//        println()
//        game.getPlayerList().forEach {
//            val playerCanHaveCards = chooseCardAnalyzer.playerCanHaveCards(it)
//            print(String.format("%-5s ", it.tableSide.toString().lowercase()))
//            print(String.format("(%2d): ", playerCanHaveCards.size))
//            CardColor.values().forEach { color ->
//                print(String.format("%-7s: %-25s  ", color, playerCanHaveCards.filter{it.color == color}.map { it.rank.rankString }))
//            }
//            println()
//        }
    }

    override fun chooseCard(): Card {
        val trumpColor = (game.getCurrentRound() as RoundKlaverjassen).getTrumpColor()
        val legalCards = getCardsInHand().legalPlayable(game.getCurrentRound().getTrickOnTable(), trumpColor)
        if (legalCards.size == 1)
            return legalCards.first()

        chooseCardAnalyzer.refreshAnalysis()

        if (firstTrick() && isContractOwner() && isLeadPlayer() && hasTrumpJack()) {
            return trumpJack()
        }

        if (!firstPlayer() && canFollow()) {
            val (card, _) = tryPlay(getCurrentRound().getTrickOnTable() as TrickKlaverjassen, this.tableSide, true)
            return card?:throw Exception("Null value calculated by tryPlay")
        }

        // ALS IK SLAG LEADER BEN
        //  REGELS MAKEN PER 1e ronde, 2e ronde, etc.
        //     wil ik troef trekken?
        //     en kan ik troef trekken?
        //     of wil ik troef voor laatste slag bewaren?

        //ALS IK TWEEDE SPELER BEN
        //  kan follow no-trump color
        //  kan follow trump color
        //  cannot follow no-trump color but have trumps
        //  cannot follow no-trump color and have no trumps

        //ALS IK DERDE SPELER BEN
        //  kan follow no-trump color
        //  kan follow trump color
        //  cannot follow no-trump color but have trumps
        //  cannot follow no-trump color and have no trumps

        //ALS IK VIERDE SPELER BEN
        //  kan follow no-trump color
        //  kan follow trump color
        //  cannot follow no-trump color but have trumps
        //  cannot follow no-trump color and have no trumps

        return super.chooseCard()
    }

    override fun chooseTrumpColor(cardColorOptions: List<CardColor>): CardColor {
        val trumpChoiceAnalyzer = TrumpChoiceAnalyzer(this.getCardsInHand())

        return cardColorOptions.maxBy { cardColor ->
            trumpChoiceAnalyzer.trumpChoiceValue(cardColor)
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private fun tryPlay(trickSoFar: TrickKlaverjassen, side: TableSide, maxNode: Boolean): CardPlayedValue {
        if (trickSoFar.isComplete()) {
            return CardPlayedValue(null, trickSoFar.getScore().getDeltaForPlayer(this.tableSide))
        }

        val legalCards = if (side == this.tableSide) {
            getCardsInHand().legalPlayable(trickSoFar, trump())
        } else {
            val possibleCards = (
                    chooseCardAnalyzer.playerCanHaveCards(side) +
                    chooseCardAnalyzer.playerSureHasCards(side) -
                    trickSoFar.getCardsPlayed()
                    ).toList()
            val legalPossibilities = possibleCards.legalPlayable(trickSoFar, trump())
            assert (legalPossibilities.isNotEmpty())
            legalPossibilities
        }

        var best = CardPlayedValue(null, if (maxNode) Int.MIN_VALUE else Int.MAX_VALUE)
        legalCards.forEach { card ->
            trickSoFar.addCard(card)
            val v = tryPlay(trickSoFar, side.clockwiseNext(), !maxNode)
            if (maxNode && v.isBetter(best)) {
                best = CardPlayedValue(card, v.value)
            } else if (!maxNode && v.isWorse(best)) {
                best = CardPlayedValue(card, v.value)
            }
            trickSoFar.removeLastCard()
        }
        return best
    }

    //------------------------------------------------------------------------------------------------------------------

    private fun firstPlayer() = getCurrentRound().getTrickOnTable().hasNotStarted()
    private fun secondPlayer() = getCurrentRound().getTrickOnTable().getCardsPlayed().size == 1
    private fun thirdPlayer() = getCurrentRound().getTrickOnTable().getCardsPlayed().size == 2
    private fun lastPlayer() = getCurrentRound().getTrickOnTable().getCardsPlayed().size == 3

    private fun canFollow() = getCardsInHand().any{getCurrentRound().getTrickOnTable().isLeadColor(it.color)}
    private fun hasTroef() = hasColor(trump())
    private fun mustTroeven() = !canFollow() && hasTroef()

    private fun hasColor(cardColor: CardColor) = getCardsInHand().any{it.color == cardColor}
    private fun hasCard(card: Card) = card in getCardsInHand()

    private fun firstTrick() = getCurrentRound().getTrickList().size == 1

    private fun isLeadPlayer() = getCurrentRound().getTrickOnTable().isSideToLead(this.tableSide)
    private fun isContractOwner() = getCurrentRound().isContractOwningSide(this.tableSide)
    private fun isContractOwnersPartner() = getCurrentRound().isContractOwningSide(this.tableSide.opposite())

    private fun trump() = getCurrentRound().getTrumpColor()

    private fun hasTrumpCard(rank: CardRank) = hasCard(Card(trump(), rank))
    private fun hasTrumpJack() = hasCard(trumpJack())
    private fun trumpJack() = Card(trump(), CardRank.JACK)
    private fun trumpNine() = Card(trump(), CardRank.NINE)
}

data class CardPlayedValue(val card: Card?, val value: Int) {
    fun isBetter(other: CardPlayedValue): Boolean = this.value > other.value
    fun isWorse(other: CardPlayedValue): Boolean = this.value < other.value
}
