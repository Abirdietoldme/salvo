package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")

public class GameController {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository  gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;


    //Crear juego
    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Object> createGame(Authentication authentication) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>("User NOT autorized", HttpStatus.UNAUTHORIZED);
        }
        Player  player  = playerRepository.findByUserName(authentication.getName()).orElse(null);

        if(player ==  null){
            return new ResponseEntity<>("User NOT autorized", HttpStatus.UNAUTHORIZED);
        }

        Game  game  = gameRepository.save(new Game());
        GamePlayer  gamePlayer  = gamePlayerRepository.save(new GamePlayer(game,player));

        return new ResponseEntity<>(makeMap("gpid",gamePlayer.getId()),HttpStatus.CREATED);
    }

    @RequestMapping(path = "/games/{id}/players", method = RequestMethod.POST)
    public ResponseEntity<Object> joinGame(@RequestParam Long id, Authentication authentication) {

        ResponseEntity responseEntity;
        Player  player  =   playerRepository.findByUserName(authentication.getName()).orElse(null);
        Game game = gameRepository.findById(id).orElse(null);
        GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game,player));


        if ( isGuest(authentication)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        if (player ==  null) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        if (game ==  null) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(makeMap("gpid",gamePlayer.getId()),HttpStatus.CREATED);
    }


    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

}
