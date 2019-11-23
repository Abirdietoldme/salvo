//Lista de juegos
function updateView(data) {
        var listaDeJuegos = data.map(function (game) {
             return  '<li>Date: ' + new Date(game.created).toLocaleString() + ' Player 1: ' + game.gamePlayers.map(function(p) { return p.player.email}).join(' VS Player 2: ') +'</li>';
        }).join('');
      document.getElementById("gameList").innerHTML = listaDeJuegos;
    }

    loadData2();


function updateView(data) {

    var listaDeJuegos = data.games.map(function (game) {
        var item;

        if(game.gamePlayers.length  ==  1   &&  data.player.id  ==  game.gamePlayers[0].player.id){
                    item    =   '<li>Date: ' + new Date(game.created).toLocaleString() + ' Player 1: ' + game.gamePlayers[0].player.email   +' VS Player 2: '   +   game.gamePlayers[1].player.email    +   '<a href="game.html?gp=' + game.gamePlayers[0].id + '" class="button btn btn-primary" id="BackToGame">Return to Game</a>'+'</li>';
        //El link me lleva a game.html?gp=id
        }
                 return item;


        if(game.gamePlayers.length  ==  0   &&  data.player.id  ==  game.gamePlayers[0].player.id){
                    item    =   '<li>Date: ' + new Date(game.created).toLocaleString() + ' Player 1: ' + game.gamePlayers[0].player.email   +' VS Waiting for Player 2... '    +   '<button id="BackToGame" class="btn btn-primary">Return to Game</button>'+'</li>';
                }
                         return item;


        if(game.gamePlayers.length  ==  1   &&  data.player.id  ==  game.gamePlayers[1].player.id){
                    item    =   '<li>Date: ' + new Date(game.created).toLocaleString() + ' Player 1: ' + game.gamePlayers[0].player.email   + ' VS Player 2: ' +   game.gamePlayers[1].player.email    +   '<button id="BackToGame" class="btn btn-primary">Return to Game</button>'+'</li>';
                        }
                                 return item;


        if(game.gamePlayers.length  ==  1   &&  data.player.id  !=  (game.gamePlayers[0].player.id && game.gamePlayers[1].player.id)){
                    item    =   '<li>Date: ' + new Date(game.created).toLocaleString() + ' Player 1: ' + game.gamePlayers[0].player.email   + ' VS Player 2: ' +   game.gamePlayers[1].player.email    +   '<div id="gameFull">Game Full</div>'+'</li>';
                                }
                                         return item;


        if(game.gamePlayers.length  ==  0   &&  data.player.id  !=  game.gamePlayers[0].player.id ){
                    item    =   '<li>Date: ' + new Date(game.created).toLocaleString() + ' Player 1: ' + game.gamePlayers[0].player.email  + ' VS Waiting for Player 2: '    +   '<button id="joinGame" class="btn btn-primary">Join Game</button>'+'</li>';
                                        }
                                                 return item;


    });

    document.getElementById("gameList").innerHTML = listaDeJuegos;
}
