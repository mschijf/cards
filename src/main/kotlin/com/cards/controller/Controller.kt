package com.cards.controller

import com.cards.controller.model.CardPlayedModel
import com.cards.controller.model.CardPlayedResponse
import com.cards.controller.model.GameStatusModel
import com.cards.controller.model.ScoreModel
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

const val REQUESTPATH_BASE = "/api/v1/"

@RestController
@RequestMapping(REQUESTPATH_BASE)
class Controller @Autowired constructor(private val gameService: GameService) {

    @PostMapping("/new-game")
    fun newGame(): GameStatusModel {
        return gameService.newGame()
    }

    @GetMapping("/game-status")
    fun getGameStatus(): GameStatusModel {
        return gameService.getGameStatus()
    }

    @GetMapping("/score-list")
    fun getScoreList(): ScoreModel {
        return gameService.getScoreCard()
    }

    @PostMapping("/computeMove")
    fun computeMove(): CardPlayedResponse {
        return createCardPlayResponse(gameService.computeMove())
    }

    @PostMapping("/executeMove/{color}/{rank}")
    fun executeMove(@PathVariable(name = "color") color: CardColor,
                    @PathVariable(name = "rank") rank: CardRank): CardPlayedResponse {
        return createCardPlayResponse(gameService.executeMove(color, rank))
    }

    private fun createCardPlayResponse(response: CardPlayedModel?): CardPlayedResponse {
        return if (response != null) CardPlayedResponse(true, response) else CardPlayedResponse(false)
    }
}


