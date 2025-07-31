package com.cards.game.fourplayercardgame.klaverjassen

import com.cards.game.card.Card
import com.cards.game.card.CardColor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KLAVERJASSENTest {


    @Test
    fun test50low() {
        val trick = listOf("7H", "8H", "9H", "10H").map { str -> Card.of(str) }
        assertEquals(50, KLAVERJASSEN.bonusValue(trick, CardColor.CLUBS))
        assertEquals(50, KLAVERJASSEN.bonusValue(trick, CardColor.HEARTS))
    }

    @Test
    fun test50high() {
        val trick = listOf("JH", "AH", "KH", "QH").map { str -> Card.of(str) }
        assertEquals(50, KLAVERJASSEN.bonusValue(trick, CardColor.CLUBS))
        assertEquals(70, KLAVERJASSEN.bonusValue(trick, CardColor.HEARTS))
    }

    @Test
    fun test20_left() {
        val trick = listOf("7H", "8H", "9H", "JH").map { str -> Card.of(str) }
        assertEquals(20, KLAVERJASSEN.bonusValue(trick, CardColor.CLUBS))
        assertEquals(20, KLAVERJASSEN.bonusValue(trick, CardColor.HEARTS))
    }

    @Test
    fun test20_right() {
        val trick = listOf("7H", "10H", "JH", "QH").map { str -> Card.of(str) }
        assertEquals(20, KLAVERJASSEN.bonusValue(trick, CardColor.CLUBS))
        assertEquals(20, KLAVERJASSEN.bonusValue(trick, CardColor.HEARTS))
    }

    @Test
    fun test40() {
        val trick = listOf("7H", "JH", "QH", "KH").map { str -> Card.of(str) }
        assertEquals(20, KLAVERJASSEN.bonusValue(trick, CardColor.CLUBS))
        assertEquals(40, KLAVERJASSEN.bonusValue(trick, CardColor.HEARTS))
    }

    @Test
    fun test20_stuk() {
        val trick = listOf("7C", "JC", "QH", "KH").map { str -> Card.of(str) }
        assertEquals(0, KLAVERJASSEN.bonusValue(trick, CardColor.CLUBS))
        assertEquals(20, KLAVERJASSEN.bonusValue(trick, CardColor.HEARTS))
    }

    @Test
    fun testNoBonus() {
        val trick = listOf("7C", "8C", "9H", "10C").map { str -> Card.of(str) }
        assertEquals(0, KLAVERJASSEN.bonusValue(trick, CardColor.CLUBS))
        assertEquals(0, KLAVERJASSEN.bonusValue(trick, CardColor.HEARTS))
    }

    @Test
    fun testOneMore20() {
        val trick = listOf("7C", "8C", "9H", "9C").map { str -> Card.of(str) }
        assertEquals(20, KLAVERJASSEN.bonusValue(trick, CardColor.CLUBS))
        assertEquals(20, KLAVERJASSEN.bonusValue(trick, CardColor.HEARTS))
    }

    @Test
    fun test100King() {
        val trick = listOf("KC", "KH", "KD", "KS").map { str -> Card.of(str) }
        assertEquals(100, KLAVERJASSEN.bonusValue(trick, CardColor.CLUBS))
        assertEquals(100, KLAVERJASSEN.bonusValue(trick, CardColor.HEARTS))
    }

    @Test
    fun test100Seven() {
        val trick = listOf("7C", "7H", "7D", "7S").map { str -> Card.of(str) }
        assertEquals(100, KLAVERJASSEN.bonusValue(trick, CardColor.CLUBS))
        assertEquals(100, KLAVERJASSEN.bonusValue(trick, CardColor.HEARTS))
    }

    @Test
    fun test100Jack() {
        val trick = listOf("JC", "JH", "JD", "JS").map { str -> Card.of(str) }
        assertEquals(100, KLAVERJASSEN.bonusValue(trick, CardColor.CLUBS))
        assertEquals(100, KLAVERJASSEN.bonusValue(trick, CardColor.HEARTS))
    }


}