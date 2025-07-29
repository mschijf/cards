let gameType="klaverjassen"

function showGameSpecific(gameStatus) {
    document.getElementById("trumpCard").src = cardColorAndRankToImageURL(gameStatus.trumpColor, "TWO")
}

function handleScoreCard(scoreModel) {
    let maxRows = 16
}
