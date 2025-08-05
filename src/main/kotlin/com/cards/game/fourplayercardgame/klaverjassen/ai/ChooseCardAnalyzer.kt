package com.cards.game.fourplayercardgame.klaverjassen.ai

import com.cards.game.card.CARDDECK
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.ofColor
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.klaverjassen.PlayerKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.beats
import com.cards.game.fourplayercardgame.klaverjassen.RoundKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.toRankNumberNoTrump
import com.cards.game.fourplayercardgame.klaverjassen.toRankNumberTrump
import kotlin.collections.minusAssign

class ChooseCardAnalyzer(
    private val player: GeniusPlayerKlaverjassen,
    private val currentRound: RoundKlaverjassen,
    private val trumpColor: CardColor,
    private val playerPossibilities: Map<Player, Set<Card>>) {

    fun getCardsPlayed(): Set<Card> {
        return currentRound
            .getCompletedTrickList()
            .flatMap { trick -> trick.getCardsPlayed() }
            .union(currentRound.getTrickOnTable().getCardsPlayed())
            .map { cardPlayed -> cardPlayed.card }
            .toSet()
    }

    fun getCardsStillInPlay(): Set<Card> {
        return CARDDECK
            .baseDeckCardsSevenAndHigher
            .minus(getCardsPlayed())
            .minus(player.getCardsInHand().toSet())
            .toSet()
    }


    fun playerCanHaveCards(player: Player): Set<Card> = playerPossibilities[player]!!
    fun playerCanHaveTrumps(player: Player): Set<Card> = playerPossibilities[player]!!
        .filter { it.color == trumpColor }
        .toSet()
    fun playerCanHaveCardsOfSameColor(player: Player, card: Card): Set<Card> = playerPossibilities[player]!!
        .filter { it.color == card.color }
        .toSet()
    fun playerCanHaveHigherThanCardsOfSameColor(player: Player, card: Card): Set<Card> = playerPossibilities[player]!!
        .filter { it.color == card.color && if (card.color == trumpColor) it.toRankNumberTrump() > card.toRankNumberTrump() else it.toRankNumberNoTrump() > card.toRankNumberNoTrump()}
        .toSet()



    companion object {
        fun forPlayer(player: GeniusPlayerKlaverjassen): ChooseCardAnalyzer {
            return ChooseCardAnalyzer(
                player,
                player.getCurrentRound() as RoundKlaverjassen,
                (player.getCurrentRound() as RoundKlaverjassen).getTrumpColor(),
                determinePlayerCanHaveCards(player)
            )
        }

        private fun determinePlayerCanHaveCards(
            player: GeniusPlayerKlaverjassen): Map<Player, Set<Card>> {

            val currentRound = player.getCurrentRound() as RoundKlaverjassen
            val trumpColor = currentRound.getTrumpColor()

            val playerCanHaveCards = (player.getOtherPlayers() + player)
                .associateWith { other -> if (other != player) (CARDDECK.baseDeckCardsSevenAndHigher - player.getCardsInHand()).toMutableSet() else mutableSetOf()}

            val allCards = CARDDECK.baseDeckCardsSevenAndHigher

            (player.getCurrentRound().getCompletedTrickList() + currentRound.getTrickOnTable()).forEach { trick ->
                if (!trick.hasNotStarted()) {
                    val firstCard = trick.getCardsPlayed().first().card
                    player.getOtherPlayers().forEach { otherPlayer -> playerCanHaveCards[otherPlayer]!! -= firstCard }

                    var highestTrumpUpTillNow = if (firstCard.color == trumpColor) firstCard else null
                    trick.getCardsPlayed().drop(1).forEach { playerPlayedCard ->

                        player.getOtherPlayers()
                            .forEach { otherPlayer -> playerCanHaveCards[otherPlayer]!! -= playerPlayedCard.card }

                        if (playerPlayedCard.card.color != firstCard.color) {
                            playerCanHaveCards[playerPlayedCard.player]!! -= allCards.ofColor(firstCard.color)
                            if (playerPlayedCard.card.color != trumpColor) {
                                playerCanHaveCards[playerPlayedCard.player]!! -= allCards.ofColor(trumpColor)
                            } else {
                                if (!playerPlayedCard.card.beats(highestTrumpUpTillNow, trumpColor)) {
                                    playerCanHaveCards[playerPlayedCard.player]!! -= allCards.filter {
                                        it.beats(
                                            highestTrumpUpTillNow,
                                            trumpColor
                                        )
                                    }
                                }
                            }
                        } else if (playerPlayedCard.card.color == trumpColor && highestTrumpUpTillNow!!.beats(playerPlayedCard.card, trumpColor)) {
                            playerCanHaveCards[playerPlayedCard.player]!! -= allCards.filter {
                                it.beats(
                                    highestTrumpUpTillNow,
                                    trumpColor
                                )
                            }
                        } else {
                            //player just follows, we can not conclude anything yet
                        }
                        if (playerPlayedCard.card.color == trumpColor && playerPlayedCard.card.beats(highestTrumpUpTillNow, trumpColor))
                            highestTrumpUpTillNow = playerPlayedCard.card
                    }
                }
            }
            return playerCanHaveCards
        }
    }
}