package com.cards.game.fourplayercardgame.hearts.player

import com.cards.game.card.Card
import com.cards.game.fourplayercardgame.basic.player.Player
import com.cards.game.fourplayercardgame.basic.TableSide
import com.cards.game.fourplayercardgame.hearts.GameHearts
import com.cards.game.fourplayercardgame.hearts.legalPlayable

open class PlayerHearts (tableSide: TableSide, game: GameHearts) : Player(tableSide, game) {

    override fun chooseCard(): Card {
        return getCardsInHand()
            .legalPlayable(
                game.getCurrentRound().getTrickOnTable().getCardsPlayed()
            )
        .first()
    }
}