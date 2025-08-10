package com.cards.controller.klaverjassen.model

import com.cards.game.card.CardColor
import com.cards.game.fourplayercardgame.basic.TablePosition

data class TrumpChoiceModel(
    val trumpColor: CardColor,
    val contractOwner: TablePosition,
)
