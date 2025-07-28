package com.cards.controller

import com.cards.controller.model.*
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.hearts.GameHearts
import com.cards.game.fourplayercardgame.hearts.HeartsConstants
import com.cards.game.fourplayercardgame.hearts.ai.GeniusHeartsPlayer
import org.springframework.stereotype.Service

@Service
class GameService {
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

        val nCardsInHand = HeartsConstants.INITIAL_NUMBER_OF_CARDS_IN_HAND
        val playerSouth =
            List(nCardsInHand) { i -> gameHearts.getCardPlayer(TablePosition.SOUTH).getCardsInHand().elementAtOrNull(i) }
        val playerWest =
            List(nCardsInHand) { i -> gameHearts.getCardPlayer(TablePosition.WEST).getCardsInHand().elementAtOrNull(i) }
        val playerNorth =
            List(nCardsInHand) { i -> gameHearts.getCardPlayer(TablePosition.NORTH).getCardsInHand().elementAtOrNull(i) }
        val playerEast =
            List(nCardsInHand) { i -> gameHearts.getCardPlayer(TablePosition.EAST).getCardsInHand().elementAtOrNull(i) }

        //todo: add legal cards to play for player to move, can be used n 'setClickable' in board-scripts.js

        val gameJsonString = "" //Gson().toJson(gm)

        val goingUp = gameHearts.isGoingUp()
        val geniusValueSouth = List(HeartsConstants.INITIAL_NUMBER_OF_CARDS_IN_HAND) { i ->
            getGeniusCardValue(
                (gameHearts.getCardPlayer(TablePosition.SOUTH) as GeniusHeartsPlayer),
                gameHearts.getCardPlayer(TablePosition.SOUTH).getCardsInHand().elementAtOrNull(i)
            )
        }

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
            geniusValueSouth,
        )
    }

    private fun getGeniusCardValue(geniusHeartsPlayer: GeniusHeartsPlayer, card: Card?): String? {
        if (card == null)
            return null
        return geniusHeartsPlayer.getMetaCardList().getCardValue(card)?.toString() ?: "x"
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