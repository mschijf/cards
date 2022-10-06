package com.cards.controller

import com.cards.controller.model.CardPlayedModel
import com.cards.controller.model.GameStatusModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

const val REQUESTPATH_BASE = "/api/v1/"

@RestController
@RequestMapping(REQUESTPATH_BASE)
class Controller @Autowired constructor(private val gameService: GameService) {

    @GetMapping("/game-status")
    fun getGameStatus(): GameStatusModel {
        return gameService.getGameStatus()
    }

    @PostMapping("/computeMove")
    fun computeMove(): CardPlayedModel {
        return gameService.computeMove()
    }
}


