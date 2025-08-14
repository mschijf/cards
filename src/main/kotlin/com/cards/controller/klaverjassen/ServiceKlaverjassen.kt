package com.cards.controller.klaverjassen

import com.cards.controller.basic.model.*
import com.cards.controller.klaverjassen.model.GameStatusModelKlaverjassen
import com.cards.controller.klaverjassen.model.RoundScoreKlaverjassen
import com.cards.controller.klaverjassen.model.ScoreModelKlaverjassen
import com.cards.controller.klaverjassen.model.TrumpChoiceModel
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.TableSide
import com.cards.controller.klaverjassen.GameMasterKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.player.PlayerKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.RoundKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.ScoreType
import com.cards.game.fourplayercardgame.klaverjassen.player.ai.GeniusPlayerKlaverjassen
import com.cards.tools.RANDOMIZER
import org.springframework.stereotype.Service

@Service
class ServiceKlaverjassen {
    private val gameMasterKlaverjassen = GameMasterKlaverjassen().also { it.startNewGame() }
    private var gameKlaverjassen = gameMasterKlaverjassen.getGame()

    fun newGame(): GameStatusModelKlaverjassen {
        gameMasterKlaverjassen.startNewGame()
        gameKlaverjassen = gameMasterKlaverjassen.getGame()
        return getGameStatus()
    }

    fun getGameStatus(): GameStatusModelKlaverjassen {
        val trickOnTable = gameKlaverjassen.getCurrentRound().getTrickOnTable()
        val onTableSide = TableModel(
            trickOnTable.getCardPlayedBy(TableSide.SOUTH),
            trickOnTable.getCardPlayedBy(TableSide.WEST),
            trickOnTable.getCardPlayedBy(TableSide.NORTH),
            trickOnTable.getCardPlayedBy(TableSide.EAST)
        )
        val sideToMove = gameMasterKlaverjassen.getPlayerToMove().tableSide
        val sideToLead = trickOnTable.getSideToLead()

        val playerSouth = makePlayerCardListModel(TableSide.SOUTH)
        val playerNorth = makePlayerCardListModel(TableSide.NORTH)
        val playerWest = makePlayerCardListModel(TableSide.WEST)
        val playerEast = makePlayerCardListModel(TableSide.EAST)

        val gameJsonString = "" //Gson().toJson(gm)
        val newRoundStarted = gameKlaverjassen.getCurrentRound().hasNotStarted()

        if (sideToMove == TableSide.SOUTH) {
            (gameMasterKlaverjassen.getCardPlayer(TableSide.SOUTH) as GeniusPlayerKlaverjassen).printAnalyzer()
        }

        return GameStatusModelKlaverjassen(
            generic = GameStatusModel(
                onTableSide,
                sideToMove,
                sideToLead,
                newRoundStarted,
                playerSouth,
                playerWest,
                playerNorth,
                playerEast,
                gameJsonString,
                RANDOMIZER.getLastSeedUsed()
            ),
            trumpChoice = TrumpChoiceModel(
                    (gameKlaverjassen.getCurrentRound() as RoundKlaverjassen).getTrumpColor(),
                    (gameKlaverjassen.getCurrentRound() as RoundKlaverjassen).getContractOwningSide()
                )
        )
    }

    private fun makePlayerCardListModel(tableSide: TableSide): List<CardInHandModel> {
        val player = gameMasterKlaverjassen.getCardPlayer(tableSide)
        return player
            .getCardsInHand()
            .sortedBy { card -> 100 * card.color.ordinal + card.rank.ordinal }
            .map { card ->
                CardInHandModel(
                    card,
                    gameMasterKlaverjassen.isLegalCardToPlay(player, card),
                    getGeniusCardValue(player as PlayerKlaverjassen, card)
                )
            }
    }

    private fun getGeniusCardValue(geniusPlayerKlaverjassen: PlayerKlaverjassen, card: Card): String {
        return "?"
    }

    fun computeMove(): CardPlayedModel? {
        val playerToMove = gameMasterKlaverjassen.getPlayerToMove()
        val suggestedCardToPlay = playerToMove.chooseCard()
        return executeMove(suggestedCardToPlay.color, suggestedCardToPlay.rank)
    }

    fun executeMove(color: CardColor, rank: CardRank): CardPlayedModel? {
        val playerToMove = gameMasterKlaverjassen.getPlayerToMove()
        val suggestedCardToPlay = Card(color, rank)
        if (!gameMasterKlaverjassen.isLegalCardToPlay(playerToMove, suggestedCardToPlay))
            return null

        val cardsStillInHand = playerToMove.getNumberOfCardsInHand()

        val gameStatus = gameMasterKlaverjassen.playCard(suggestedCardToPlay)

        val trickCompleted = if (gameStatus.trickFinished)
            TrickCompletedModel(
                gameKlaverjassen.getLastTrickWinner()!!,
                gameStatus.roundFinished,
                gameStatus.gameFinished,
            )
        else
            null

        val nextSideToPlay = gameKlaverjassen.getSideToMove()

        return CardPlayedModel(
            playerToMove.tableSide,
            suggestedCardToPlay,
            nextSideToPlay,
            cardsStillInHand,
            trickCompleted,
        )
    }

    fun getScoreCard(): ScoreModelKlaverjassen {
        return ScoreModelKlaverjassen(
            gameKlaverjassen.getAllScoresPerRound()
                .map { roundScore ->
                    RoundScoreKlaverjassen(
                        if (roundScore.northSouthPoints == 0) {
                            when (roundScore.scoreType) {
                                ScoreType.NAT -> "NAT"
                                ScoreType.PIT -> "PIT"
                                ScoreType.REGULAR -> "0"
                            }
                        } else {
                            roundScore.northSouthPoints.toString()
                        },

                        if (roundScore.eastWestPoints == 0) {
                            when (roundScore.scoreType) {
                                ScoreType.NAT -> "NAT"
                                ScoreType.PIT -> "PIT"
                                ScoreType.REGULAR -> "0"
                            }
                        } else {
                            roundScore.eastWestPoints.toString()
                        },

                        if (roundScore.northSouthBonus == 0) "" else roundScore.northSouthBonus.toString(),
                        if (roundScore.eastWestBonus == 0) "" else roundScore.eastWestBonus.toString()
                    )
                }
        )
    }

    fun computeTrumpCardChoice(tableSide: TableSide): TrumpChoiceModel {
        val choosingPlayer = (gameMasterKlaverjassen.getCardPlayer(tableSide) as PlayerKlaverjassen)
        val trumpColor = choosingPlayer.chooseTrumpColor()
        return executeTrumpCardChoice(trumpColor, tableSide)
    }

    fun executeTrumpCardChoice(trumpColor: CardColor, tableSide: TableSide): TrumpChoiceModel {
        (gameKlaverjassen.getCurrentRound() as RoundKlaverjassen).setTrumpColorAndContractOwner(trumpColor, tableSide)
        return TrumpChoiceModel(trumpColor, tableSide)
    }
}