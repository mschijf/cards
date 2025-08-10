package com.cards.game.fourplayercardgame.klaverjassen.ai

import com.cards.game.card.*
import com.cards.game.fourplayercardgame.basic.Player
import com.cards.game.fourplayercardgame.klaverjassen.*

class ChooseCardAnalyzer(
    private val playerForWhichWeAnalyse: GeniusPlayerKlaverjassen) {

    private lateinit var currentRound: RoundKlaverjassen
    private lateinit var trumpColor: CardColor

    private lateinit var playerCanHave: Map<Player, MutableSet<Card>>
    private lateinit var playerSureHas: Map<Player, MutableSet<Card>>
    private lateinit var playerProbablyHas: Map<Player, MutableSet<Card>>
    private lateinit var playerProbablyHasNot: Map<Player, MutableSet<Card>>

    private val cardsPlayedDuringAnalysis = mutableListOf<Card>()

    fun refreshAnalysis() {
        initVariables()
        determinePlayerCanHaveCards()
        updateAfterAnalysis()
    }

    private fun initVariables() {
        currentRound = playerForWhichWeAnalyse.getCurrentRound()
        trumpColor = currentRound.getTrumpColor()

        val allPlayers = playerForWhichWeAnalyse.getOtherPlayers() + playerForWhichWeAnalyse
        playerCanHave = allPlayers.associateWith { mutableSetOf() }
        playerSureHas = allPlayers.associateWith { mutableSetOf() }
        playerProbablyHas = allPlayers.associateWith { mutableSetOf() }
        playerProbablyHasNot = allPlayers.associateWith { mutableSetOf() }

        cardsPlayedDuringAnalysis.clear()
    }

    fun playerCanHaveCards(player: Player): Set<Card> = playerCanHave[player]!!
    fun playerSureHasCards(player: Player): Set<Card> = playerSureHas[player]!!

    private fun updateAfterAnalysis() {

        //canHave -- no assumptions
        //sureHas -- no assumptions (geen overlap met welke canhave dan ook)

        //probablyHas -- assumption moet ook in canHave of SureHas zitten
        //probablyHasNot -- assumption moet ook in canHave of SureHas zitten (en heeft geen overlap met probablyHas)

        // 1. als speler enige is met een kaart in can have, dan is het een sure have
        //        remove it from canhave and add it to sureHas
        // 2. remove all cards in probablys that are not in canhave/surehas
        // 3. if number of canhaves+surehas == cards in hand, then everything = surehas

        updateOwn()
        equalizeSureHasWithCanHave()
        removeImpossibles()
    }

    private fun updateOwn() {
        playerCanHave[playerForWhichWeAnalyse]!!.clear()
        playerSureHas[playerForWhichWeAnalyse]!!.addAll(playerForWhichWeAnalyse.getCardsInHand())
        playerProbablyHas[playerForWhichWeAnalyse]!!.clear()
        playerProbablyHasNot[playerForWhichWeAnalyse]!!.clear()
    }

    private fun equalizeSureHasWithCanHave() {

        do {
            val sumSureHas0 = playerSureHas.values.fold(emptySet<Card>()) { acc, sureHasCards -> acc + sureHasCards }

            //update sureHas --> if a player has cards in canhave that all other players don't have, then it becomes a sureHas
            val allPlayers = playerForWhichWeAnalyse.getOtherPlayers() + playerForWhichWeAnalyse
            playerForWhichWeAnalyse.getOtherPlayers().forEach { otherPlayer ->
                val otherCanHave = (allPlayers - otherPlayer).flatMap { playerCanHave[it]!! + playerSureHas[it]!! }.toSet()
                val unique = playerCanHave[otherPlayer]!!.filterNot{card -> card in otherCanHave}
                playerSureHas[otherPlayer]!! += unique
                playerCanHave[otherPlayer]!! -= unique
            }

            //a sureHas can not appear in any other canHaves:
            val sumSureHas1 = playerSureHas.values.fold(emptySet<Card>()) { acc, sureHasCards -> acc + sureHasCards }
            allPlayers.forEach { player -> playerCanHave[player]!!.removeAll(sumSureHas1) }

            //if number of canHave + SureHas == number of cardsInHand
            playerForWhichWeAnalyse.getOtherPlayers().forEach { otherPlayer ->
                if (playerSureHas[otherPlayer]!!.size == otherPlayer.getNumberOfCardsInHand()) {
                    playerCanHave[otherPlayer]!!.clear()
                } else if ((playerSureHas[otherPlayer]!! + playerCanHave[otherPlayer]!!).size == otherPlayer.getNumberOfCardsInHand()) {
                    playerSureHas[otherPlayer]!! += playerCanHave[otherPlayer]!!
                    playerCanHave[otherPlayer]!!.clear()
                }
            }
            //a sureHas can not appear in any other canHaves:
            val sumSureHas2 = playerSureHas.values.fold(emptySet<Card>()) { acc, sureHasCards -> acc + sureHasCards }
            allPlayers.forEach { player -> playerCanHave[player]!!.removeAll(sumSureHas2) }
        } while (sumSureHas0.size != sumSureHas2.size)
    }

    private fun removeImpossibles() {
        val allPlayers = playerForWhichWeAnalyse.getOtherPlayers() + playerForWhichWeAnalyse
        allPlayers.forEach { player ->
            val impossibleCards = playerProbablyHasNot[player]!!.filterNot { card -> card in (playerCanHave[player]!! + playerSureHas[player]!!) }
            playerProbablyHas[player]!! -= impossibleCards
            playerProbablyHasNot[player]!! -= impossibleCards
        }
    }

    //-----------------------------------------------------------------------------------------------------------------

    private fun determinePlayerCanHaveCards() {

        playerForWhichWeAnalyse.getOtherPlayers().forEach { otherPlayer ->
            playerCanHave[otherPlayer]!!.addAll(CARDDECK.baseDeckCardsSevenAndHigher - playerForWhichWeAnalyse.getCardsInHand())
        }

        val allCards = CARDDECK.baseDeckCardsSevenAndHigher
        val allTricks = playerForWhichWeAnalyse.getCurrentRound().getCompletedTrickList() + currentRound.getTrickOnTable()
        allTricks.filterNot { trick -> trick.hasNotStarted() }.forEach { trick ->
            val firstCard = trick.getCardsPlayed().first().card
            playerForWhichWeAnalyse.getOtherPlayers().forEach {
                otherPlayer -> playerCanHave[otherPlayer]!! -= firstCard
            }

            val newTrick = TrickKlaverjassen(trick.getLeadPlayer(), currentRound)
            newTrick.addCard(firstCard)
            determineAssumptions(newTrick)
            cardsPlayedDuringAnalysis.add(firstCard)

            var highestTrumpUpTillNow = if (firstCard.color == trumpColor) firstCard else null
            trick.getCardsPlayed().drop(1).forEach { playerPlayedCard ->

                playerForWhichWeAnalyse.getOtherPlayers().forEach { otherPlayer ->
                    playerCanHave[otherPlayer]!! -= playerPlayedCard.card
                }

                if (playerPlayedCard.card.color != firstCard.color) {
                    playerCanHave[playerPlayedCard.player]!! -= allCards.ofColor(firstCard.color)
                    if (playerPlayedCard.card.color != trumpColor) {
                        playerCanHave[playerPlayedCard.player]!! -= allCards.ofColor(trumpColor)
                    } else {
                        if (!playerPlayedCard.card.beats(highestTrumpUpTillNow, trumpColor)) {
                            playerCanHave[playerPlayedCard.player]!! -= allCards.filter {
                                it.beats(
                                    highestTrumpUpTillNow,
                                    trumpColor
                                )
                            }
                        }
                    }
                } else if (playerPlayedCard.card.color == trumpColor && highestTrumpUpTillNow!!.beats(playerPlayedCard.card, trumpColor)) {
                    playerCanHave[playerPlayedCard.player]!! -= allCards.filter {
                        it.beats(
                            highestTrumpUpTillNow,
                            trumpColor
                        )
                    }
                } else {
                    //player just follows, we can not conclude anything yet
                }

                newTrick.addCard(playerPlayedCard.card)
                determineAssumptions(newTrick)
                cardsPlayedDuringAnalysis.add(playerPlayedCard.card)

                if (playerPlayedCard.card.color == trumpColor && playerPlayedCard.card.beats(highestTrumpUpTillNow, trumpColor))
                    highestTrumpUpTillNow = playerPlayedCard.card

            }
        }
    }

    private fun determineAssumptions(trickSoFar: TrickKlaverjassen) {
        val playerJustMoved = trickSoFar.getCardsPlayed().last().player as PlayerKlaverjassen
        val cardJustPlayed = trickSoFar.getCardsPlayed().last().card

        if (trickSoFar.isLeadPLayer(playerJustMoved) && currentRound.isContractOwner(playerJustMoved)) {
            if (noRealTrumpsPlayed()) {
                if (cardJustPlayed.color == trumpColor) {
                    if (cardJustPlayed.isJack(trumpColor)) {
                        if (cardJustPlayed.isNine(trumpColor)) {
                            addProbablyHas(playerJustMoved, Card(trumpColor, CardRank.JACK))
                        } else {
                            //speler speelt geen boer en geen negen: probeert boer er uit te krijgen, om eigen nel hoog te maken
                            addProbablyHasNot(playerJustMoved, Card(trumpColor, CardRank.JACK))
                            addProbablyHas(playerJustMoved, Card(trumpColor, CardRank.NINE))
                        }
                    }
                } else {
                    //speler komt met andere kaart dan de boer en ook geen troef
                    //todo: wat als het een aas is? (poging eerst roem te spelen, voordat er troef wordt getrokken)?
                    addProbablyHasNot(playerJustMoved, Card(trumpColor, CardRank.JACK))
                }
            }
        }

        //seinen
        if (playerJustMoved.isPartner(trickSoFar.getWinner())) {
            if (cardJustPlayed.color != trickSoFar.getLeadColor() && cardJustPlayed.color != trickSoFar.getWinningCard()!!.color && cardJustPlayed.color != trumpColor) {
                val highestCard = highestOfColorStillAvailable(cardJustPlayed.color)
                if (cardJustPlayed.toRankNumberNoTrump() <= Card(cardJustPlayed.color, CardRank.NINE).toRankNumberNoTrump()) {
                    if (highestCard != null)
                        addProbablyHas(playerJustMoved, highestCard)
                } else if (cardJustPlayed == highestCard) {
                    val secondHighest = secondHighestOfColorStillAvailable(cardJustPlayed.color)
                    if (secondHighest != null)
                        addProbablyHas(playerJustMoved, secondHighest)
                }
            }
        }

        if (!trickSoFar.isLeadColor(trumpColor) && trickSoFar.isLeadColor(cardJustPlayed.color) && !playerJustMoved.isPartner(trickSoFar.getWinner())) {
            if (!cardJustPlayed.isTrumpCard() && cardJustPlayed.isTen() )
                if (playerCanHave[playerJustMoved]!!.count { it.color == cardJustPlayed.color } > 1) {
                    //kale 10 --> dus heeft die kleur verder niet meer (of roem ontwijken ==> nog checken)
                    //todo: (of roem ontwijken ==> nog checken)
                    addProbablyHasNot(playerJustMoved, playerCanHave[playerJustMoved]!!.filter { it.color == cardJustPlayed.color })
                }
        }

        if (trickSoFar.isComplete()) { //playerToMove is last player in this trick that played a card
            if (trickSoFar.getWinner() != playerJustMoved && trickSoFar.getWinner() != playerJustMoved.getPartner()) {
                if (roemWeggegevenDoorLastPlayer(trickSoFar)) { //roem weggegeven
                    val bonusAfter = trickSoFar.getScore().getBonusForPlayer(playerJustMoved.nextPlayer())
                    trickSoFar.removeLastCard()
                    playerCanHave[playerJustMoved]!!.filter { it.color == cardJustPlayed.color }.forEach { otherCard ->
                        if (otherCard != cardJustPlayed) {
                            trickSoFar.addCard(otherCard)
                            if (trickSoFar.getScore().getBonusForPlayer(playerJustMoved.nextPlayer()) < bonusAfter) {
                                addProbablyHasNot(playerJustMoved, otherCard)
                            }
                            trickSoFar.removeLastCard()
                        }
                    }
                    trickSoFar.addCard(cardJustPlayed)
                }
            } else {
                if (roemOntwekenDoorLastPlayer(trickSoFar)) { //roem niet gemaakt
                    val bonusAfter = trickSoFar.getScore().getBonusForPlayer(playerJustMoved)
                    trickSoFar.removeLastCard()
                    playerCanHave[playerJustMoved]!!.filter { it.color == cardJustPlayed.color }.forEach { otherCard ->
                        if (otherCard != cardJustPlayed) {
                            trickSoFar.addCard(otherCard)
                            if (trickSoFar.getScore().getBonusForPlayer(playerJustMoved) > bonusAfter) {
                                addProbablyHasNot(playerJustMoved, otherCard)
                            }
                            trickSoFar.removeLastCard()
                        }
                    }
                    trickSoFar.addCard(cardJustPlayed)
                }
            }
        }
    }

    private fun roemWeggegevenDoorLastPlayer(trickSoFar: TrickKlaverjassen): Boolean {
        val playerJustMoved = trickSoFar.getCardsPlayed().last().player
        val cardJustPlayed = trickSoFar.getCardsPlayed().last().card
        val bonusAfter = trickSoFar.getScore().getBonusForPlayer(playerJustMoved.nextPlayer())
        trickSoFar.removeLastCard()
        val bonusBefore = trickSoFar.getScore().getBonusForPlayer(playerJustMoved.nextPlayer())
        trickSoFar.addCard(cardJustPlayed)
        return bonusAfter > bonusBefore
    }

    private fun roemOntwekenDoorLastPlayer(trickSoFar: TrickKlaverjassen): Boolean {
        val playerJustMoved = trickSoFar.getCardsPlayed().last().player
        val cardJustPlayed = trickSoFar.getCardsPlayed().last().card
        val bonusAfter = trickSoFar.getScore().getBonusForPlayer(playerJustMoved)
        trickSoFar.removeLastCard()
        val bonusBefore = trickSoFar.getScore().getBonusForPlayer(playerJustMoved)
        trickSoFar.addCard(cardJustPlayed)
        return bonusAfter == bonusBefore
    }


    private fun Card.isTrumpCard(): Boolean = this.color == trumpColor

    private fun noRealTrumpsPlayed() =
        (cardsPlayedDuringAnalysis.count { it.color == trumpColor } == 0)

    private fun highestOfColorStillAvailable(cardColor: CardColor): Card? {
        val cardsStillAvailable = (CARDDECK.baseDeckCardsSevenAndHigher - cardsPlayedDuringAnalysis)
        return if (cardColor == trumpColor)
            cardsStillAvailable.filter { it.color == cardColor }.maxByOrNull { it.toRankNumberTrump() }
        else
            cardsStillAvailable.filter { it.color == cardColor }.maxByOrNull { it.toRankNumberNoTrump() }
    }

    private fun secondHighestOfColorStillAvailable(cardColor: CardColor): Card? {
        val cardsStillAvailable = (CARDDECK.baseDeckCardsSevenAndHigher - cardsPlayedDuringAnalysis)
        val sortedCards = if (cardColor == trumpColor)
            cardsStillAvailable.filter { it.color == cardColor }.sortedByDescending { it.toRankNumberTrump() }
        else
            cardsStillAvailable.filter { it.color == cardColor }.sortedByDescending { it.toRankNumberNoTrump() }

        return if (sortedCards.size > 1)
            sortedCards[1]
        else
            null
    }

    private fun addProbablyHas(player: Player, card: Card) {
        playerProbablyHas[player]!! += card
        playerProbablyHasNot[player]!! -= card
    }
    private fun addProbablyHasNot(player: Player, card: Card) {
        playerProbablyHas[player]!! -= card
        playerProbablyHasNot[player]!! += card
    }
    private fun addProbablyHasNot(player: Player, cardList: List<Card>) {
        playerProbablyHas[player]!! -= cardList
        playerProbablyHasNot[player]!! += cardList
    }

}