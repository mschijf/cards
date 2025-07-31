package com.cards.controller.klaverjassen

import com.cards.controller.basic.model.CardInHandModel
import com.cards.controller.basic.model.CardPlayedModel
import com.cards.controller.basic.model.GameStatusModel
import com.cards.controller.klaverjassen.model.GameStatusModelKlaverjassen
import com.cards.controller.klaverjassen.model.RoundScoreKlaverjassen
import com.cards.controller.klaverjassen.model.ScoreModelKlaverjassen
import com.cards.controller.basic.model.TableModel
import com.cards.controller.basic.model.TrickCompletedModel
import com.cards.controller.klaverjassen.model.TrumpChoiceModel
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.Table
import com.cards.game.fourplayercardgame.klaverjassen.GameKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.PlayerKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.RoundKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.ScoreType
import org.springframework.stereotype.Service

@Service
class ServiceKlaverjassen {
    private var gameKlaverjassen = GameKlaverjassen()

    fun newGame(): GameStatusModelKlaverjassen {
        gameKlaverjassen = GameKlaverjassen()
        return getGameStatus()
    }

    fun getGameStatus(): GameStatusModelKlaverjassen {
        val trickOnTable = gameKlaverjassen.getCurrentRound().getTrickOnTable()
        val onTable = TableModel(
            trickOnTable.getCardPlayedBy(gameKlaverjassen.getCardPlayer(Table.SOUTH)),
            trickOnTable.getCardPlayedBy(gameKlaverjassen.getCardPlayer(Table.WEST)),
            trickOnTable.getCardPlayedBy(gameKlaverjassen.getCardPlayer(Table.NORTH)),
            trickOnTable.getCardPlayedBy(gameKlaverjassen.getCardPlayer(Table.EAST))
        )
        val playerToMove = gameKlaverjassen.getPlayerToMove()
        val leadPlayer = trickOnTable.getLeadPlayer()

        val playerSouth = makePlayerCardListModel(Table.SOUTH)
        val playerNorth = makePlayerCardListModel(Table.NORTH)
        val playerWest = makePlayerCardListModel(Table.WEST)
        val playerEast = makePlayerCardListModel(Table.EAST)

        val gameJsonString = "" //Gson().toJson(gm)
        val nr = gameKlaverjassen.getCurrentRound().hasNotStarted()
        return GameStatusModelKlaverjassen(
            generic = GameStatusModel(
                onTable,
                playerToMove.tablePosition,
                leadPlayer.tablePosition,
                nr,
                playerSouth,
                playerWest,
                playerNorth,
                playerEast,
                gameJsonString,
            ),
            trumpChoice = TrumpChoiceModel(
                (gameKlaverjassen.getCurrentRound() as RoundKlaverjassen).getTrumpColor(),
                (gameKlaverjassen.getCurrentRound() as RoundKlaverjassen).getContractOwner().tablePosition
            )
        )
    }

    private fun makePlayerCardListModel(tablePosition: Table): List<CardInHandModel> {
        val player = gameKlaverjassen.getCardPlayer(tablePosition)
        return player
            .getCardsInHand()
            .sortedBy { card -> 100 * card.color.ordinal + card.rank.ordinal }
            .map { card ->
                CardInHandModel(
                    card,
                    gameKlaverjassen.isLegalCardToPlay(player, card),
                    getGeniusCardValue(player as PlayerKlaverjassen, card)
                )
            }
    }

    private fun getGeniusCardValue(geniusPlayerKlaverjassen: PlayerKlaverjassen, card: Card): String {
        return "?"
    }

    fun computeMove(): CardPlayedModel? {
        val playerToMove = gameKlaverjassen.getPlayerToMove()
        val suggestedCardToPlay = playerToMove.chooseCard()
        return executeMove(suggestedCardToPlay.color, suggestedCardToPlay.rank)
    }

    fun executeMove(color: CardColor, rank: CardRank): CardPlayedModel? {
        val playerToMove = gameKlaverjassen.getPlayerToMove()
        val suggestedCardToPlay = Card(color, rank)
        if (!gameKlaverjassen.isLegalCardToPlay(playerToMove, suggestedCardToPlay))
            return null

        val cardsStillInHand = playerToMove.getCardsInHand().size

        gameKlaverjassen.playCard(suggestedCardToPlay)

        val trickCompleted = if (gameKlaverjassen.trickCompleted())
            TrickCompletedModel(
                gameKlaverjassen.getLastTrickWinner()!!.tablePosition,
                gameKlaverjassen.roundCompleted(),
                gameKlaverjassen.isFinished(),
            )
        else
            null

        val nextPlayer = gameKlaverjassen.getPlayerToMove()

        return CardPlayedModel(
            playerToMove.tablePosition,
            suggestedCardToPlay,
            nextPlayer.tablePosition,
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

    fun computeTrumpCardChoice(tablePosition: Table): TrumpChoiceModel {
        val choosingPlayer = (gameKlaverjassen.getCardPlayer(tablePosition) as PlayerKlaverjassen)
        val trumpColor = choosingPlayer.chooseTrumpColor()
        return executeTrumpCardChoice(trumpColor, tablePosition)
    }

    fun executeTrumpCardChoice(trumpColor: CardColor, tablePosition: Table): TrumpChoiceModel {
        val choosingPlayer = (gameKlaverjassen.getCardPlayer(tablePosition) as PlayerKlaverjassen)
        gameKlaverjassen.setTrumpColorAndContractOwner(trumpColor, choosingPlayer)
        return TrumpChoiceModel(trumpColor, choosingPlayer.tablePosition)
    }

}