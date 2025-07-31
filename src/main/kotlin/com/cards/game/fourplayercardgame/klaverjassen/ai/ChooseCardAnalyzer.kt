package com.cards.game.fourplayercardgame.klaverjassen.ai

import com.cards.game.card.CARDDECK
import com.cards.game.card.Card
import com.cards.game.card.ofColor
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.klaverjassen.KLAVERJASSEN.beats
import com.cards.game.fourplayercardgame.klaverjassen.RoundKlaverjassen

class ChooseCardAnalyzer(
    private val player: GeniusPlayerKlaverjassen) {

    fun determinePlayerCanHaveCards(): Map<Player, Set<Card>> {
        val playerCanHaveCards = (player.getOtherPlayers() + player)
            .associateWith { other -> if (other != player) (CARDDECK.baseDeckCardsSevenAndHigher - player.getCardsInHand()).toMutableSet() else mutableSetOf()}

        val currentRound = player.getCurrentRound() as RoundKlaverjassen
        val trumpColor = currentRound.getTrumpColor()
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
                    } else if (playerPlayedCard.card.color == trumpColor) {
                        playerCanHaveCards[playerPlayedCard.player]!! -= allCards.filter {
                            it.beats(
                                highestTrumpUpTillNow,
                                trumpColor
                            )
                        }
                    } else {
                        //player just follows, we can not conclude anything yet
                    }
                    if (playerPlayedCard.card.color == trumpColor && playerPlayedCard.card.beats(
                            highestTrumpUpTillNow,
                            trumpColor
                        )
                    )
                        highestTrumpUpTillNow = playerPlayedCard.card
                }
            }
        }
        return playerCanHaveCards
    }
}