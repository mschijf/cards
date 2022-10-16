package com.cards.game.hearts

import com.google.gson.Gson
import org.junit.jupiter.api.Test

internal class GameMasterTest {

    @Test
    fun testJson() {
        val jsonString = "{\"game\":{\"leadPlayer\":\"WEST\",\"goingDownFromRoundNumber\":2147483647,\"completedRoundList\":[],\"currentRound\":{\"completedTrickList\":[{\"leadPlayer\":\"WEST\",\"cardsPlayed\":[{\"player\":\"WEST\",\"card\":{\"color\":\"CLUBS\",\"rank\":\"NINE\"}},{\"player\":\"NORTH\",\"card\":{\"color\":\"CLUBS\",\"rank\":\"KING\"}},{\"player\":\"EAST\",\"card\":{\"color\":\"CLUBS\",\"rank\":\"SEVEN\"}},{\"player\":\"SOUTH\",\"card\":{\"color\":\"CLUBS\",\"rank\":\"QUEEN\"}}],\"playerToMove\":\"WEST\"}],\"currentTrick\":{\"leadPlayer\":\"NORTH\",\"cardsPlayed\":[],\"playerToMove\":\"NORTH\"}}},\"playerList\":[{\"player\":\"SOUTH\",\"cardsInHand\":[{\"color\":\"SPADES\",\"rank\":\"KING\"},{\"color\":\"HEARTS\",\"rank\":\"SEVEN\"},{\"color\":\"HEARTS\",\"rank\":\"TEN\"},{\"color\":\"HEARTS\",\"rank\":\"QUEEN\"},{\"color\":\"CLUBS\",\"rank\":\"EIGHT\"},{\"color\":\"DIAMONDS\",\"rank\":\"SEVEN\"},{\"color\":\"DIAMONDS\",\"rank\":\"EIGHT\"}]},{\"player\":\"WEST\",\"cardsInHand\":[{\"color\":\"HEARTS\",\"rank\":\"NINE\"},{\"color\":\"HEARTS\",\"rank\":\"JACK\"},{\"color\":\"CLUBS\",\"rank\":\"TEN\"},{\"color\":\"CLUBS\",\"rank\":\"JACK\"},{\"color\":\"CLUBS\",\"rank\":\"ACE\"},{\"color\":\"DIAMONDS\",\"rank\":\"TEN\"},{\"color\":\"DIAMONDS\",\"rank\":\"ACE\"}]},{\"player\":\"NORTH\",\"cardsInHand\":[{\"color\":\"SPADES\",\"rank\":\"SEVEN\"},{\"color\":\"SPADES\",\"rank\":\"EIGHT\"},{\"color\":\"SPADES\",\"rank\":\"TEN\"},{\"color\":\"HEARTS\",\"rank\":\"EIGHT\"},{\"color\":\"HEARTS\",\"rank\":\"ACE\"},{\"color\":\"DIAMONDS\",\"rank\":\"NINE\"},{\"color\":\"DIAMONDS\",\"rank\":\"KING\"}]},{\"player\":\"EAST\",\"cardsInHand\":[{\"color\":\"SPADES\",\"rank\":\"NINE\"},{\"color\":\"SPADES\",\"rank\":\"JACK\"},{\"color\":\"SPADES\",\"rank\":\"QUEEN\"},{\"color\":\"SPADES\",\"rank\":\"ACE\"},{\"color\":\"HEARTS\",\"rank\":\"KING\"},{\"color\":\"DIAMONDS\",\"rank\":\"JACK\"},{\"color\":\"DIAMONDS\",\"rank\":\"QUEEN\"}]}]}\n"

        val gm = Gson().fromJson(jsonString, GameMaster::class.java)
        gm.resetGameForPlayers()

        println(jsonString)
        println(Gson().toJson(gm))
    }
}