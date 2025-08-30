package com.cards.game.hearts

class Round() {

    private val trickList = mutableListOf<Trick>()

    private fun getLastTrick() = trickList.lastOrNull()?:throw Exception("We do not have a last trick")
    fun hasNotStarted(): Boolean = trickList.size == 1 && trickList.first().hasNotStarted()
    fun getTrickOnTable() = if (getLastTrick().isActive()) getLastTrick() else throw Exception("We do not have a current trick on table")
    fun getLastCompletedTrickWinner(): TableSide? = getLastCompletedTrick()?.getWinningSide()
    fun getTrickList() = trickList.toList()
    fun isComplete() = getTrickList().size == NUMBER_OF_TRICKS_PER_ROUND && getTrickList().last().isComplete()

    fun getLastCompletedTrick(): Trick? {
        if (trickList.isEmpty())
            return null
        if (!trickList.last().isActive())
            return trickList.last()
        if (trickList.size <= 1)
            return null
        return trickList[trickList.size - 2]
    }

    fun addTrick(trick: Trick) {
        if (isComplete())
            throw Exception("Trying to add more tricks to a round than the maximum allowed")
        trickList.add(trick)
    }

    fun getScore(): ScoreHearts {
        var score = ScoreHearts.ZERO
        if (isComplete()) {
            getTrickList().forEach { trick ->
                score = score.plus(trick.getScore())
            }
        }
        return score
    }
}