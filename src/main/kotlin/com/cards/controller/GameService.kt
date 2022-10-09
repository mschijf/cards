package com.cards.controller

import com.cards.controller.model.CardPlayedModel
import com.cards.controller.model.GameStatusModel
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.hearts.GameMaster
import org.springframework.stereotype.Service

@Service
class GameService {
    val gm = GameMaster()

    fun getGameStatus(): GameStatusModel {
        return GameStatusModel(gm)
    }

    fun computeMove(): CardPlayedModel {
        val playerToMove = gm.game.getPlayerToMove()
        val suggestedCardToPlay = gm.getCardPlayer(playerToMove).chooseCard()
        gm.playCard(suggestedCardToPlay)
        val gameStatusAfterLastMove = gm.game.getStatusAfterLastMove()
        val nextPlayer = gm.game.getPlayerToMove()
        return CardPlayedModel(playerToMove, suggestedCardToPlay, nextPlayer,
            gameStatusAfterLastMove.trickCompleted, gameStatusAfterLastMove.trickWinner, gameStatusAfterLastMove.roundCompleted)
    }

    fun executeMove(color: CardColor, rank: CardRank): CardPlayedModel? {
        val playerToMove = gm.game.getPlayerToMove()
        val suggestedCardToPlay = Card(color, rank)
        if (!gm.legalCardToPlay(playerToMove, suggestedCardToPlay))
            return null

        gm.playCard(suggestedCardToPlay)
        val gameStatusAfterLastMove = gm.game.getStatusAfterLastMove()
        val nextPlayer = gm.game.getPlayerToMove()
        return CardPlayedModel(playerToMove, suggestedCardToPlay, nextPlayer,
            gameStatusAfterLastMove.trickCompleted, gameStatusAfterLastMove.trickWinner, gameStatusAfterLastMove.roundCompleted)
    }
}