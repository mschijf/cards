package com.cards.controller.hearts

import com.cards.controller.basic.model.*
import com.cards.controller.hearts.model.GameStatusModelHearts
import com.cards.controller.hearts.model.RoundScoreHearts
import com.cards.controller.hearts.model.ScoreModelHearts
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.TableSide
import com.cards.game.fourplayercardgame.hearts.GameMasterHearts
import com.cards.game.fourplayercardgame.hearts.ai.GeniusPlayerHearts
import com.cards.tools.RANDOMIZER
import org.springframework.stereotype.Service

@Service
class ServiceHearts {
    private val gameMasterHearts = GameMasterHearts().also { it.createGame() }
    private var gameHearts = gameMasterHearts.getGame()

    fun newGame(): GameStatusModelHearts {
        gameMasterHearts.startNewGame()
        gameHearts = gameMasterHearts.getGame()
        return getGameStatus()
    }

    fun getGameStatus(): GameStatusModelHearts {
        val trickOnTable = gameHearts.getCurrentRound().getTrickOnTable()
        val onTableSide = TableModel(
            trickOnTable.getCardPlayedBy(TableSide.SOUTH),
            trickOnTable.getCardPlayedBy(TableSide.WEST),
            trickOnTable.getCardPlayedBy(TableSide.NORTH),
            trickOnTable.getCardPlayedBy(TableSide.EAST)
        )
        val sideToMove = gameHearts.getSideToMove()
        val sideToLead = trickOnTable.getSideToLead()

        val playerSouth = makePlayerCardListModel(TableSide.SOUTH)
        val playerNorth = makePlayerCardListModel(TableSide.NORTH)
        val playerWest = makePlayerCardListModel(TableSide.WEST)
        val playerEast = makePlayerCardListModel(TableSide.EAST)

        val gameJsonString = "" //Gson().toJson(gm)

        val goingUp = gameHearts.isGoingUp()

        return GameStatusModelHearts(
            GameStatusModel(
                onTableSide,
                sideToMove,
                sideToLead,
                gameHearts.getCurrentRound().hasNotStarted(),
                playerSouth,
                playerWest,
                playerNorth,
                playerEast,
                gameJsonString,
                RANDOMIZER.getLastSeedUsed()
            ),
            goingUp,
        )
    }

    private fun makePlayerCardListModel(tableSide: TableSide): List<CardInHandModel> {
        val player = gameMasterHearts.getCardPlayer(tableSide)
        return player
            .getCardsInHand()
            .sortedBy { card -> 100 * card.color.ordinal + card.rank.ordinal }
            .map { card ->
                CardInHandModel(
                    card,
                    gameMasterHearts.isLegalCardToPlay(player, card),
                    getGeniusCardValue(player as GeniusPlayerHearts, card)
                )
            }
    }

    private fun getGeniusCardValue(geniusPlayerHearts: GeniusPlayerHearts, card: Card): String {
        return geniusPlayerHearts
            .getMetaCardList()
            .getCardAnalysisValue(card)?.toString() ?: "x"
    }

    fun computeMove(): CardPlayedModel? {
        val playerToMove = gameMasterHearts.getCardPlayer(gameHearts.getSideToMove())
        val suggestedCardToPlay = playerToMove.chooseCard()
        return executeMove(suggestedCardToPlay.color, suggestedCardToPlay.rank)
    }

    fun executeMove(color: CardColor, rank: CardRank): CardPlayedModel? {
        val sideToMove = gameHearts.getSideToMove()
        val playerToMove = gameMasterHearts.getCardPlayer(sideToMove)
        val suggestedCardToPlay = Card(color, rank)
        if (!gameMasterHearts.isLegalCardToPlay(playerToMove, suggestedCardToPlay))
            return null

        val cardsStillInHand = playerToMove.getNumberOfCardsInHand()

        val gameStatus = gameMasterHearts.playCard(suggestedCardToPlay)

        val trickCompleted = if (gameStatus.trickFinished)
            TrickCompletedModel(
                gameHearts.getLastTrickWinner()!!,
                gameStatus.roundFinished,
                gameStatus.gameFinished,
            )
        else
            null

        val nextPlayer = gameHearts.getSideToMove()

        return CardPlayedModel(
            sideToMove,
            suggestedCardToPlay,
            nextPlayer,
            cardsStillInHand,
            trickCompleted,
        )
    }

    fun getScoreCard(): ScoreModelHearts {
        return ScoreModelHearts(
            gameHearts.getCumulativeScorePerRound()
                .map { spr ->
                    RoundScoreHearts(
                        spr.southValue,
                        spr.westValue,
                        spr.eastValue,
                        spr.northValue
                    )
                }
        )
    }
}