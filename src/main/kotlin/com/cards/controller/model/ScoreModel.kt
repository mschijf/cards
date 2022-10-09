package com.cards.controller.model

import com.cards.game.hearts.Score

class ScoreModel(scorePerRound: List<Score>) {
    val scoreList = scorePerRound.map { spr -> PlayerScore(spr.getSouthValue(), spr.getWestValue(), spr.getEastValue(), spr.getNorthValue())}
}

data class PlayerScore (val south: Int, val west: Int, val east: Int, val north: Int)