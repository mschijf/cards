package com.cards.controller

import com.cards.controller.model.Model
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.servlet.http.Cookie

const val REQUESTPATH_BASE = "/api/v1/"

@RestController
@RequestMapping(REQUESTPATH_BASE)
class Controller @Autowired constructor(private val gameService: GameService) {

    @GetMapping("/game-status")
    fun getGameStatus(): Model {
        return Model()
    }

}


