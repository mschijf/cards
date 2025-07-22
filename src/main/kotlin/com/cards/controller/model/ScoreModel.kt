package com.cards.controller.model

import com.cards.game.hearts.Score

data class ScoreModel(val scoreList: List<PlayerScore>) {

    companion object {
        fun of(scorePerRound: List<Score>): ScoreModel {
            return ScoreModel (
                scorePerRound
                    .map { spr -> PlayerScore(
                            spr.getSouthValue(),
                            spr.getWestValue(),
                            spr.getEastValue(),
                            spr.getNorthValue())
                    }
            )
        }
    }
}

data class PlayerScore (val south: Int, val west: Int, val east: Int, val north: Int)