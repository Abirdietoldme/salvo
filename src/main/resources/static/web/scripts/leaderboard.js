$(function() {
    loadData();
    loadData2();
});

var submitButton

$(function(){
$(".submitButton").click(function(){
submitButton=$(this).attr("name")
})
})

//Login
function login() {
    event.preventDefault();

      $.post("/api/login", {
          username: $("#username").val(),
          password: $("#password").val()
        })
        .done(function(){
            location.reload();

        $("#login-form").hide(),
        $("#logout-form").show(),
        $("#password").val("")

        }).fail(function(){
        console.log("Failed to Loggin");
        alert("User not found. You have to register first")
        //No es necesario $("#login-form").show()
        });
    }

//Signup
function signup() {
        event.preventDefault();

      $.post("/api/players", {
          username: $("#username").val(),
          password: $("#password").val()
        })
        .done(function(data){
        console.log(data);
        login();
        //location.reload();

        $("#login-form").hide(),
        $("#logout-form").show()
        $("#password").val("")
        }).fail(function(){
        console.log("Failed to Logged Up");
        alert("User is not valid")
        });

       }

// load and display JSON sent by server for /players

        function loadData2() {
          $.get("/api/games")
            .done(function (data) {
                quienSoy(data);
                console.log(data);
                if(data.player == "Guest"){
                updateViewGuest(data);
                }
                if(data.player != "Guest"){
                updateView(data);
                }


        })

        //Login
        .then(function (data) {
            if(data.player ==  "Guest"){
              $("#login-form").show();
              $("#logout-form").hide();
              $("#nuevoJuego").hide();
            }else{
              $("#login-form").hide();
              $("#logout-form").show();
            }
        })

        .fail(function (jqXHR, textStatus) {
          showOutput("Failed: " + textStatus);
        });
    };

//Lista de juegos
function updateView(data) {


    var listaDeJuegos = '<table><thead><td>Date</td><td>Player 1</td><td> </td><td>Player 2</td><td>Status</td></thead>' + data.games.filter(juego =>juego.score.length == 0).map(function (game) {
        console.log(game)

        var gpIndicado = game.gamePlayers.find(gamePlayer => gamePlayer.player.id ==data.player.id)
        if (gpIndicado != undefined ){
            if(gpIndicado.hasShips == 0)
                { url= "crear-grid.html"}
            else
                {url = "game.html"}

        }
            if(game.gamePlayers.length  ==  2   &&  data.player.id  ==  game.gamePlayers[0].player.id){
                    return '<tr><td>' + new Date(game.created).toLocaleString() + '</td><td>' + game.gamePlayers[0].player.email   +'</td><td>VS</td><td>'   +   game.gamePlayers[1].player.email    +  '</td><td><a href='+ url +'?gp=' + game.gamePlayers[0].id + ' class=" BackToGame button btn btn-primary">Return to Game</a>'+'</td></tr>';
        //El link me lleva a game.html?gp=id
            }


        if(game.gamePlayers.length  ==  1   &&  data.player.id  ==  game.gamePlayers[0].player.id){
                    return   '<tr><td>' + new Date(game.created).toLocaleString() + '</td><td>' + game.gamePlayers[0].player.email   +'</td><td>VS</td><td>Waiting for Player 2... '    +   '</td><td><a href='+ url +'?gp=' + game.gamePlayers[0].id + ' class=" BackToGame button btn btn-primary">Return to Game</a>'+'</td></tr>';
                }


        if(game.gamePlayers.length  ==  2   &&  data.player.id  ==  game.gamePlayers[1].player.id){
                   return  '<tr><td>' + new Date(game.created).toLocaleString() + '</td><td>' + game.gamePlayers[0].player.email   + '</td><td>VS</td><td>' +   game.gamePlayers[1].player.email    +   '</td><td><a href='+ url +'?gp=' + game.gamePlayers[1].id + ' class=" BackToGame button btn btn-primary">Return to Game</a>'+'</td></tr>';
                        }


        if(game.gamePlayers.length  ==  2   &&  data.player.id  !=  (game.gamePlayers[0].player.id && game.gamePlayers[1].player.id)){
                    return   '<tr><td>' + new Date(game.created).toLocaleString() + '</td><td>' + game.gamePlayers[0].player.email   + '</td><td>VS</td><td>' +   game.gamePlayers[1].player.email    +   '</td><td><div id="gameFull">Full Game</div>'+'</td></tr>';
                                }


        if(game.gamePlayers.length  ==  1   &&  data.player.id  !=  game.gamePlayers[0].player.id){
                    return  '<tr><td>' + new Date(game.created).toLocaleString() + '</td><td>' + game.gamePlayers[0].player.email  + '</td><td>VS</td><td>Waiting for Player 2... '    +   '</td><td><button onclick= "joinGame('+ game.id + ')" class="joinGame btn btn-primary">Join Game</button>'+'</td></tr>';
                                        }

        }).join('');

        listaDeJuegos += '</table>'

        document.getElementById("gameList").innerHTML = listaDeJuegos;
}


//Tabla de Juegos si user == Guest
function updateViewGuest(data){
    var listaDeJuegosGuest = '<table><thead><td>Date</td><td>Player 1</td><td> </td><td>Player 2</td><td>Status</td></thead>' + data.games.filter(juego =>juego.score.length == 0).map(function (game) {

            if(game.gamePlayers.length  ==  2){
                        return '<tr><td>' + new Date(game.created).toLocaleString() + '</td><td>' + game.gamePlayers[0].player.email   +'</td><td>VS</td><td>'   +   game.gamePlayers[1].player.email    +   '</td><td><div id="gameFull">Full Game</div>'+'</td></tr>';
            }

            if(game.gamePlayers.length  ==  1){
                        return   '<tr><td>' + new Date(game.created).toLocaleString() + '</td><td>' + game.gamePlayers[0].player.email   +'</td><td>VS</td><td>Waiting for Player 2... '    +   '</td><td><button class="JoinGameGris button btn btn-primary" onclick=\'alert("You have to be logged in to join the game")\' value="Click">Join Game</button>'+'</td></tr>';
                    }
 }).join('');

     listaDeJuegosGuest += '</table>'

     document.getElementById("gameList").innerHTML = listaDeJuegosGuest;
 }

function loadData() {
    $.get('/api/leaderBoard')
        .done(function(data) {

        data.sort((a,b)=> {return b.score.total - a.score.total});
              scoreBoard(data);
            })

        .fail(function( jqXHR, textStatus ) {
          alert( "Failed: " + textStatus );
        });
};

function scoreBoard(data){
      var board = data.map(function (score) {
          return  '<tr>'
                  + '<td>' + score.userName + '</td>'
                  + '<td>' + score.score.total + '</td>'
                  + '<td>' + score.score.won + '</td>'
                  + '<td>' + score.score.lost + '</td>'
                  + '<td>' + score.score.tied + '</td>'
                  + '</tr>';
      }).join('');
      document.getElementById("puntajes").innerHTML = board;
}

$("#login-form").on("submit", function(event){
event.preventDefault();
//console.log("Estamos aqui");

    if (submitButton=="login"){
    login();

    }else if(submitButton=="signup"){
    signup();

    }else{
    //No se presionó ningún botón
    }
})


//Logout
function logout() {
        event.preventDefault();

  $.post("/api/logout")
    .done(function(){
    console.log("User logged out");

    $("#logout-form").hide();
    $("#login-form").show();

    location.reload();
    })

    .fail(function(){
    console.log("Please try again")
    });
}


//Chequear si el usuario logueado tiene juegos pendientes -  Si los tiene esconder botón Create Game
function quienSoy(data) {

    if(data.player  !=  "Guest"){

        data.games.map(function (game) {

                if(game.gamePlayers.length == 2){
                    if( game.gamePlayers[0].player.id  == data.player.id  || game.gamePlayers[1].player.id  == data.player.id){
                                    if (game.score.length == 0){

                                               return  $("#nuevoJuego").hide();
                                    }
                }
}
        });
    }

}


//Crear Juego
$('#createGame').click(function (event) {
    event.preventDefault();
    $.post("/api/games")
        .done(function (data) {
            console.log("My game ",data);
            console.log("Game created");
            gameViewUrl = "/web/crear-grid.html?gp=" + data.gpid;
            $('#gameCreatedSuccessfully').show("slow").delay(2000).hide("slow");
            setTimeout(
                function()
                {
                    location.href = gameViewUrl;
                }, 3000);
        }).fail(function (data) {
            console.log("Game creation failed");
            $('#errorSignup').text(data.responseJSON.error);
            $('#errorSignup').show( "slow" ).delay(4000).hide( "slow" );
        })
});

//Unirse a juego
function joinGame (gameId){
    event.preventDefault();
    console.log(event)
    $.post('/api/game/' + gameId   + '/players')
        .done(function(data){
        console.log(data)
            gameViewUrl = "/web/crear-grid.html?gp=" + data.gpid;
        $('#joinedToGameSuccessfully').show("slow").delay(2000).hide("slow");
                    setTimeout(
                        function()
                        {
                            location.href = gameViewUrl;
                        }, 3000);
                }).fail(function (data) {
                    console.log("Join to game failed");
                    $('#errorSignup').text(data.responseJSON.error);
                    $('#errorSignup').show( "slow" ).delay(4000).hide( "slow" );
                })
}

//Música
var reproducir = document.getElementsByTagName("audio");
reproducir.play();
