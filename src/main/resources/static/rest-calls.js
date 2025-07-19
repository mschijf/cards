function requestForNewGame() {
    var request = new XMLHttpRequest();

    request.open("POST", "/api/v1/new-game/");
    request.onreadystatechange = function() {
        if(this.readyState === 4 && this.status === 200) {
            var gameStatus = JSON.parse(this.responseText);
            showCardsInHands(gameStatus)

            if (gameStatus.playerToMove != "SOUTH") {
                computeMove();
            }

        }
    };
    request.send();
}

function requestGameStatus() {
    var request = new XMLHttpRequest();

    request.open("GET", "/api/v1/game-status/");
    request.onreadystatechange = function() {
        if(this.readyState === 4 && this.status === 200) {
            var gameStatus = JSON.parse(this.responseText);
            showCardsInHands(gameStatus)
        }
    };
    request.send();
}

function requestForScoreCard() {
    var request = new XMLHttpRequest();

    request.open("GET", "/api/v1/score-list/");
    request.onreadystatechange = function() {
        if(this.readyState === 4 && this.status === 200) {
            var scoreModel = JSON.parse(this.responseText);
            showScoreCard(scoreModel)
        }
    };
    request.send();
}


function computeMove() {
    var request = new XMLHttpRequest();

    request.open("POST", "/api/v1/computeMove/");
    request.onreadystatechange = function() {
        if(this.readyState === 4 && this.status === 200) {
            var movePlayed = JSON.parse(this.responseText);
            if (movePlayed.success == true) {
                showMove(movePlayed.cardPlayedModel)
            }
        }
    };
    request.send();
}

function doMove(cardModel) {
    var request = new XMLHttpRequest();

    request.open("POST", "/api/v1/executeMove/" + cardModel.color + "/" + cardModel.rank);
    request.onreadystatechange = function() {
        if(this.readyState === 4 && this.status === 200) {
            var movePlayed = JSON.parse(this.responseText);
            if (movePlayed.success == true) {
                showMove(movePlayed.cardPlayedModel)
            } else {
                console.log("FOUTE KAART!!")
            }
        }
    };
    request.send();
}


