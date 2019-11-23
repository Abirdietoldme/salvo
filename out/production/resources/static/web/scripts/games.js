$(function () {

    // load and display JSON sent by server for /players

    function loadData() {
      $.get("/api/games")
        .done(function (data) {
          updateView(data)
          console.log(data);
        })
        .fail(function (jqXHR, textStatus) {
          showOutput("Failed: " + textStatus);
        });
    }

function updateView(data) {
        var listaDeJuegos = data.map(function (game) {
             return  '<li>Date: ' + new Date(game.created).toLocaleString() + ' Player 1: ' + game.gamePlayers.map(function(p) { return p.player.email}).join(' VS Player 2: ') +'</li>';
        }).join('');
      document.getElementById("gameList").innerHTML = listaDeJuegos;
    }

    loadData();

});