function requestForNewGame() {
    let request = new XMLHttpRequest();

    request.open("POST", "/api/v1/new-game/");
    request.onreadystatechange = function() {
        if(this.readyState === 4 && this.status === 200) {
            let gameStatus = JSON.parse(this.responseText);
            handleGameStatus(gameStatus)
        }
    };
    request.send();
}

function requestGameStatus() {
    let request = new XMLHttpRequest();

    request.open("GET", "/api/v1/game-status/");
    request.onreadystatechange = function() {
        if(this.readyState === 4 && this.status === 200) {
            let gameStatus = JSON.parse(this.responseText);
            handleGameStatus(gameStatus)
        }
    };
    request.send();
}

function requestForScoreCard() {
    let request = new XMLHttpRequest();

    request.open("GET", "/api/v1/score-list/");
    request.onreadystatechange = function() {
        if(this.readyState === 4 && this.status === 200) {
            let scoreModel = JSON.parse(this.responseText);
            handleScoreCard(scoreModel)
        }
    };
    request.send();
}


function requestComputeMove() {
    let request = new XMLHttpRequest();

    request.open("POST", "/api/v1/computeMove/");
    request.onreadystatechange = function() {
        if(this.readyState === 4 && this.status === 200) {
            let movePlayed = JSON.parse(this.responseText);
            if (movePlayed.success) {
                handleMove(movePlayed.cardPlayedModel)
            }
        }
    };
    request.send();
}

function requestDoMove(cardModel) {
    let request = new XMLHttpRequest();

    request.open("POST", "/api/v1/executeMove/" + cardModel.color + "/" + cardModel.rank);
    request.onreadystatechange = function() {
        if(this.readyState === 4 && this.status === 200) {
            let movePlayed = JSON.parse(this.responseText);
            if (movePlayed.success) {
                handleMove(movePlayed.cardPlayedModel)
            } else {
                handleIllegalMoveDone()
            }
        }
    };
    request.send();
}

function requestLog() {
    let request = new XMLHttpRequest();

    request.open("GET", "/api/v1/log/");
    request.onreadystatechange = function() {
        if(this.readyState === 4 && this.status === 200) {
            let logLines = this.responseText;
            handleLog(logLines)
        }
    };
    request.send();
}



