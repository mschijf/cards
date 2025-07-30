package com.cards.controller

import com.cards.controller.model.*
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.basic.Table
import com.cards.game.fourplayercardgame.klaverjassen.GameKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.PlayerKlaverjassen
import com.cards.game.fourplayercardgame.klaverjassen.RoundKlaverjassen
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

        return GameStatusModelKlaverjassen(
            GameStatusModel (
                onTable,
                playerToMove.tablePosition,
                leadPlayer.tablePosition,
                playerSouth,
                playerWest,
                playerNorth,
                playerEast,
                gameJsonString,
            ),
            (gameKlaverjassen.getCurrentRound() as RoundKlaverjassen).getTrumpColor(),
            (gameKlaverjassen.getCurrentRound() as RoundKlaverjassen).getContractOwner().tablePosition
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

    fun getScoreCard(): ScoreModel {
        return ScoreModel (
            gameKlaverjassen.getCumulativeScorePerRound()
                .map { spr -> PlayerScore(
                    spr.southValue,
                    spr.westValue,
                    spr.eastValue,
                    spr.northValue)
                }
        )
    }
}