package com.cards.controller

import com.cards.controller.model.CardPlayedModel
import com.cards.controller.model.CardPlayedResponse
import com.cards.controller.model.GameStatusModel
import com.cards.controller.model.ScoreModel
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.tools.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView


@RestController
class HtmlController() {

    @GetMapping("/")
    fun home(model: ModelMap): ModelAndView {
        //model.addAttribute("attribute", "redirectWithRedirectPrefix")
        return ModelAndView("redirect:/klaverjassen.html", model)
    }
}


