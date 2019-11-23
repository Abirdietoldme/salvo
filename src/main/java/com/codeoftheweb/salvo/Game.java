package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {


    @Id
    @GeneratedValue (strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name ="native", strategy = "native")
    private long id;

    private Date fechaJuego;

    @JsonIgnore
    @OneToMany (mappedBy ="game", fetch = FetchType.EAGER)
    private Set <GamePlayer> gamePlayers;

    @JsonIgnore
    @OneToMany(mappedBy ="game", fetch = FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();

    public Map<String, Object> makeGameDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("created", this.getFechaJuego());
        dto.put("gamePlayers", this.getGamePlayers()
                .stream()
                .map(gamePlayer -> gamePlayer.makeGamePlayerDTO())
                .collect(Collectors.toList()));
        dto.put("score", this.getAllScores(getScores()));
        return  dto;

    }

    public Game() {

    }

    public Game(Date fechaJuego) {

        this.fechaJuego = fechaJuego;
    }

    public long getId() {

        return id;
    }

    public Date getFechaJuego() {

        return fechaJuego;
    }

    public void setFechaJuego(Date fechaJuego) {

        this.fechaJuego = fechaJuego;
    }

    public Set<GamePlayer> getGamePlayers() {

        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {

        this.gamePlayers = gamePlayers;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    private List<Map<String, Object>>getAllScores(Set<Score> scores) {
        return scores.stream().map(Score -> Score.makeScoreDTO()).collect(Collectors.toList());
    }

}

