package com.cards.controller

import com.cards.controller.model.GameStatusModelHearts
import com.cards.controller.model.RoundScoreHearts
import com.cards.controller.model.ScoreModelHearts
import com.cards.controller.model.CardInHandModel
import com.cards.controller.model.CardPlayedModel
import com.cards.controller.model.GameStatusModel
import com.cards.controller.model.TableModel
import com.cards.controller.model.TrickCompletedModel
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.hearts.GAME_START_PLAYER
import com.cards.game.hearts.Game
import com.cards.game.hearts.GameStatus
import com.cards.game.hearts.TableSide
import com.cards.game.hearts.legalPlayable
import com.cards.player.Player
import com.cards.player.PlayerGroup
import com.cards.player.ai.GeniusPlayerHearts
import com.cards.tools.RANDOMIZER
import org.springframework.stereotype.Service

@Service
class ServiceHearts {


    private var gameHearts = Game.startNewGame()

    private fun createInitialPlayerList(): List<Player> {
        return listOf(
            GeniusPlayerHearts(TableSide.WEST, gameHearts),
            GeniusPlayerHearts(TableSide.NORTH, gameHearts),
            GeniusPlayerHearts(TableSide.EAST, gameHearts),
            GeniusPlayerHearts(TableSide.SOUTH, gameHearts),
        )
    }

    private var playerGroup = PlayerGroup(createInitialPlayerList()).also { it.dealCards() }

    fun newGame(): GameStatusModelHearts {
        RANDOMIZER.setSeed(1960426776)
        gameHearts = Game.startNewGame()
        playerGroup = PlayerGroup(createInitialPlayerList())
        playerGroup.dealCards()
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
        val player = playerGroup.getPlayer(tableSide)
        return player
            .getCardsInHand()
            .sortedBy { card -> 100 * card.color.ordinal + card.rank.ordinal }
            .map { card ->
                CardInHandModel(
                    card,
                    isLegalCardToPlay(player, card),
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
        val playerToMove = playerGroup.getPlayer(gameHearts.getSideToMove())
        val suggestedCardToPlay = playerToMove.chooseCard()
        return executeMove(suggestedCardToPlay.color, suggestedCardToPlay.rank)
    }

    fun executeMove(color: CardColor, rank: CardRank): CardPlayedModel? {
        val sideToMove = gameHearts.getSideToMove()
        val playerToMove = playerGroup.getPlayer(sideToMove)
        val suggestedCardToPlay = Card(color, rank)
        if (!isLegalCardToPlay(playerToMove, suggestedCardToPlay))
            return null

        val cardsStillInHand = playerToMove.getNumberOfCardsInHand()

        val gameStatus = playCard(suggestedCardToPlay)

        val trickCompleted = if (gameStatus.trickFinished)
            TrickCompletedModel(
                gameHearts.getLastTrickWinner()!!,
                gameStatus.roundFinished,
                gameStatus.gameFinished,
            )
        else
            null


        val nextPlayer = if (gameStatus.gameFinished) GAME_START_PLAYER else gameHearts.getSideToMove()

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

    //======================================================================================================
    // added from GameMaster
    //======================================================================================================

    private fun isLegalCardToPlay(player: Player, card: Card): Boolean {
        val trickOnTable = gameHearts.getCurrentRound().getTrickOnTable()

        val cardsInHand = player.getCardsInHand()
        val legalCards = cardsInHand.legalPlayable(trickOnTable)
        return legalCards.contains(card)
    }

    private fun playCard(card: Card): GameStatus {
        val playerToMove = playerGroup.getPlayer(gameHearts.getSideToMove())
        playerToMove.removeCard(card)

        val gameStatus = gameHearts.playCard(card)
        if (playerGroup.allEmptyHanded())
            playerGroup.dealCards()
        return gameStatus
    }


}