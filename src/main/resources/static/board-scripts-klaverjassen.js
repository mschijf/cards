let gameType="klaverjassen"

function clearContract() {
    document.getElementById("hasContractWest").innerHTML = ""
    document.getElementById("hasContractNorth").innerHTML = ""
    document.getElementById("hasContractEast").innerHTML = ""
    document.getElementById("hasContractSouth").innerHTML = ""
}

function showGameSpecific(gameStatus) {
    document.getElementById("trumpCard").src = cardColorAndRankToImageURL(gameStatus.trumpColor, "TWO")
    clearContract()
    document.getElementById("hasContract" + playerModelToElementPostFix(gameStatus.contractOwner)).innerHTML = "speelt"
}

function handleScoreCard(scoreModel) {
    let maxRows = 16

    let scoreList = scoreModel.scoreList
    let start = 0
    for (let i = 0; i < maxRows; i++) {
        let scoreNS = document.getElementById("pointsNS" + (i + 1))
        let scoreEW = document.getElementById("pointsEW" + (i + 1))
        let bonusNS = document.getElementById("bonusNS" + (i + 1))
        let bonusEW = document.getElementById("bonusEW" + (i + 1))
        let roundNr = document.getElementById("roundNr" + (i + 1))
        roundNr.innerHTML = "" + (i+1)
        if (scoreList.length > i) {
            scoreNS.innerHTML = scoreList[start + i].northSouthPoints
            bonusNS.innerHTML = scoreList[start + i].northSouthBonus
            scoreEW.innerHTML = scoreList[start + i].eastWestPoints
            bonusEW.innerHTML = scoreList[start + i].eastWestBonus
        } else {
            scoreNS.innerHTML = ""
            bonusNS.innerHTML = ""
            scoreEW.innerHTML = ""
            bonusEW.innerHTML = ""
        }
    }

}
