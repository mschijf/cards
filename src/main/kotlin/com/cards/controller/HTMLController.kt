package com.cards.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class HTMLController() {
    @GetMapping("/")
    fun index(): String {
        return readFileDirectlyAsText("cards.html")
    }

    private fun readFileDirectlyAsText(fileName: String): String {
        val resource = this::class.java.getResourceAsStream("/html/$fileName")
        return resource.bufferedReader()?.readText()
    }
}


