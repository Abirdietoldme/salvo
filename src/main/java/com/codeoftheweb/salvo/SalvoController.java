package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private ShipRepository  shipRepository;



    //Autenticación
    @RequestMapping("/games")
    public Map<String, Object> getGameAll(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();

        if (isGuest(authentication)) {
            dto.put("player", "Guest");
        } else {
            Player player = playerRepository.findByUserName(authentication.getName()).get();
            dto.put("player", player.makePlayerDTO());
        }

        dto.put("games", gameRepository.findAll()
                .stream()
                .map(game -> game.makeGameDTO())
                .collect(Collectors.toList()));
        return dto;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }


    @RequestMapping("/game_view/{nn}")
    public ResponseEntity<Map<String, Object>> getGameViewByGamePlayerID(@PathVariable Long nn, Authentication authentication) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "Not autorized. You have to be logged in"), HttpStatus.UNAUTHORIZED);
        }

        Player player = playerRepository.findByUserName(authentication.getName()).orElse(null);
        GamePlayer gamePlayer = gamePlayerRepository.findById(nn).orElse(null);

        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "Authentication failed"), HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer == null) {
            return new ResponseEntity<>(makeMap("error", "Authentication failed"), HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer.getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(makeMap("error", "Fail"), HttpStatus.CONFLICT);
        }


        Map<String, Object> hits = new LinkedHashMap<>();
        hits.put("self", new ArrayList<>());
        hits.put("opponent", new ArrayList<>());

        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getGame().getId());
        dto.put("created", gamePlayer.getGame().getFechaJuego());
        dto.put("gameState", "PLACESHIPS");

        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers()
                .stream()
                //Borrar comentario si funciona la linea de abajo
                .sorted(Comparator.comparing(GamePlayer::getId))
                .map(gp1 -> gp1.makeGamePlayerDTO())
                .collect(Collectors.toList()));
        dto.put("ships", gamePlayer.getShips()
                .stream()
                .map(ship -> ship.makeShipDTO())
                .collect(Collectors.toList()));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers()
                .stream()
                .flatMap(gp1 -> gp1.getSalvoes()
                        .stream()
                        .map(salvo -> salvo.makeSalvoDTO()))
                .collect(Collectors.toList()));
        dto.put("hits", gamePlayer.barcosgolpeados());


        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @RequestMapping(path = "/game/{id}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long id, Authentication authentication) {
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "You can't join to game. You have to Login!"), HttpStatus.UNAUTHORIZED);
        }

        Player player = playerRepository.findByUserName(authentication.getName()).orElse(null);
        Game gameToJoin = gameRepository.getOne(id);


        if (gameRepository.getOne(id) == null) {
            return new ResponseEntity<>(makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }

        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }

        long gamePlayersCount = gameToJoin.getGamePlayers().size();

        if (gamePlayersCount == 1) {
            GamePlayer gameplayer = gamePlayerRepository.save(new GamePlayer(gameToJoin, player));
            return new ResponseEntity<>(makeMap("gpid", gameplayer.getId()), HttpStatus.CREATED);

        } else {
            return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
        }

    }


    @RequestMapping("/leaderBoard")
    public List<Map<String, Object>> MakeLeaderBoard() {

        return playerRepository.findAll()
                .stream()
                .map(player -> playerLeaderBoardDTO(player))
                .collect(Collectors.toList());
    }


    private Map<String, Object> playerLeaderBoardDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("userName", player.getUserName());
        dto.put("score", getScoreList(player));
        //poner .sort
        return dto;
    }

    private Map<String, Object> getScoreList(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("total", player.getTotalscore());
        dto.put("won", player.getWins(player.getScores()));
        dto.put("tied", (player.getDraws(player.getScores())));
        dto.put("lost", (player.getLosses(player.getScores())));
        return dto;
    }

    //
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String username, @RequestParam String password) {

        if (username.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(username).orElse(null) != null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(username, passwordEncoder.encode(password)));
        return new ResponseEntity<>("Jugador creado", HttpStatus.CREATED);
    }


    //Add salvoes
    @RequestMapping(value = "/games/players/{gpId}/salvoes", method = RequestMethod.POST)
    private ResponseEntity<Map> AddSalvoes(@PathVariable long gpId,
                                           @RequestBody Salvo salvo,
                                           Authentication authentication) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "No player logged in"), HttpStatus.UNAUTHORIZED);
        }

        GamePlayer gamePlayer = gamePlayerRepository.findById(gpId).orElse(null);

        Player player = playerRepository.findByUserName(authentication.getName()).orElse(null);

        if (player==null){
            return new ResponseEntity<>(makeMap("error", "No está autorizado"), HttpStatus.UNAUTHORIZED);

        }


        if(gamePlayer==null){
            return new ResponseEntity<>(makeMap("error", "No existe el Game player"), HttpStatus.UNAUTHORIZED);

        }

        if(gamePlayer.getPlayer().getId() != player.getId()){
            return new ResponseEntity<>(makeMap("error", "Los players no coinciden"), HttpStatus.FORBIDDEN);

        }

        if(gamePlayer.getShips().isEmpty()){
            return new ResponseEntity<>(makeMap("error", "No está autorizado, You don't have ships"), HttpStatus.FORBIDDEN);

        }

        //Comprobar si estoy disparando mas de 5 tiros

        if(salvo.getSalvoLocations().size() > 5){
            return new ResponseEntity<>(makeMap("error", "You cant shoot more than 5 salvoes"), HttpStatus.FORBIDDEN);

        }

        //Si ya disparé en este turno no me deja disparar, si no disparé me guarda la jugada

        //if(!turnHasSalvoes(salvo, gamePlayer.getSalvoes())) {

        if(gamePlayer.getSalvoes().size()   <=  gamePlayer.getOpponent().getSalvoes().size()){
            salvo.setTurn(gamePlayer.getSalvoes().size() + 1);
            salvo.setGamePlayer(gamePlayer);
            salvoRepository.save(salvo);

            return new ResponseEntity<>(makeMap("ok", "Salvoes added"), HttpStatus.CREATED);
        }


        return new ResponseEntity<>(makeMap("OK", "You have fired salvoes in this turn"), HttpStatus.FORBIDDEN);

    }

    //Chequea si ya jugué ese turno que estoy intentando crear
    public boolean turnHasSalvoes(Salvo newSalvo,
                                  Set<Salvo> playerSalvoes){
        boolean hasSalvoes = false;
        for (Salvo salvo: playerSalvoes){
            if (salvo.getTurn() == newSalvo.getTurn()){
                hasSalvoes = true;

            }
        }
        return hasSalvoes;
    }


}