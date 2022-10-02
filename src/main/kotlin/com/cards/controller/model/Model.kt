package com.cards.controller.model

class Model()  {
    val onTable = TableModel(
        null,
        CardModel(CardColor.SPADES, CardRank.EIGHT),
        CardModel(CardColor.HEARTS, CardRank.SEVEN),
        null)

    val playerSouth = arrayListOf(
            CardModel(CardColor.HEARTS, CardRank.ACE),
            CardModel(CardColor.HEARTS, CardRank.KING),
            CardModel(CardColor.HEARTS, CardRank.QUEEN),
            CardModel(CardColor.HEARTS, CardRank.JACK),
            CardModel(CardColor.HEARTS, CardRank.TEN),
            CardModel(CardColor.HEARTS, CardRank.NINE),
            CardModel(CardColor.HEARTS, CardRank.EIGHT),
            null
        )
    val playerWest = emptyArray<CardModel>()
    val playerNorth = emptyArray<CardModel>()
    val playerEast = emptyArray<CardModel>()
}

data class TableModel(
    val south: CardModel?,
    val west: CardModel?,
    val north: CardModel?,
    val east: CardModel?)
data class CardModel(
    val color: CardColor,
    val rank: CardRank)

