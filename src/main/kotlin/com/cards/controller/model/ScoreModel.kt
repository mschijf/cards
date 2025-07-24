package com.cards.controller.model

data class ScoreModel(val scoreList: List<PlayerScore>)

data class PlayerScore (val south: Int, val west: Int, val east: Int, val north: Int)