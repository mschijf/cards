package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.TablePosition

open class PlayerHearts (
    tablePosition: TablePosition,
    game: GameHearts) : Player(tablePosition, game) {

    override fun chooseCard(): Card {
        return getCardsInHand()
            .legalPlayable(
                game.getCurrentRound().getTrickOnTable().getCardsPlayed().map{it.card}
            )
        .first()
    }
}