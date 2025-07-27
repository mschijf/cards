package com.cards.controller

import com.cards.controller.model.*
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.GameMaster
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.hearts.GameHearts
import com.cards.game.fourplayercardgame.hearts.GeniusHeartsPlayer
import org.springframework.stereotype.Service

@Service
class GameService {
    private var gm = newGameMaster()

    private fun newGameMaster(): GameMaster {
        return GameMaster(GameHearts())
    }

    fun newGame(): GameStatusModel {
        gm = newGameMaster()
        return getGameStatus()
    }

    fun getGameStatus(): GameStatusModel {
        val trickOnTable = gm.game.getCurrentRound().getTrickOnTable()
        val onTable = TableModel(
            trickOnTable.getCardPlayedBy(gm.game.getCardPlayer(TablePosition.SOUTH)),
            trickOnTable.getCardPlayedBy(gm.game.getCardPlayer(TablePosition.WEST)),
            trickOnTable.getCardPlayedBy(gm.game.getCardPlayer(TablePosition.NORTH)),
            trickOnTable.getCardPlayedBy(gm.game.getCardPlayer(TablePosition.EAST))
        )
        val playerToMove = gm.game.getPlayerToMove()
        val leadPlayer = trickOnTable.getLeadPlayer()

        val nCardsInHand = gm.game.initialNumberOfCardsInHand()
        val playerSouth =
            List(nCardsInHand) { i -> gm.game.getCardPlayer(TablePosition.SOUTH).getCardsInHand().elementAtOrNull(i) }
        val playerWest =
            List(nCardsInHand) { i -> gm.game.getCardPlayer(TablePosition.WEST).getCardsInHand().elementAtOrNull(i) }
        val playerNorth =
            List(nCardsInHand) { i -> gm.game.getCardPlayer(TablePosition.NORTH).getCardsInHand().elementAtOrNull(i) }
        val playerEast =
            List(nCardsInHand) { i -> gm.game.getCardPlayer(TablePosition.EAST).getCardsInHand().elementAtOrNull(i) }

        //todo: add legal cards to play for player to move, can be used n 'setClickable' in board-scripts.js

        val gameJsonString = "" //Gson().toJson(gm)

        val goingUp = (gm.game as GameHearts).isGoingUp()
        val geniusValueSouth = List(gm.game.initialNumberOfCardsInHand()) { i ->
            getGeniusCardValue(
                (gm.game.getCardPlayer(TablePosition.SOUTH) as GeniusHeartsPlayer),
                gm.game.getCardPlayer(TablePosition.SOUTH).getCardsInHand().elementAtOrNull(i)
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
        val playerToMove = gm.game.getPlayerToMove()
        val suggestedCardToPlay = playerToMove.chooseCard()
        return executeMove(suggestedCardToPlay.color, suggestedCardToPlay.rank)
    }

    fun executeMove(color: CardColor, rank: CardRank): CardPlayedModel? {
        val playerToMove = gm.game.getPlayerToMove()
        val suggestedCardToPlay = Card(color, rank)
        if (!gm.isLegalCardToPlay(playerToMove, suggestedCardToPlay))
            return null

        val cardsStillInHand = playerToMove.getCardsInHand().size

        gm.playCard(suggestedCardToPlay)

        val trickCompleted = if (gm.game.trickCompleted())
            TrickCompletedModel(
                gm.game.getLastTrickWinner()!!.tablePosition,
                gm.game.roundCompleted(),
                gm.game.isFinished(),
            )
        else
            null

        val nextPlayer = gm.game.getPlayerToMove()

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
            gm.game.getCumulativeScorePerRound()
                .map { spr -> PlayerScore(
                    spr.southValue,
                    spr.westValue,
                    spr.eastValue,
                    spr.northValue)
                }
        )
    }

}