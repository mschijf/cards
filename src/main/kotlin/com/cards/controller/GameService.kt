package com.cards.controller

import com.cards.controller.model.CardPlayedModel
import com.cards.controller.model.GameStatusModel
import com.cards.controller.model.ScoreModel
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.hearts.Game
import com.cards.game.hearts.GameMaster
import com.cards.tools.Log
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class GameService {
    private var gm = GameMaster(Game(0))

    fun newGame(seed: Int = Random.nextInt() ): GameStatusModel {
        gm = GameMaster( Game(seed) )
        return getGameStatus()
    }

    fun getGameStatus(): GameStatusModel {
        return GameStatusModel(gm)
    }

    fun computeMove(): CardPlayedModel {
        val playerToMove = gm.game.getPlayerToMove()
        val suggestedCardToPlay = gm.getCardPlayer(playerToMove).chooseCard()
        val cardsStillInHand = gm.getCardPlayer(playerToMove).getCardsInHand().size

        gm.playCard(suggestedCardToPlay)

        val gameStatusAfterLastMove = gm.game.getStatusAfterLastMove()
        val nextPlayer = gm.game.getPlayerToMove()

        return CardPlayedModel(playerToMove, suggestedCardToPlay, nextPlayer, gameStatusAfterLastMove, cardsStillInHand)
    }

    fun executeMove(color: CardColor, rank: CardRank): CardPlayedModel? {
        val playerToMove = gm.game.getPlayerToMove()
        val suggestedCardToPlay = Card(color, rank)
        if (!gm.legalCardToPlay(playerToMove, suggestedCardToPlay))
            return null

        val cardsStillInHand = gm.getCardPlayer(playerToMove).getCardsInHand().size

        gm.playCard(suggestedCardToPlay)

        val gameStatusAfterLastMove = gm.game.getStatusAfterLastMove()
        val nextPlayer = gm.game.getPlayerToMove()

        return CardPlayedModel(playerToMove, suggestedCardToPlay, nextPlayer, gameStatusAfterLastMove, cardsStillInHand)
    }

    fun getScoreCard(): ScoreModel {
        return ScoreModel.of(gm.game.getCumulativeScorePerRound())
    }

}