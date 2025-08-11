package com.cards.controller.hearts

import com.cards.controller.basic.model.*
import com.cards.controller.hearts.model.GameStatusModelHearts
import com.cards.controller.hearts.model.RoundScoreHearts
import com.cards.controller.hearts.model.ScoreModelHearts
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.hearts.GameHearts
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
        val onTablePosition = TableModel(
            trickOnTable.getCardPlayedBy(TablePosition.SOUTH),
            trickOnTable.getCardPlayedBy(TablePosition.WEST),
            trickOnTable.getCardPlayedBy(TablePosition.NORTH),
            trickOnTable.getCardPlayedBy(TablePosition.EAST)
        )
        val positionToMove = gameHearts.getPositionToMove()
        val leadPosition = trickOnTable.getLeadPosition()

        val playerSouth = makePlayerCardListModel(TablePosition.SOUTH)
        val playerNorth = makePlayerCardListModel(TablePosition.NORTH)
        val playerWest = makePlayerCardListModel(TablePosition.WEST)
        val playerEast = makePlayerCardListModel(TablePosition.EAST)

        val gameJsonString = "" //Gson().toJson(gm)

        val goingUp = gameHearts.isGoingUp()

        return GameStatusModelHearts(
            GameStatusModel(
                onTablePosition,
                positionToMove,
                leadPosition,
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

    private fun makePlayerCardListModel(tablePosition: TablePosition): List<CardInHandModel> {
        val player = gameMasterHearts.getCardPlayer(tablePosition)
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
        val playerToMove = gameMasterHearts.getCardPlayer(gameHearts.getPositionToMove())
        val suggestedCardToPlay = playerToMove.chooseCard()
        return executeMove(suggestedCardToPlay.color, suggestedCardToPlay.rank)
    }

    fun executeMove(color: CardColor, rank: CardRank): CardPlayedModel? {
        val positionToMove = gameHearts.getPositionToMove()
        val playerToMove = gameMasterHearts.getCardPlayer(positionToMove)
        val suggestedCardToPlay = Card(color, rank)
        if (!gameMasterHearts.isLegalCardToPlay(playerToMove, suggestedCardToPlay))
            return null

        val cardsStillInHand = playerToMove.getNumberOfCardsInHand()

        gameMasterHearts.playCard(suggestedCardToPlay)

        val trickCompleted = if (gameHearts.trickCompleted())
            TrickCompletedModel(
                gameHearts.getLastTrickWinner()!!,
                gameHearts.roundCompleted(),
                gameHearts.isFinished(),
            )
        else
            null

        val nextPlayer = gameHearts.getPositionToMove()

        return CardPlayedModel(
            positionToMove,
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