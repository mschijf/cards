package com.cards.controller

import com.cards.controller.model.*
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.hearts.GameHearts
import com.cards.game.fourplayercardgame.hearts.ai.GeniusPlayerHearts
import org.springframework.stereotype.Service

@Service
class GameHeartsService {
    private var gameHearts = GameHearts()

    fun newGame(): GameStatusModel {
        gameHearts = GameHearts()
        return getGameStatus()
    }

    fun getGameStatus(): GameStatusModel {
        val trickOnTable = gameHearts.getCurrentRound().getTrickOnTable()
        val onTable = TableModel(
            trickOnTable.getCardPlayedBy(gameHearts.getCardPlayer(TablePosition.SOUTH)),
            trickOnTable.getCardPlayedBy(gameHearts.getCardPlayer(TablePosition.WEST)),
            trickOnTable.getCardPlayedBy(gameHearts.getCardPlayer(TablePosition.NORTH)),
            trickOnTable.getCardPlayedBy(gameHearts.getCardPlayer(TablePosition.EAST))
        )
        val playerToMove = gameHearts.getPlayerToMove()
        val leadPlayer = trickOnTable.getLeadPlayer()

        val playerSouth = makePlayerCardListModel(TablePosition.SOUTH)
        val playerNorth = makePlayerCardListModel(TablePosition.NORTH)
        val playerWest = makePlayerCardListModel(TablePosition.WEST)
        val playerEast = makePlayerCardListModel(TablePosition.EAST)

        val gameJsonString = "" //Gson().toJson(gm)

        val goingUp = gameHearts.isGoingUp()

        return GameStatusModel(
            onTable,
            playerToMove.tablePosition,
            leadPlayer.tablePosition,
            playerSouth,
            playerWest,
            playerNorth,
            playerEast,
            gameJsonString,
            goingUp,
        )
    }

    private fun makePlayerCardListModel(tablePosition: TablePosition): List<CardInHandModel> {
        val player = gameHearts.getCardPlayer(tablePosition)
        return player
            .getCardsInHand()
            .sortedBy { card -> 100 * card.color.ordinal + card.rank.ordinal }
            .map { card ->
                CardInHandModel(
                    card,
                    gameHearts.isLegalCardToPlay(player, card),
                    getGeniusCardValue(player as GeniusPlayerHearts, card)
                )
            }
    }

    private fun getGeniusCardValue(geniusPlayerHearts: GeniusPlayerHearts, card: Card): String {
        return geniusPlayerHearts
            .getMetaCardList()
            .getCardValue(card)?.toString() ?: "x"
    }

    fun computeMove(): CardPlayedModel? {
        val playerToMove = gameHearts.getPlayerToMove()
        val suggestedCardToPlay = playerToMove.chooseCard()
        return executeMove(suggestedCardToPlay.color, suggestedCardToPlay.rank)
    }

    fun executeMove(color: CardColor, rank: CardRank): CardPlayedModel? {
        val playerToMove = gameHearts.getPlayerToMove()
        val suggestedCardToPlay = Card(color, rank)
        if (!gameHearts.isLegalCardToPlay(playerToMove, suggestedCardToPlay))
            return null

        val cardsStillInHand = playerToMove.getCardsInHand().size

        gameHearts.playCard(suggestedCardToPlay)

        val trickCompleted = if (gameHearts.trickCompleted())
            TrickCompletedModel(
                gameHearts.getLastTrickWinner()!!.tablePosition,
                gameHearts.roundCompleted(),
                gameHearts.isFinished(),
            )
        else
            null

        val nextPlayer = gameHearts.getPlayerToMove()

        return CardPlayedModel(
            playerToMove.tablePosition,
            suggestedCardToPlay,
            nextPlayer.tablePosition,
            cardsStillInHand,
            trickCompleted,
        )
    }

    fun getScoreCard(): ScoreModel {
        return ScoreModel (
            gameHearts.getCumulativeScorePerRound()
                .map { spr -> PlayerScore(
                    spr.southValue,
                    spr.westValue,
                    spr.eastValue,
                    spr.northValue)
                }
        )
    }

}