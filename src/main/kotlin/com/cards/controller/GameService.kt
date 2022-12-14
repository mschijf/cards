package com.cards.controller

import com.cards.controller.model.CardPlayedModel
import com.cards.controller.model.GameStatusModel
import com.cards.controller.model.ScoreModel
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.hearts.GameMaster
import org.springframework.stereotype.Service

@Service
class GameService {
    private var gm = GameMaster()

    fun newGame(): GameStatusModel {
        gm = GameMaster()
        return getGameStatus()
    }

    fun getGameStatus(): GameStatusModel {
        return GameStatusModel(gm)
    }

    fun computeMove(): CardPlayedModel {
        val playerToMove = gm.game.getPlayerToMove()
        val suggestedCardToPlay = gm.getCardPlayer(playerToMove).chooseCard()
        gm.playCard(suggestedCardToPlay)
        val gameStatusAfterLastMove = gm.game.getStatusAfterLastMove()
        val nextPlayer = gm.game.getPlayerToMove()
        return CardPlayedModel(playerToMove, suggestedCardToPlay, nextPlayer, gameStatusAfterLastMove)
    }

    fun executeMove(color: CardColor, rank: CardRank): CardPlayedModel? {
        val playerToMove = gm.game.getPlayerToMove()
        val suggestedCardToPlay = Card(color, rank)
        if (!gm.legalCardToPlay(playerToMove, suggestedCardToPlay))
            return null

        gm.playCard(suggestedCardToPlay)
        val gameStatusAfterLastMove = gm.game.getStatusAfterLastMove()
        val nextPlayer = gm.game.getPlayerToMove()
        return CardPlayedModel(playerToMove, suggestedCardToPlay, nextPlayer, gameStatusAfterLastMove)
    }

    fun getScoreCard(): ScoreModel {
        return ScoreModel(gm.game.getCumulativeScorePerRound())
    }

}