package com.cards.controller

import com.cards.controller.model.Model
import com.cards.game.hearts.GameMaster
import com.cards.game.hearts.Player
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

const val REQUESTPATH_BASE = "/api/v1/"

@RestController
@RequestMapping(REQUESTPATH_BASE)
class Controller @Autowired constructor(private val gameService: GameService) {

    @GetMapping("/game-status")
    fun getGameStatus(): Model {
        val gm = GameMaster()
        gm.playCard(gm.getHeartsPlayer(Player.SOUTH).cardsInHand[0])
        gm.playCard(gm.getHeartsPlayer(Player.WEST).cardsInHand[1])
        return Model(gm)
    }

}


