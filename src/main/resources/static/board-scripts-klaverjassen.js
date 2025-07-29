function showExtras(gameStatus) {
    clearGeniusSouthValues()
    showGeniusSouthValues(gameStatus.playerSouth)

    document.getElementById("trumpCard").src = "carddeck/2H.SVG"

    document.getElementById("buttonJson").onclick = function () {
        showJson(gameStatus.gameJsonString)
    };
    showJson("")
}

function handleScoreCard(scoreModel) {
    let maxRows = 16
}
