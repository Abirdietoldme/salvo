package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name ="native", strategy = "native")
    private long id;

    private String userName;

    @JsonIgnore
    @OneToMany(mappedBy ="player", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @JsonIgnore
    @OneToMany(mappedBy ="player", fetch = FetchType.EAGER)
    Set<Score> scores = new LinkedHashSet<>();

    private String password;

    public Player() {
    }

    public Player(String userName, String password) {

        this.userName = userName;
        this.password   =   password;
    }

    public Map<String,Object> makePlayerDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("email", this.getUserName());
        return dto;
    }



    public Set<Score> getScores() {

        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public long getId() {

        return id;
    }

    public String getUserName() {

        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public Set<GamePlayer> getGamePlayers() {

        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {

        this.gamePlayers = gamePlayers;
    }


    public float getWins(Set<Score> puntajes){
        return puntajes.stream().filter(puntaje -> puntaje.getScore() == 1).count();
    }

    public float getLosses(Set<Score> puntajes){
        return puntajes.stream().filter(puntaje -> puntaje.getScore() == 0).count();
    }

    public float getDraws(Set<Score> puntajes){
        return puntajes.stream().filter(puntaje -> puntaje.getScore() == (float) 0.5).count();
    }

    public float getTotalscore() {
        float victorias = getWins(this.getScores())*1;
        float empates = getDraws(this.getScores())* (float) 0.5;
        float derrotas = getLosses(this.getScores())*0;

        return victorias + empates + derrotas;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
