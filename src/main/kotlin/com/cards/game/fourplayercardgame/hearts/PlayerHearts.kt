package com.cards.game.fourplayercardgame.hearts

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.basic.TableSide

open class PlayerHearts (tableSide: TableSide, game: GameHearts) : Player(tableSide, game) {

    override fun chooseCard(): Card {
        return getCardsInHand()
            .legalPlayable(
                game.getCurrentRound().getTrickOnTable().getCardsPlayed()
            )
        .first()
    }
}