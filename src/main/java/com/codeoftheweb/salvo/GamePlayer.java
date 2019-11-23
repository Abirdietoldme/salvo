package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name ="native", strategy = "native")
    private long id;

    private Date joinDate = new Date();

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @OneToMany(mappedBy = "gamePlayer",fetch = FetchType.EAGER)
    private List<Ship> ships;

    @OneToMany(mappedBy = "gamePlayer",fetch = FetchType.EAGER)
    private Set<Salvo> salvoes;



    public GamePlayer() {

        this.joinDate = new Date();
        this.ships = new ArrayList<>();
        this.salvoes = new HashSet<>();
    }

    public GamePlayer(Game game, Player player) {
        this.joinDate = new Date();
        this.game = game;
        this.player = player;
    }

    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("hasShips", this.getShips().size());
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().makePlayerDTO());
        return dto;
    }


    public long getId() {

        return id;
    }

    public Date getJoinDate() {

        return joinDate;
    }

    public  void  setGame(Game  game){

        this.game = game;
    }

    public  void  setPlayer(Player  player){

        this.player = player;
    }

    public void setJoinDate(Date joinDate) {

        this.joinDate = joinDate;
    }

    public Game getGame() {

        return game;
    }

    public  Player  getPlayer(){

        return  this.player;
    }

    public List<Ship> getShips() {

        return ships;
    }

    public void setShips(List<Ship> ships) {

        this.ships = ships;
    }

    public Set<Salvo> getSalvoes() {

        return salvoes;
    }

    public void setSalvoes(Set<Salvo> salvoes) {

        this.salvoes = salvoes;
    }
}
