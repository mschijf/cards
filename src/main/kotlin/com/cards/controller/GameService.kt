package com.cards.controller

import com.cards.controller.model.CardPlayedModel
import com.cards.controller.model.GameStatusModel
import com.cards.controller.model.PlayerScore
import com.cards.controller.model.ScoreModel
import com.cards.controller.model.TableModel
import com.cards.controller.model.TrickCompletedModel
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.hearts.GameHearts
import com.cards.game.fourplayercardgame.GameMaster
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.hearts.GeniusHeartsPlayer
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class GameService {
    private var gm = createGameMaster()

    private fun createGameMaster(seed: Int = 0): GameMaster {
        val game = GameHearts(seed)
        val playerList = Player.values().map { p -> GeniusHeartsPlayer(p, game) }
        return GameMaster(GameHearts(seed), playerList)
    }

    fun newGame(seed: Int = Random.nextInt()): GameStatusModel {
        gm = createGameMaster(seed)
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

        val size = gm.game.rules.getInitialNumberOfCardsPerPlayer()
        val playerSouth = List(size) { i -> gm.getCardPlayer(Player.SOUTH).getCardsInHand().elementAtOrNull(i) }
        val playerWest = List(size) { i -> gm.getCardPlayer(Player.WEST).getCardsInHand().elementAtOrNull(i) }
        val playerNorth = List(size) { i -> gm.getCardPlayer(Player.NORTH).getCardsInHand().elementAtOrNull(i) }
        val playerEast = List(size) { i -> gm.getCardPlayer(Player.EAST).getCardsInHand().elementAtOrNull(i) }

        val seed = gm.game.getSeed()
        val gameJsonString = "" //Gson().toJson(gm)

        val goingUp = (gm.game as GameHearts).getGoingUp()
        val geniusValueSouth = List(gm.game.rules.getInitialNumberOfCardsPerPlayer()) { i ->
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
            seed,
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