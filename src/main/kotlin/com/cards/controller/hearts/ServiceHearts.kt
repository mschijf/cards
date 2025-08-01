package com.cards.controller.hearts

import com.cards.controller.basic.model.CardInHandModel
import com.cards.controller.basic.model.CardPlayedModel
import com.cards.controller.basic.model.GameStatusModel
import com.cards.controller.hearts.model.GameStatusModelHearts
import com.cards.controller.hearts.model.RoundScoreHearts
import com.cards.controller.hearts.model.ScoreModelHearts
import com.cards.controller.basic.model.TableModel
import com.cards.controller.basic.model.TrickCompletedModel
import com.cards.tools.RANDOMIZER
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.Table
import com.cards.game.fourplayercardgame.hearts.GameHearts
import com.cards.game.fourplayercardgame.hearts.ai.GeniusPlayerHearts
import org.springframework.stereotype.Service

@Service
class ServiceHearts {
    private var gameHearts = GameHearts()

    fun newGame(): GameStatusModelHearts {
        gameHearts = GameHearts()
        return getGameStatus()
    }

    fun getGameStatus(): GameStatusModelHearts {
        val trickOnTable = gameHearts.getCurrentRound().getTrickOnTable()
        val onTable = TableModel(
            trickOnTable.getCardPlayedBy(gameHearts.getCardPlayer(Table.SOUTH)),
            trickOnTable.getCardPlayedBy(gameHearts.getCardPlayer(Table.WEST)),
            trickOnTable.getCardPlayedBy(gameHearts.getCardPlayer(Table.NORTH)),
            trickOnTable.getCardPlayedBy(gameHearts.getCardPlayer(Table.EAST))
        )
        val playerToMove = gameHearts.getPlayerToMove()
        val leadPlayer = trickOnTable.getLeadPlayer()

        val playerSouth = makePlayerCardListModel(Table.SOUTH)
        val playerNorth = makePlayerCardListModel(Table.NORTH)
        val playerWest = makePlayerCardListModel(Table.WEST)
        val playerEast = makePlayerCardListModel(Table.EAST)

        val gameJsonString = "" //Gson().toJson(gm)

        val goingUp = gameHearts.isGoingUp()

        return GameStatusModelHearts(
            GameStatusModel(
                onTable,
                playerToMove.tablePosition,
                leadPlayer.tablePosition,
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

    private fun makePlayerCardListModel(tablePosition: Table): List<CardInHandModel> {
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
            .getCardAnalysisValue(card)?.toString() ?: "x"
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