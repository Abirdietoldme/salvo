$(function () {
  var gamesData;
  var dataGral;
  (async () => {
                    dataGral    =   await loadData2();
                    console.log(dataGral);
                    loadData(dataGral.player.id);
 })()

     makeTables();

});

function getParameterByName(name) {
  var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
  return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}

function loadData(idPlayerLogued) {
  $.get('/api/game_view/' + getParameterByName('gp'))
    .done(function (data) {
        gamesData = data;
              console.log(gamesData);



//Cargar CSS
 if (data.gamePlayers.length == 2 && data.gamePlayers[1].id == getParameterByName('gp')){
               document.getElementById('cssArchivo').href="styles/game_gp2.css";

 }

      //Jugadores
            var playerInfo;
            if (data.gamePlayers.length == 2){
              if(data.gamePlayers[0].id == getParameterByName('gp')){
              playerInfo = [data.gamePlayers[0].player, data.gamePlayers[1].player];
              }
              if(data.gamePlayers[1].id == getParameterByName('gp')){
              playerInfo = [data.gamePlayers[1].player, data.gamePlayers[0].player];
                      }
              $('#playerInfo').text(playerInfo[0].email + '(you) vs ' + playerInfo[1].email);

              }

           else{
              playerInfo = [data.gamePlayers[0].player];
                $('#playerInfo').text(playerInfo[0].email + ' Waiting for another player...');

              }


       data.ships.forEach(function (shipPiece) {
        shipPiece.locations.forEach(function (shipLocation) {
          let turnHitted = isHit(shipLocation,data.salvoes,playerInfo[0].id)
          if(turnHitted >0){
            $('#B_' + shipLocation).addClass('ship-piece-hited');
            //$('#B_' + shipLocation).text(turnHitted);
          }
          else
            $('#B_' + shipLocation).addClass('ship-piece');
        });
      });
      data.salvoes.forEach(function (salvo) {
        console.log(salvo);
        if (playerInfo[0].id === salvo.player) {
          salvo.locations.forEach(function (location) {
            $('#S_' + location).addClass('salvo');
          });
        } else {
          salvo.locations.forEach(function (location) {
            $('#_' + location).addClass('salvo');
          });
        }
      });
      setShips(gamesData);
      console.log(idPlayerLogued);
      setSalvoes(gamesData, idPlayerLogued); //carga los salvoes guardados

      //Determinar que celdas golpeé
            gamesData.hits.opponent.forEach(function(playTurn) {
                    playTurn.hitLocations.forEach(function (hitCell) {
          x = +(hitCell.substring(1)) - 1
          y =stringToInt(hitCell[0].toUpperCase())
                        cellID = "#salvo" + y + x;
                        $(cellID).addClass("hitCell");
                    });
                });
            makeTables(gamesData.hits.opponent, "gameRecordOppTable");
            makeTables(gamesData.hits.self, "gameRecordSelfTable");

            // Consultar si le pegué a los barcos del otro - Tabla

            function makeTables (hitsArray, tableId) {

                    var tableId = "#" + tableId + " tbody";
                    $(tableId).empty();
                    var shipsAfloat = 5;
                    var playerTag;
                    if (tableId == "gameRecordOppTable") {
                        playerTag = "#opp";
                    }
                    if (tableId == "gameRecordSelfTable") {
                        playerTag = "#";
                    }

                    hitsArray.forEach(function (playTurn) {
                        var hitsReport = "";
                        if (playTurn.damages.carrierHits > 0){
                            hitsReport += "Carrier " + addDamagesIcons(playTurn.damages.carrierHits, "hit") + " ";
                            if (playTurn.damages.carrier === 5){
                                hitsReport += "SUNK! ";
                                shipsAfloat--;
                            }
                        }

                        if (playTurn.damages.battleshipHits > 0){
                            hitsReport += "Battleship " + addDamagesIcons(playTurn.damages.battleshipHits, "hit") + " ";
                            if (playTurn.damages.battleship === 4){
                                hitsReport += "SUNK! ";
                                shipsAfloat--;
                            }
                        }
                        if (playTurn.damages.submarineHits > 0){
                            hitsReport += "Submarine " + addDamagesIcons(playTurn.damages.submarineHits, "hit") + " ";
                            if (playTurn.damages.submarine === 3){
                                hitsReport += "SUNK! ";
                                shipsAfloat--;
                            }
                        }
                        if (playTurn.damages.destroyerHits > 0){
                            hitsReport += "Destroyer " + addDamagesIcons(playTurn.damages.destroyerHits, "hit") + " ";
                            if (playTurn.damages.destroyer === 3){
                                hitsReport += "SUNK! ";
                                shipsAfloat--;
                            }
                        }
                        if (playTurn.damages.patrolboatHits > 0){
                            hitsReport += "Patrol Boat " + addDamagesIcons(playTurn.damages.patrolboatHits, "hit") + " ";
                            if (playTurn.damages.patrolboat === 2){
                                hitsReport += "SUNK! ";
                                shipsAfloat--;
                            }
                        }

                        if (playTurn.missed > 0){
                            hitsReport +=  " ||  Missed shots " + addDamagesIcons(playTurn.missed, "missed") + " ";
                        }

                        if (hitsReport === ""){
                            hitsReport = "All salvoes missed! No damages!"
                        }

                        $('<tr><td class="textCenter">' + playTurn.turn + '</td><td>' + hitsReport + '</td></tr>').prependTo(tableId);

                    });
                    $('#shipsLeftSelfCount').text(shipsAfloat);
                }

                function addDamagesIcons (numberOfHits, hitOrMissed) {
                    var damagesIcons = "";
                    if (hitOrMissed === "missed") {
                        for (var i = 0; i < numberOfHits; i++) {
                            damagesIcons += "<img class='hitblast' src='img/snow.png'>"
                        }
                    }
                        if (hitOrMissed === "hit") {
                            for (var i = 0; i < numberOfHits; i++) {
                                damagesIcons += "<img class='hitblast' src='img/fire.png'>"
                            }
                    }
                    return damagesIcons;
                }


    })
    .fail(function (jqXHR, textStatus) {
      alert('Failed: ' + textStatus);
    });

}

async   function loadData2() {
    return  $.get('/api/games');

}

function isHit(shipLocation,salvoes,player_id) {
  var hit = 0;
  salvoes.forEach(function (salvo) {
    if(salvo.player != player_id)
      salvo.locations.forEach(function (location) {
        if(shipLocation === location)
          hit = salvo.turn;
      });
  });
  return hit;
}

/* Metodos propios de gridstack:
all the functionalities are explained in the gridstack github
https://github.com/gridstack/gridstack.js/tree/develop/doc
*/
$(() => loadGrid())
//Función principal que dispara el frame gridstack.js y carga la matriz con los barcos
const loadGrid = function () {
    var options = {
        //matriz 10 x 10
        width: 10,
        height: 10,
        //espacio entre las celdas (widgets)
        verticalMargin: 0,
        //altura de las celdas
        cellHeight: 45,
        //inhabilita la posibilidad de modificar el tamaño
        disableResize: true,
        //floating widgets
		float: true,
        //removeTimeout: tiempo en milisegundos antes de que el widget sea removido
        //mientras se arrastra fuera de la matriz (default: 2000)
        // removeTimeout:100,
        //permite al widget ocupar mas de una columna
        //sirve para no inhabilitar el movimiento en pantallas pequeñas
        disableOneColumnMode: true,
        // en falso permite arrastrar a los widget, true lo deniega
        staticGrid: true,
        //para animaciones
        animate: true,
        //PARA QUE LOS BARCOS NO SE MUEVAN
        disableDrag: true,

    }
    //inicializacion de la matriz
    $('.grid-stack').gridstack(options);

    grid = $('#grid').data('gridstack');

    //createGrid construye la estructura de la matriz
    createGrid(11, $(".grid-ships"), 'ships')

    listenBusyCells('ships')
    $('.grid-stack').on('change', () => listenBusyCells('ships'))

    //Crear grilla de Salvoes
    createGrid(11, $(".grid-salvoes"), 'salvo')

    var contador = 0


    //Si selecciono mas de 5 casilleros no puedo disparar
    //Una vez cargado los salvoes con createGrid procedemos a establecer una funcion click por cada celda de la siguiente manera
        $('div[id^="salvo"].grid-cell').click(function(event){
            if(!$(this).hasClass("salvo") && !$(this).hasClass("targetCell") && $(".targetCell").length < 5)
              {
                $(this).addClass("targetCell");
              } else if($(this).hasClass("targetCell")){
                $(this).removeClass("targetCell");}
        })

}

//createGrid construye la estructura de la matriz
/*
size:refiere al tamaño de nuestra grilla (siempre sera una matriz
     de n*n, osea cuadrada)
element: es la tag que contendra nuestra matriz, para este ejemplo
        sera el primer div de nuestro body
id: sera como lo llamamos, en este caso ship ???)
*/
const createGrid = function(size, element, id){
    //console.log(gamesData);
    // definimos un nuevo elemento: <div></div>
    let wrapper = document.createElement('DIV')

    // le agregamos la clase grid-wrapper: <div class="grid-wrapper"></div>
    wrapper.classList.add('grid-wrapper')

    //vamos armando la tabla fila por fila
    for(let i = 0; i < size; i++){
        //row: <div></div>
        let row = document.createElement('DIV')
        //row: <div class="grid-row"></div>
        row.classList.add('grid-row')
        //row: <div id="ship-grid-row0" class="grid-wrapper"></div>
        row.id =`${id}-grid-row${i}`
        /*
        wrapper:
                <div class="grid-wrapper">
                    <div id="ship-grid-row-0" class="grid-row">

                    </div>
                </div>
        */
        wrapper.appendChild(row)

        for(let j = 0; j < size; j++){
            //cell: <div></div>
            let cell = document.createElement('DIV')
            //cell: <div class="grid-cell"></div>
            cell.classList.add('grid-cell')
            //aqui entran mis celdas que ocuparan los barcos
            if(i > 0 && j > 0){
                //cell: <div class="grid-cell" id="ships00"></div>
                cell.id = `${id}${i - 1}${ j - 1}`
            }
            //aqui entran las celdas cabecera de cada fila
            if(j===0 && i > 0){
                // textNode: <span></span>
                let textNode = document.createElement('SPAN')
                /*String.fromCharCode(): método estático que devuelve
                una cadena creada mediante el uso de una secuencia de
                valores Unicode especificada. 64 == @ pero al entrar
                cuando i sea mayor a cero, su primer valor devuelto
                sera "A" (A==65)
                <span>A</span>*/
                textNode.innerText = String.fromCharCode(i+64)
                //cell: <div class="grid-cell" id="ships00"></div>
                cell.appendChild(textNode)
            }
            // aqui entran las celdas cabecera de cada columna
            if(i === 0 && j > 0){
                // textNode: <span>A</span>
                let textNode = document.createElement('SPAN')
                // 1
                textNode.innerText = j
                //<span>1</span>
                cell.appendChild(textNode)
            }
            /*
            row:
                <div id="ship-grid-row0" class="grid-row">
                    <div class="grid-cell"></div>
                </div>
            */
            row.appendChild(cell)
        }
    }

    element.append(wrapper)
}


//Turnos
function getTurn(){
  var array=[]
  var turn = 0;
  gamesData.salvoes.map(function(salvo){
    if(salvo.player == getParameterByName('gp')){
      array.push(salvo.turn);
    }
  })
  turn = Math.max.apply(Math, array);

  if (turn == -Infinity){
    return 1;
  } else {
    return turn + 1;
  }

}

//Salvo seleccionar 5 y enviar post al back
function enviarSalvos(){
    var turno = getTurn()
    var locationsToShoot=[];
    $(".targetCell").each(function(){
        let location = $(this).attr("id").substring(5);
        let locationConverted = String.fromCharCode(parseInt(location[0]) + 65) + (parseInt(location[1]) + 1)

        locationsToShoot.push(locationConverted)
    })
    console.log(locationsToShoot)
    var url = "/api/games/players/" + getParameterByName("gp") + "/salvoes"
    $.post({
        url: url,
        data: JSON.stringify({salvoLocations:locationsToShoot}),
        dataType: "text",
        contentType: "application/json"
    })
    .done(function (response, status, jqXHR) {
        alert( "Salvo added: " + response );
        location.reload();
    })
    .fail(function (jqXHR, status, httpError){
        alert("Failed to add salvo: " + status + " " + httpError);
    })

}



//gets the locations of the ships from the back-end
function setShips(gamesData) {
    for (i = 0; i < gamesData.ships.length; i++) {
        //only the first position of a ship is needed. The remaining positions are given by the orientation and the number of cells
        let shipType = (gamesData.ships[i].shipType).toLowerCase()
        let x = +(gamesData.ships[i].locations[0].substring(1)) - 1 //the number of the first position belongs to the x axis. To match the framework structure beginning at 0, we must substract 1 from it
        let y = stringToInt(gamesData.ships[i].locations[0][0].toUpperCase())//the letter of the first position belongs to y axis. In this case we must transform the string to a number, starting from 0.
        let w
        let h
        let orientation
        if (gamesData.ships[i].locations[0][0] == gamesData.ships[i].locations[1][0]) {
            //if the letter of the first position is equal to letter of the second position, the ship orientation is horizontal.
            //Therefore, the width is equal to the length of the location array and the height is equal to 1
            w = gamesData.ships[i].locations.length
            h = 1
            orientation = "Horizontal"
        } else {
            h = gamesData.ships[i].locations.length
            w = 1
            orientation = "Vertical"
        }
        //Finally, the addWidget function adds the ships to the grid
        grid.addWidget($('<div id="' + shipType + '"><div class="grid-stack-item-content ' + shipType + orientation + '"></div><div/>'),
            x, y, w, h);
    }
}

//Bucle que consulta por todas las celdas para ver si estan ocupadas o no
const listenBusyCells = function(id, player, dataGral){
    /* id vendria a ser ships. Recordar el id de las celdas del tablero se arma uniendo
    la palabra ships + fila + columna contando desde 0. Asi la primer celda tendra id
    ships00 */
    if (player == dataGral) {
        for(let i = 0; i < 10; i++){
            for(let j = 0; j < 10; j++){
                if(!grid.isAreaEmpty(i,j)){
                                $(`#${id}${j}${i}`).addClass('busy-cell').removeClass('empty-cell')
                            } else{
                                $(`#${id}${j}${i}`).removeClass('busy-cell').addClass('empty-cell')
                            }
            }
        }
    }
}

//get the locations of the salvoes from the back-end
function setSalvoes(gamesData, idPlayerLogued) {
    for (i = 0; i < gamesData.salvoes.length; i++) {
        for (j = 0; j < gamesData.salvoes[i].locations.length; j++) {
            let turn = gamesData.salvoes[i].turn
            let player = gamesData.salvoes[i].player
            let x = +(gamesData.salvoes[i].locations[j][1]) - 1
            let y = stringToInt(gamesData.salvoes[i].locations[j][0].toUpperCase())
            if (player == idPlayerLogued) {
                document.getElementById(`salvo${y}${x}`).classList.add('salvo')
                document.getElementById(`salvo${y}${x}`).innerHTML = `<span>${turn}</span>`
            } else {
                if (document.getElementById(`ships${y}${x}`).className.indexOf('busy-cell') != -1) {
                    document.getElementById(`ships${y}${x}`).classList.remove('busy-cell')
                    document.getElementById(`ships${y}${x}`).classList.add('ship-down')
                    //document.getElementById(`ships${y}${x}`).innerHTML = `<span>${turn}</span>`
                }
            }
        }
    }
}



const stringToInt = function (str) {
    switch (str) {
        case "A":
            return 0;
        case "B":
            return 1;
        case "C":
            return 2;
        case "D":
            return 3;
        case "E":
            return 4;
        case "F":
            return 5;
        case "G":
            return 6;
        case "H":
            return 7;
        case "I":
            return 8;
        case "J":
            return 9;
    }
}

//Música
var reproducir = document.getElementsByTagName("audio");
reproducir.play();
