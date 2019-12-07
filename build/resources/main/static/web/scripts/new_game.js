// ... añadir juego
$.post({
  url: "/api/games/players/1/ships",
  data: JSON.stringify([{type: "carrier", location:["A1","A2"]}]),
  dataType: "text",
  contentType: "application/json"
})
.done(function (response, status, jqXHR) {
  alert( "Game added: " + response );
})
.fail(function (jqXHR, status, httpError) {
  alert("Failed to add Game: " + textStatus + " " + httpError);
})