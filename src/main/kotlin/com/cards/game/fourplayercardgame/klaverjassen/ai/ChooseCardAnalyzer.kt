package com.cards.game.fourplayercardgame.klaverjassen.ai

import com.cards.game.card.*
import com.cards.game.fourplayercardgame.basic.TablePosition
import com.cards.game.fourplayercardgame.klaverjassen.*

class ChooseCardAnalyzer(
    private val playerForWhichWeAnalyse: GeniusPlayerKlaverjassen) {

    private lateinit var currentRound: RoundKlaverjassen
    private lateinit var trumpColor: CardColor

    private lateinit var playerCanHave: Map<TablePosition, MutableSet<Card>>
    private lateinit var playerSureHas: Map<TablePosition, MutableSet<Card>>
    private lateinit var playerProbablyHas: Map<TablePosition, MutableSet<Card>>
    private lateinit var playerProbablyHasNot: Map<TablePosition, MutableSet<Card>>


    private val allPositions = TablePosition.values().toSet()
    private val ownPosition = playerForWhichWeAnalyse.tablePosition
    private val otherPositions = allPositions - ownPosition

    private val cardsPlayedDuringAnalysis = mutableListOf<Card>()

    fun refreshAnalysis() {
        initVariables()
        determinePlayerCanHaveCards()
        updateAfterAnalysis()
    }

    private fun initVariables() {
        currentRound = playerForWhichWeAnalyse.getCurrentRound()
        trumpColor = currentRound.getTrumpColor()

        playerCanHave = allPositions.associateWith { mutableSetOf() }
        playerSureHas = allPositions.associateWith { mutableSetOf() }
        playerProbablyHas = allPositions.associateWith { mutableSetOf() }
        playerProbablyHasNot = allPositions.associateWith { mutableSetOf() }

        cardsPlayedDuringAnalysis.clear()
    }

    fun playerCanHaveCards(position: TablePosition): Set<Card> = playerCanHave[position]!!
    fun playerSureHasCards(position: TablePosition): Set<Card> = playerSureHas[position]!!

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
        playerCanHave[ownPosition]!!.clear()
        playerSureHas[ownPosition]!!.addAll(playerForWhichWeAnalyse.getCardsInHand())
        playerProbablyHas[ownPosition]!!.clear()
        playerProbablyHasNot[ownPosition]!!.clear()
    }

    private fun equalizeSureHasWithCanHave() {

        do {
            val sumSureHas0 = playerSureHas.values.fold(emptySet<Card>()) { acc, sureHasCards -> acc + sureHasCards }

            //update sureHas --> if a player has cards in canhave that all other players don't have, then it becomes a sureHas
            otherPositions.forEach { otherPosition ->
                val otherCanHave = (allPositions - otherPosition).flatMap { playerCanHave[it]!! + playerSureHas[it]!! }.toSet()
                val unique = playerCanHave[otherPosition]!!.filterNot{card -> card in otherCanHave}
                playerSureHas[otherPosition]!! += unique
                playerCanHave[otherPosition]!! -= unique
            }

            //a sureHas can not appear in any other canHaves:
            val sumSureHas1 = playerSureHas.values.fold(emptySet<Card>()) { acc, sureHasCards -> acc + sureHasCards }
            allPositions.forEach { player -> playerCanHave[player]!!.removeAll(sumSureHas1) }

            val playersPlayedInLastTrick = currentRound.getTrickOnTable().getCardsPlayed().map{ it.position}
            //if number of canHave + SureHas == number of cardsInHand
            otherPositions.forEach { otherPlayer ->
                val numberOfCardsInHandOtherPlayer = playerForWhichWeAnalyse.getCardsInHand().size - if (otherPlayer in playersPlayedInLastTrick) 1 else 0
                if (playerSureHas[otherPlayer]!!.size == numberOfCardsInHandOtherPlayer) {
                    playerCanHave[otherPlayer]!!.clear()
                } else if ((playerSureHas[otherPlayer]!! + playerCanHave[otherPlayer]!!).size == numberOfCardsInHandOtherPlayer) {
                    playerSureHas[otherPlayer]!! += playerCanHave[otherPlayer]!!
                    playerCanHave[otherPlayer]!!.clear()
                }
            }
            //a sureHas can not appear in any other canHaves:
            val sumSureHas2 = playerSureHas.values.fold(emptySet<Card>()) { acc, sureHasCards -> acc + sureHasCards }
            allPositions.forEach { player -> playerCanHave[player]!!.removeAll(sumSureHas2) }
        } while (sumSureHas0.size != sumSureHas2.size)
    }

    private fun removeImpossibles() {
        val allPositions = TablePosition.values()
        allPositions.forEach { position ->
            val impossibleCards = playerProbablyHasNot[position]!!.filterNot { card -> card in (playerCanHave[position]!! + playerSureHas[position]!!) }
            playerProbablyHas[position]!! -= impossibleCards
            playerProbablyHasNot[position]!! -= impossibleCards
        }
    }

    //-----------------------------------------------------------------------------------------------------------------

    private fun determinePlayerCanHaveCards() {

        val allCards = CARDDECK.baseDeckCardsSevenAndHigher

        otherPositions.forEach { otherPosition ->
            playerCanHave[otherPosition]!!.addAll(allCards - playerForWhichWeAnalyse.getCardsInHand())
        }

        val allTricks = playerForWhichWeAnalyse.getCurrentRound().getTrickList()
        allTricks.filterNot { trick -> trick.hasNotStarted() }.forEach { trick ->
            val firstCard = trick.getCardsPlayed().first().card
            val firstPosition = trick.getCardsPlayed().first().position
            otherPositions.forEach {
                otherPosition -> playerCanHave[otherPosition]!! -= firstCard
            }

            val newTrick = TrickKlaverjassen(firstPosition, currentRound)
            newTrick.addCard(firstPosition, firstCard)
            determineAssumptions(newTrick)
            cardsPlayedDuringAnalysis.add(firstCard)

            var highestTrumpUpTillNow = if (firstCard.color == trumpColor) firstCard else null
            trick.getCardsPlayed().drop(1).forEach { playerPlayedCard ->

                otherPositions.forEach { otherPosition ->
                    playerCanHave[otherPosition]!! -= playerPlayedCard.card
                }

                if (playerPlayedCard.card.color != firstCard.color) {
                    playerCanHave[playerPlayedCard.position]!! -= allCards.ofColor(firstCard.color)
                    if (playerPlayedCard.card.color != trumpColor) {
                        playerCanHave[playerPlayedCard.position]!! -= allCards.ofColor(trumpColor)
                    } else {
                        if (!playerPlayedCard.card.beats(highestTrumpUpTillNow, trumpColor)) {
                            playerCanHave[playerPlayedCard.position]!! -= allCards.filter {
                                it.beats(highestTrumpUpTillNow, trumpColor)
                            }
                        }
                    }
                } else if (playerPlayedCard.card.color == trumpColor && highestTrumpUpTillNow!!.beats(playerPlayedCard.card, trumpColor)) {
                    playerCanHave[playerPlayedCard.position]!! -= allCards.filter {
                        it.beats(highestTrumpUpTillNow, trumpColor)
                    }
                } else {
                    //player just follows, we can not conclude anything yet
                }

                newTrick.addCard(playerPlayedCard.position, playerPlayedCard.card)
                determineAssumptions(newTrick)
                cardsPlayedDuringAnalysis.add(playerPlayedCard.card)

                if (playerPlayedCard.card.color == trumpColor && playerPlayedCard.card.beats(highestTrumpUpTillNow, trumpColor))
                    highestTrumpUpTillNow = playerPlayedCard.card

            }
        }
    }

    private fun determineAssumptions(trickSoFar: TrickKlaverjassen) {
        val playerJustMoved = trickSoFar.getCardsPlayed().last().position
        val cardJustPlayed = trickSoFar.getCardsPlayed().last().card

        if (trickSoFar.isLeadPosition(playerJustMoved) && currentRound.isContractOwner(playerJustMoved)) {
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
        if (playerJustMoved.isOppositeOf(trickSoFar.getWinner())) {
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

        if (!trickSoFar.isLeadColor(trumpColor) && trickSoFar.isLeadColor(cardJustPlayed.color) && !playerJustMoved.isOppositeOf(trickSoFar.getWinner())) {
            if (!cardJustPlayed.isTrumpCard() && cardJustPlayed.isTen() )
                if (playerCanHave[playerJustMoved]!!.count { it.color == cardJustPlayed.color } > 1) {
                    //kale 10 --> dus heeft die kleur verder niet meer (of roem ontwijken ==> nog checken)
                    //todo: (of roem ontwijken ==> nog checken)
                    addProbablyHasNot(playerJustMoved, playerCanHave[playerJustMoved]!!.filter { it.color == cardJustPlayed.color })
                }
        }

        if (trickSoFar.isComplete()) { //playerToMove is last player in this trick that played a card
            if (trickSoFar.getWinner() != playerJustMoved && trickSoFar.getWinner() != playerJustMoved.opposite()) {
                if (roemWeggegevenDoorLastPlayer(trickSoFar)) { //roem weggegeven
                    val bonusAfter = trickSoFar.getScore().getBonusForPlayer(playerJustMoved.clockwiseNext())
                    trickSoFar.removeLastCard()
                    playerCanHave[playerJustMoved]!!.filter { it.color == cardJustPlayed.color }.forEach { otherCard ->
                        if (otherCard != cardJustPlayed) {
                            trickSoFar.addCard(playerJustMoved, otherCard)
                            if (trickSoFar.getScore().getBonusForPlayer(playerJustMoved.clockwiseNext()) < bonusAfter) {
                                addProbablyHasNot(playerJustMoved, otherCard)
                            }
                            trickSoFar.removeLastCard()
                        }
                    }
                    trickSoFar.addCard(playerJustMoved, cardJustPlayed)
                }
            } else {
                if (roemOntwekenDoorLastPlayer(trickSoFar)) { //roem niet gemaakt
                    val bonusAfter = trickSoFar.getScore().getBonusForPlayer(playerJustMoved)
                    trickSoFar.removeLastCard()
                    playerCanHave[playerJustMoved]!!.filter { it.color == cardJustPlayed.color }.forEach { otherCard ->
                        if (otherCard != cardJustPlayed) {
                            trickSoFar.addCard(playerJustMoved, otherCard)
                            if (trickSoFar.getScore().getBonusForPlayer(playerJustMoved) > bonusAfter) {
                                addProbablyHasNot(playerJustMoved, otherCard)
                            }
                            trickSoFar.removeLastCard()
                        }
                    }
                    trickSoFar.addCard(playerJustMoved, cardJustPlayed)
                }
            }
        }
    }

    private fun roemWeggegevenDoorLastPlayer(trickSoFar: TrickKlaverjassen): Boolean {
        val playerJustMoved = trickSoFar.getCardsPlayed().last().position
        val cardJustPlayed = trickSoFar.getCardsPlayed().last().card
        val bonusAfter = trickSoFar.getScore().getBonusForPlayer(playerJustMoved.clockwiseNext())
        trickSoFar.removeLastCard()
        val bonusBefore = trickSoFar.getScore().getBonusForPlayer(playerJustMoved.clockwiseNext())
        trickSoFar.addCard(playerJustMoved, cardJustPlayed)
        return bonusAfter > bonusBefore
    }

    private fun roemOntwekenDoorLastPlayer(trickSoFar: TrickKlaverjassen): Boolean {
        val playerJustMoved = trickSoFar.getCardsPlayed().last().position
        val cardJustPlayed = trickSoFar.getCardsPlayed().last().card
        val bonusAfter = trickSoFar.getScore().getBonusForPlayer(playerJustMoved)
        trickSoFar.removeLastCard()
        val bonusBefore = trickSoFar.getScore().getBonusForPlayer(playerJustMoved)
        trickSoFar.addCard(playerJustMoved, cardJustPlayed)
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

    private fun addProbablyHas(position: TablePosition, card: Card) {
        playerProbablyHas[position]!! += card
        playerProbablyHasNot[position]!! -= card
    }
    private fun addProbablyHasNot(position: TablePosition, card: Card) {
        playerProbablyHas[position]!! -= card
        playerProbablyHasNot[position]!! += card
    }
    private fun addProbablyHasNot(position: TablePosition, cardList: List<Card>) {
        playerProbablyHas[position]!! -= cardList
        playerProbablyHasNot[position]!! += cardList
    }

}