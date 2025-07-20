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
    switch (player) {
        case "SOUTH":
            return tableSouth;
        case "NORTH":
            return tableNorth;
        case "EAST":
            return tableEast;
        case "WEST":
            return tableWest;
    }
}

function findCardImage(cardModel) {
    src = cardModelToImageURL(cardModel)
    var allImages = document.getElementsByTagName('img');
    for (var i = 0; i < allImages.length; i++) {
        if (allImages[i].src.indexOf(src) >= 0) {
            return allImages[i];
        }
    }
    return null
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

//-----------------------------------------------------------------------------------------

function showCard(cardId, cardModel, clickable) {
    var aCardImage = document.getElementById(cardId)
    aCardImage.src = cardModelToImageURL(cardModel)
}


function showPlayerCards(player, playerHand, clickable) {
    for (let cardIndex = 0; cardIndex < playerHand.length; cardIndex++) {
        showCard(player + cardIndex, playerHand[cardIndex], clickable)
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
    showPlayerCards("playerSouth", gameStatus.playerSouth, gameStatus.playerToMove === "SOUTH")
    showPlayerCards("playerWest", gameStatus.playerWest, false)
    showPlayerCards("playerNorth", gameStatus.playerNorth, false)
    showPlayerCards("playerEast", gameStatus.playerEast, false)
    showExtras(gameStatus)
    showLeader(gameStatus.leadPlayer)
    console.log("IN SHOWBOARD")
}

var lastWinnerId = "pointToWinnerNorth"
function showLeader(leader) {
    var lastWinner = document.getElementById(lastWinnerId)
    if (leader === "NORTH") {
        lastWinnerId = "pointToWinnerNorth"
    } else if (leader === "EAST") {
        lastWinnerId = "pointToWinnerEast"
    } else if (leader === "SOUTH") {
        lastWinnerId = "pointToWinnerSouth"
    } else if (leader === "WEST") {
        lastWinnerId = "pointToWinnerWest"
    }
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

function showInfo(info) {
    document.getElementById("scoreInfo").innerHTML = info
}

//-----------------------------------------------------------------------------------------

function showMove(movePlayed) {
    var tableCardImage = playerModelToTableImage(movePlayed.player)
    var playerCardImage = findCardImage(movePlayed.cardPlayed)
    if (playerCardImage != null) {
        tableCardImage.src = playerCardImage.src
        playerCardImage.src = NoCardImage()
    } else {
        tableCardImage.src = cardModelToImageURL(movePlayed.cardPlayed)
    }
    showInfo("")
    let showWinnerWait = 2000
    let clearMoveWait = 700
    let waitAfterTrick = showWinnerWait+clearMoveWait
    let waitAfterMove = 500
    if (movePlayed.trickCompleted != null) {
        if (movePlayed.trickCompleted.roundCompleted) {
            clearTable(movePlayed.trickCompleted.trickWinner, showWinnerWait, clearMoveWait)
            setTimeout(function () {
                requestForScoreCard();
            }, waitAfterTrick)
            if (!movePlayed.trickCompleted.gameOver) {
                setTimeout(function () {
                    requestGameStatus();
                }, waitAfterTrick+300)
                setTimeout(function () {
                    if (movePlayed.nextPlayer !== "SOUTH") {
                        requestComputeMove();
                    } else {
                        waitForPlayerMove()
                    }
                }, waitAfterTrick+1800)
            } else {
                showInfo("---- GAME OVER ----")
            }
        } else {
            requestGameStatus()
            clearTable(movePlayed.trickCompleted.trickWinner, showWinnerWait, clearMoveWait)
            setTimeout(function () {
                if (movePlayed.nextPlayer !== "SOUTH") {
                    requestComputeMove();
                } else {
                    waitForPlayerMove()
                }
            }, waitAfterTrick + 500)
        }
    } else {
        requestGameStatus()
        setTimeout(function () {
            if (movePlayed.nextPlayer !== "SOUTH") {
                requestComputeMove();
            } else {
                waitForPlayerMove()
            }
        }, waitAfterMove)
    }
}

function clearTableAndResetWinner(winnerCardId) {
    if (winnerCardId === "tableSouth") {
        winnerCardNorthToSouth.id = "tableNorth"
        winnerCardWestToSouth.id = "tableWest"
        winnerCardEastToSouth.id = "tableEast"
        winnerCardSouthToSouth.id = "tableSouth"
    } else if (winnerCardId === "tableNorth") {
        winnerCardNorthToNorth.id = "tableNorth"
        winnerCardWestToNorth.id = "tableWest"
        winnerCardEastToNorth.id = "tableEast"
        winnerCardSouthToNorth.id = "tableSouth"
    } else if (winnerCardId === "tableEast") {
        winnerCardNorthToEast.id = "tableNorth"
        winnerCardWestToEast.id = "tableWest"
        winnerCardEastToEast.id = "tableEast"
        winnerCardSouthToEast.id = "tableSouth"
    } else {
        winnerCardNorthToWest.id = "tableNorth"
        winnerCardWestToWest.id = "tableWest"
        winnerCardEastToWest.id = "tableEast"
        winnerCardSouthToWest.id = "tableSouth"
    }
    removeCardsFromTable()
}

function removeCardsFromTable() {
    tableWest.src = NoCardImage();
    tableNorth.src = NoCardImage();
    tableEast.src = NoCardImage();
    tableSouth.src = NoCardImage();
}

function clearMove(trickWinner, wait) {
    var winningTableCard = playerModelToTableImage(trickWinner)
    var lastWinner = document.getElementById(lastWinnerId)
    tableEast.src = "carddeck/CardBack.svg"
    if (winningTableCard === tableSouth) {
        tableNorth.id = "winnerCardNorthToSouth"
        tableWest.id = "winnerCardWestToSouth"
        tableEast.id = "winnerCardEastToSouth"
        tableSouth.id = "winnerCardSouthToSouth"
        setTimeout(function () {
            clearTableAndResetWinner("tableSouth");
        }, wait);
    } else if (winningTableCard == tableWest) {
        tableNorth.id = "winnerCardNorthToWest"
        tableWest.id = "winnerCardWestToWest"
        tableEast.id = "winnerCardEastToWest"
        tableSouth.id = "winnerCardSouthToWest"
        setTimeout(function () {
            clearTableAndResetWinner("tableWest");
        }, wait);
    } else if (winningTableCard == tableNorth) {
        tableNorth.id = "winnerCardNorthToNorth"
        tableWest.id = "winnerCardWestToNorth"
        tableEast.id = "winnerCardEastToNorth"
        tableSouth.id = "winnerCardSouthToNorth"
        setTimeout(function () {
            clearTableAndResetWinner("tableNorth");
        }, wait);
    } else if (winningTableCard == tableEast) {
        tableNorth.id = "winnerCardNorthToEast"
        tableWest.id = "winnerCardWestToEast"
        tableEast.id = "winnerCardEastToEast"
        tableSouth.id = "winnerCardSouthToEast"
        setTimeout(function () {
            clearTableAndResetWinner("tableEast");
        }, wait);
    } else {
        console.log("Vreemd!!")
        //weird.....
    }
}

function clearTable(trickWinner, showWinnerWait, clearMoveWait) {
    showLeader(trickWinner)
    setTimeout(function () {
        clearMove(trickWinner, clearMoveWait)
    }, showWinnerWait);
}
