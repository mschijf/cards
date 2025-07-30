package com.cards.controller.model

data class ScoreModelKlaverjassen(val scoreList: List<RoundScoreKlaverjassen>)

data class RoundScoreKlaverjassen (val northSouthPoints: Int, val eastWestPoints: Int, val northSouthBonus: Int, val eastWestBonus: Int)