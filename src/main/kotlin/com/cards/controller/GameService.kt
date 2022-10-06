package com.cards.controller

import com.cards.controller.model.CardPlayedModel
import com.cards.controller.model.GameStatusModel
import com.cards.game.hearts.GameMaster
import org.springframework.stereotype.Service

@Service
class GameService {
    val gm = GameMaster()

    fun getGameStatus(): GameStatusModel {
        return GameStatusModel(gm)
    }

    fun computeMove(): CardPlayedModel {
        val playerToMove = gm.game.getCurrentRound().getTrickOnTable().playerToMove()
        val calculatedMove = gm.getCardPlayer(playerToMove).chooseCard()
        val trickWinner = gm.playCard(calculatedMove)
        val trickCompleted = gm.game.getCurrentRound().getTrickOnTable().isNew()
        val roundCompleted = gm.game.getCurrentRound().isNew()
        return CardPlayedModel(playerToMove, calculatedMove, trickCompleted, trickWinner, roundCompleted)
    }
}