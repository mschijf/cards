package com.cards.controller

import com.cards.controller.model.CardPlayedModel
import com.cards.controller.model.GameStatusModel
import com.cards.controller.model.ScoreModel
import com.cards.game.card.Card
import com.cards.game.card.CardColor
import com.cards.game.card.CardRank
import com.cards.game.fourplayercardgame.hearts.GameHearts
import com.cards.game.fourplayercardgame.GameMaster
import com.cards.game.fourplayercardgame.Player
import com.cards.game.fourplayercardgame.hearts.GeniusHeartsPlayer
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class GameService {
    private lateinit var gm: GameMaster

    init {
        newGame(0)
    }

    fun newGame(seed: Int = Random.nextInt() ): GameStatusModel {
        val game = GameHearts(seed)
        val playerList = Player.values().map { p  -> GeniusHeartsPlayer(p, game) }
        gm = GameMaster( game, playerList )
        return getGameStatus()
    }

    fun getGameStatus(): GameStatusModel {
        return GameStatusModel.of(gm)
    }

    fun computeMove(): CardPlayedModel {
        val playerToMove = gm.game.getPlayerToMove()
        val suggestedCardToPlay = gm.getCardPlayer(playerToMove).chooseCard()
        val cardsStillInHand = gm.getCardPlayer(playerToMove).getCardsInHand().size

        gm.playCard(suggestedCardToPlay)

        val gameStatusAfterLastMove = gm.game.getStatusAfterLastMove()
        val nextPlayer = gm.game.getPlayerToMove()

        return CardPlayedModel.of(playerToMove, suggestedCardToPlay, nextPlayer,cardsStillInHand, gameStatusAfterLastMove)
    }

    fun executeMove(color: CardColor, rank: CardRank): CardPlayedModel? {
        val playerToMove = gm.game.getPlayerToMove()
        val suggestedCardToPlay = Card(color, rank)
        if (!gm.legalCardToPlay(playerToMove, suggestedCardToPlay))
            return null

        val cardsStillInHand = gm.getCardPlayer(playerToMove).getCardsInHand().size

        gm.playCard(suggestedCardToPlay)

        val gameStatusAfterLastMove = gm.game.getStatusAfterLastMove()
        val nextPlayer = gm.game.getPlayerToMove()

        return CardPlayedModel.of(playerToMove, suggestedCardToPlay, nextPlayer,cardsStillInHand, gameStatusAfterLastMove)
    }

    fun getScoreCard(): ScoreModel {
        return ScoreModel.of(gm.game.getCumulativeScorePerRound())
    }

}