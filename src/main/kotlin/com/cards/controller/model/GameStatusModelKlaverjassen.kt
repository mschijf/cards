package com.cards.controller.model

import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.Table

data class GameStatusModelKlaverjassen(
    val generic: GameStatusModel,
    val trumpColor: CardColor,
    val contractOwner: Table
)
