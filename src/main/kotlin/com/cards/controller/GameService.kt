package com.cards.controller

import com.cards.controller.model.*
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.GameMaster
import com.cards.game.fourplayercardgame.Player
import com.cards.game.fourplayercardgame.Table
import com.cards.game.fourplayercardgame.hearts.GameHearts
import com.cards.game.fourplayercardgame.hearts.GeniusHeartsPlayer
import org.springframework.stereotype.Service

@Service
class GameService {
    private lateinit var gm: GameMaster

    init {
        newGame()
    }

    fun newGame(): GameStatusModel {
        val game = GameHearts()
        val playerList = Player.values().map { p -> GeniusHeartsPlayer(p, game) }
        gm = GameMaster(game, playerList)
        return getGameStatus()
    }

    fun getGameStatus(): GameStatusModel {
        val onTable = TableModel(
            gm.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.SOUTH),
            gm.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.WEST),
            gm.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.NORTH),
            gm.game.getCurrentRound().getTrickOnTable().getCardPlayedBy(Player.EAST)
        )
        val playerToMove = gm.game.getPlayerToMove()
        val leadPlayer = gm.game.getCurrentRound().getTrickOnTable().getLeadPlayer()

        val playerSouth =
            List(Table.nCardsInHand) { i -> gm.getCardPlayer(Player.SOUTH).getCardsInHand().elementAtOrNull(i) }
        val playerWest =
            List(Table.nCardsInHand) { i -> gm.getCardPlayer(Player.WEST).getCardsInHand().elementAtOrNull(i) }
        val playerNorth =
            List(Table.nCardsInHand) { i -> gm.getCardPlayer(Player.NORTH).getCardsInHand().elementAtOrNull(i) }
        val playerEast =
            List(Table.nCardsInHand) { i -> gm.getCardPlayer(Player.EAST).getCardsInHand().elementAtOrNull(i) }

        val gameJsonString = "" //Gson().toJson(gm)

        val goingUp = (gm.game as GameHearts).getGoingUp()
        val geniusValueSouth = List(Table.nCardsInHand) { i ->
            getGeniusCardValue(
                (gm.getCardPlayer(Player.SOUTH) as GeniusHeartsPlayer),
                gm.getCardPlayer(Player.SOUTH).getCardsInHand().elementAtOrNull(i)
            )
        }

        return GameStatusModel(
            onTable,
            playerToMove,
            leadPlayer,
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
        val suggestedCardToPlay = gm.getCardPlayer(playerToMove).chooseCard()
        return executeMove(suggestedCardToPlay.color, suggestedCardToPlay.rank)
    }

    fun executeMove(color: CardColor, rank: CardRank): CardPlayedModel? {
        val playerToMove = gm.game.getPlayerToMove()
        val suggestedCardToPlay = Card(color, rank)
        if (!gm.legalCardToPlay(playerToMove, suggestedCardToPlay))
            return null

        val cardsStillInHand = gm.getCardPlayer(playerToMove).getCardsInHand().size

        gm.playCard(suggestedCardToPlay)

        val gameStatusAfterLastMove = gm.game.getStatusAfterLastMove()
        val trickCompleted = if (gameStatusAfterLastMove.trickCompleted)
            TrickCompletedModel(
                gameStatusAfterLastMove.trickWinner!!,
                gameStatusAfterLastMove.roundCompleted,
                gameStatusAfterLastMove.gameFinished
            )
        else
            null

        val nextPlayer = gm.game.getPlayerToMove()

        return CardPlayedModel(
            playerToMove,
            suggestedCardToPlay,
            nextPlayer,
            cardsStillInHand,
            trickCompleted,
        )
    }

    fun getScoreCard(): ScoreModel {
        return ScoreModel (
            gm.game.getCumulativeScorePerRound()
                .map { spr -> PlayerScore(
                    spr.getSouthValue(),
                    spr.getWestValue(),
                    spr.getEastValue(),
                    spr.getNorthValue())
                }
        )
    }

}