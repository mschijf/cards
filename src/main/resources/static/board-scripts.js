var __globalGameStatus = null

function upDownSignalImage(goingUp) {
    if (goingUp)
        return "assets/Green_Arrow_Up.svg"
    else
        return "assets/Red_Arrow_Down.svg"
}

function NoCardImage() {
    return "carddeck/NoCard.SVG"
}

function CardBackImage() {
    return "carddeck/CardBack.SVG"
}

function cardModelToImageURL(cardModel) {
    if (cardModel == null) {
        return NoCardImage()
    }

    switch (cardModel.color) {
        case "SPADES":
            colorString = "S";
            break;
        case "HEARTS":
            colorString = "H";
            break;
        case "CLUBS":
            colorString = "C";
            break;
        case "DIAMONDS":
            colorString = "D";
            break;
    }
    switch (cardModel.rank) {
        case "TWO":
            rankString = "2";
            break;
        case "THREE":
            rankString = "3";
            break;
        case "FOUR":
            rankString = "4";
            break;
        case "FIVE":
            rankString = "5";
            break;
        case "SIX":
            rankString = "6";
            break;
        case "SEVEN":
            rankString = "7";
            break;
        case "EIGHT":
            rankString = "8";
            break;
        case "NINE":
            rankString = "9";
            break;
        case "TEN":
            rankString = "10";
            break;
        case "JACK":
            rankString = "J";
            break;
        case "QUEEN":
            rankString = "Q";
            break;
        case "KING":
            rankString = "K";
            break;
        case "ACE":
            rankString = "A";
            break;
    }
    return "carddeck/" + rankString + colorString + ".SVG"
}

function playerModelToTableImage(player) {
    return document.getElementById("table" + playerModelToElementPostFix(player))
}

function playerModelToElementPostFix(player) {
    return player[0].toUpperCase() + player.substring(1).toLowerCase()
}

function cardModelToImage(cardModel) {
    src = cardModelToImageURL(cardModel)
    var allImages = document.getElementsByTagName('img');
    for (var i = 0; i < allImages.length; i++) {
        if (allImages[i].src.indexOf(src) >= 0) {
            return allImages[i];
        }
    }
    return null
}

function isHumanPlayer(player) {
    return player === "SOUTH"
}

//-----------------------------------------------------------------------------------------

function initGame() {
    removeCardsFromTable()
    requestForNewGame()
    requestForScoreCard()
    showInfo("")
    showJson("")
}

function showJson(msg) {
    jsonText.innerHTML = msg
}

function showInfo(info) {
    document.getElementById("scoreInfo").innerHTML = info
}

//-----------------------------------------------------------------------------------------

function showCard(cardId, cardModel) {
    var aCardImage = document.getElementById(cardId)
    aCardImage.src = cardModelToImageURL(cardModel)
}


function showPlayerCards(player, playerHand) {
    for (let cardIndex = 0; cardIndex < playerHand.length; cardIndex++) {
        showCard(player + cardIndex, playerHand[cardIndex])
    }
}

function showPlayerSouthValues(values) {
    for (let i = 0; i < values.length; i++) {
        document.getElementById("meta" + i).innerHTML = values[i]
    }
}

function showExtras(gameStatus) {
    showPlayerSouthValues(gameStatus.valueSouth)
    upDownSignal.src = upDownSignalImage(gameStatus.goingUp)
    buttonJson.onclick = function () {
        showJson(gameStatus.gameJsonString)
    };
    showJson("")
}

function showBoard(gameStatus) {
    __globalGameStatus = gameStatus
    showPlayerCards("playerSouth", gameStatus.playerSouth)
    showPlayerCards("playerWest", gameStatus.playerWest)
    showPlayerCards("playerNorth", gameStatus.playerNorth)
    showPlayerCards("playerEast", gameStatus.playerEast)
    showExtras(gameStatus)
    showLeader(gameStatus.leadPlayer)
}

var lastWinnerId = "pointToWinnerNorth"
function showLeader(leader) {
    var lastWinner = document.getElementById(lastWinnerId)
    lastWinnerId = "pointToWinner" + playerModelToElementPostFix(leader)
    lastWinner.id = lastWinnerId
}

//-----------------------------------------------------------------------------------------

function waitForPlayerMove() {
    setClickableCards(true)
}

function doMove(cardModel) {
    setClickableCards(false)
    requestDoMove(cardModel)
}

function setClickableCards(clickable) {
    for (let cardIndex = 0; cardIndex < __globalGameStatus.playerSouth.length; cardIndex++) {
        let aCardImage = document.getElementById("playerSouth" + cardIndex)
        let cardModel = __globalGameStatus.playerSouth[cardIndex]
        if (clickable && cardModel != null) {
            aCardImage.onclick = function () {
                doMove(cardModel)
            };
            aCardImage.style.cursor = "pointer"
        } else {
            aCardImage.onclick = null
            aCardImage.style.cursor = "default"
        }
    }
}

//-----------------------------------------------------------------------------------------

function showWrongMoveDone() {
    console.log("FOUTE KAART!!")
    waitForPlayerMove()
}

function showMove(movePlayed) {
    cardFromHandToTable(movePlayed)

    let trickCompleteTime = 0
    if (movePlayed.trickCompleted != null) {
        let showWinnerTime = 2000
        let clearMoveTime = 700
        trickCompleteTime = showWinnerTime + clearMoveTime

        animateTrickWinnerAndRemoveTrickFromTable(movePlayed.trickCompleted.trickWinner, showWinnerTime, clearMoveTime)
    }

    let roundCompleteTime = 0
    if (movePlayed.trickCompleted != null && movePlayed.trickCompleted.roundCompleted) {
        roundCompleteTime = 1000
        setTimeout(function () {
            requestForScoreCard();
        }, trickCompleteTime)
    }

    if (movePlayed.trickCompleted != null && movePlayed.trickCompleted.gameOver) {
        showInfo("---- GAME OVER ----")
        return
    }

    setTimeout(function () {
        requestGameStatus();
    }, trickCompleteTime + roundCompleteTime)

    let waitForNextMove = isHumanPlayer(movePlayed.nextPlayer) ? 0 : 500
    setTimeout(function () {
        handleNextMove(movePlayed.nextPlayer)
    }, trickCompleteTime + roundCompleteTime + waitForNextMove)

}

function cardFromHandToTable(movePlayed) {
    var tableCardImage = playerModelToTableImage(movePlayed.player)
    var playerCardImage = cardModelToImage(movePlayed.cardPlayed)
    tableCardImage.src = cardModelToImageURL(movePlayed.cardPlayed)
    playerCardImage.src = NoCardImage()
    showInfo("")
}

function handleNextMove(nextPlayer) {
    if (isHumanPlayer(nextPlayer)) {
        waitForPlayerMove()
    } else {
        requestComputeMove();
    }
}

function removeCardsFromTable() {
    document.getElementById("tableWest").src = NoCardImage();
    document.getElementById("tableNorth").src = NoCardImage();
    document.getElementById("tableEast").src = NoCardImage();
    document.getElementById("tableSouth").src = NoCardImage();
}

function animateTrickWinnerAndRemoveTrickFromTable(trickWinner, showWinnerWait, clearMoveWait) {
    showLeader(trickWinner)
    setTimeout(function () {
        animateTrickWinner(trickWinner, clearMoveWait)
    }, showWinnerWait);
}

function animateTrickWinner(trickWinner, wait) {
    let postFix = playerModelToElementPostFix(trickWinner)

    animateCardToWinner("tableNorth", "winnerCardNorthTo" + postFix)
    animateCardToWinner("tableWest", "winnerCardWestTo" + postFix)
    animateCardToWinner("tableEast", "winnerCardEastTo" + postFix)
    animateCardToWinner("tableSouth", "winnerCardSouthTo" + postFix)

    setTimeout(function () {
        resetTableCardAnimation("winnerCardNorthTo" + postFix, "tableNorth")
        resetTableCardAnimation("winnerCardWestTo" + postFix, "tableWest")
        resetTableCardAnimation("winnerCardEastTo" + postFix, "tableEast")
        resetTableCardAnimation("winnerCardSouthTo" + postFix, "tableSouth")
    }, wait);
}

function animateCardToWinner(from, to) {
    document.getElementById(from).src = CardBackImage()
    document.getElementById(from).id = to
}

function resetTableCardAnimation(to, from) {
    document.getElementById(to).id = from
    document.getElementById(from).src = NoCardImage();
}

//-----------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------

function showScoreCard(scoreModel) {
    var maxRows = 9
    scoreList = scoreModel.scoreList
    var start = Math.max(0, scoreList.length - maxRows)
    for (let i = 0; i < maxRows; i++) {
        var scoreSouth = document.getElementById("scoreS" + (i + 1))
        var scoreWest = document.getElementById("scoreW" + (i + 1))
        var scoreNorth = document.getElementById("scoreN" + (i + 1))
        var scoreEast = document.getElementById("scoreE" + (i + 1))
        var roundNr = document.getElementById("roundNr" + (i + 1))
        roundNr.innerHTML = (start + i + 1)
        if (scoreList.length > i) {
            scoreSouth.innerHTML = scoreList[start + i].south
            scoreWest.innerHTML = scoreList[start + i].west
            scoreEast.innerHTML = scoreList[start + i].east
            scoreNorth.innerHTML = scoreList[start + i].north
        } else {
            scoreSouth.innerHTML = ""
            scoreWest.innerHTML = ""
            scoreEast.innerHTML = ""
            scoreNorth.innerHTML = ""
        }
    }
}

//-----------------------------------------------------------------------------------------
