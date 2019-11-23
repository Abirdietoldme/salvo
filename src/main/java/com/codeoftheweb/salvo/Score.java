package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;



@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private float score;

    private Date finishDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    public Score(float score, Date finishDate) {
        this.finishDate = finishDate;
    }

    public Score(float score, Date finishDate, Player player, Game game) {
        this.score = score;
        this.finishDate = finishDate;
        this.player = player;
        this.game = game;
    }

    public Map<String,  Object> makeScoreDTO(){
        Map<String,  Object>    dto=    new LinkedHashMap<>();
        dto.put("player",   this.getPlayer().getId());
        dto.put("score",   this.getScore());
        dto.put("finishDate", this.getFinishDate().getTime());
        return  dto;
    }

    public Score() {
    }

    public long getId() {
        return id;
    }

    public float getScore() {

        return score;
    }

    public void setScore(float score) {

        this.score = score;
    }

    public Date getFinishDate() {

        return finishDate;
    }

    public void setFinishDate(Date finishDate) {

        this.finishDate = finishDate;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public Game getGame() {

        return game;
    }

    public void setGame(Game game) {

        this.game = game;
    }


}
