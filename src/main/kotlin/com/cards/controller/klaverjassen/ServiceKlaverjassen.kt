package com.cards.controller.klaverjassen

import com.cards.controller.basic.model.*
import com.cards.controller.klaverjassen.model.GameStatusModelKlaverjassen
import com.cards.controller.klaverjassen.model.RoundScoreKlaverjassen
import com.cards.controller.klaverjassen.model.ScoreModelKlaverjassen
import com.cards.controller.klaverjassen.model.TrumpChoiceModel
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.klaverjassen.GameKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.GameMasterKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.PlayerKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.RoundKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.ScoreType
import com.cards.game.fourplayercardgame.klaverjassen.ai.GeniusPlayerKlaverjassen
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
        val onTablePosition = TableModel(
            trickOnTable.getCardPlayedBy(TablePosition.SOUTH),
            trickOnTable.getCardPlayedBy(TablePosition.WEST),
            trickOnTable.getCardPlayedBy(TablePosition.NORTH),
            trickOnTable.getCardPlayedBy(TablePosition.EAST)
        )
        val positionToMove = gameMasterKlaverjassen.getPlayerToMove().tablePosition
        val leadPosition = trickOnTable.getLeadPosition()

        val playerSouth = makePlayerCardListModel(TablePosition.SOUTH)
        val playerNorth = makePlayerCardListModel(TablePosition.NORTH)
        val playerWest = makePlayerCardListModel(TablePosition.WEST)
        val playerEast = makePlayerCardListModel(TablePosition.EAST)

        val gameJsonString = "" //Gson().toJson(gm)
        val newRoundStarted = gameKlaverjassen.getCurrentRound().hasNotStarted()

        if (positionToMove == TablePosition.SOUTH) {
            (gameMasterKlaverjassen.getCardPlayer(TablePosition.SOUTH) as GeniusPlayerKlaverjassen).printAnalyzer()
        }

        return GameStatusModelKlaverjassen(
            generic = GameStatusModel(
                onTablePosition,
                positionToMove,
                leadPosition,
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
                (gameKlaverjassen.getCurrentRound() as RoundKlaverjassen).getContractOwner()
            )
        )
    }

    private fun makePlayerCardListModel(tablePosition: TablePosition): List<CardInHandModel> {
        val player = gameMasterKlaverjassen.getCardPlayer(tablePosition)
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

        gameMasterKlaverjassen.playCard(suggestedCardToPlay)

        val trickCompleted = if (gameKlaverjassen.trickCompleted())
            TrickCompletedModel(
                gameKlaverjassen.getLastTrickWinner()!!,
                gameKlaverjassen.roundCompleted(),
                gameKlaverjassen.isFinished(),
            )
        else
            null

        val nextPositionToPlay = gameKlaverjassen.getPositionToMove()

        return CardPlayedModel(
            playerToMove.tablePosition,
            suggestedCardToPlay,
            nextPositionToPlay,
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

    fun computeTrumpCardChoice(tablePosition: TablePosition): TrumpChoiceModel {
        val choosingPlayer = (gameMasterKlaverjassen.getCardPlayer(tablePosition) as PlayerKlaverjassen)
        val trumpColor = choosingPlayer.chooseTrumpColor()
        return executeTrumpCardChoice(trumpColor, tablePosition)
    }

    fun executeTrumpCardChoice(trumpColor: CardColor, tablePosition: TablePosition): TrumpChoiceModel {
        (gameKlaverjassen.getCurrentRound() as RoundKlaverjassen).setTrumpColorAndContractOwner(trumpColor, tablePosition)
        return TrumpChoiceModel(trumpColor, tablePosition)
    }
}