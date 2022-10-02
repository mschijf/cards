package com.cards.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.servlet.http.Cookie

const val BOARD_COOKIE = "CARDSTATUS"
const val REQUESTPATH_BASE = "api/v1"

@RestController
@RequestMapping(REQUESTPATH_BASE)
class Controller @Autowired constructor(private val gameService: GameService) {

    private fun getNewCookie(persistanceString: String): Cookie {
        val cookie = Cookie(BOARD_COOKIE, persistanceString)
        cookie.maxAge = 3600*24*365
        cookie.path = "/"
        return cookie
    }
}


