package com.cards.controller

import com.cards.controller.model.CardPlayedModel
import com.cards.controller.model.GameStatusModel
import com.cards.controller.model.ScoreModel
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

const val REQUESTPATH_BASE = "/api/v1/"

@RestController
@RequestMapping(REQUESTPATH_BASE)
class Controller @Autowired constructor(private val gameService: GameService) {

    @GetMapping("/game-status")
    fun getGameStatus(): GameStatusModel {
        return gameService.getGameStatus()
    }

    @GetMapping("/score-list")
    fun getScoreList(): ScoreModel {
        return gameService.getScorePerRound()
    }

    @PostMapping("/computeMove")
    fun computeMove(): CardPlayedModel {
        return gameService.computeMove()
    }

    @PostMapping("/executeMove/{color}/{rank}")
    fun executeMove(@PathVariable(name = "color") color: CardColor,
                    @PathVariable(name = "rank") rank: CardRank): CardPlayedModel {
        return gameService.executeMove(color, rank)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Illegal Card")
    }
}


