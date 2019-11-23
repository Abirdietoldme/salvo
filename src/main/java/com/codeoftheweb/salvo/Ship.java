package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String shipType;

    @ElementCollection
    @Column(name = "shipLocations")
    private List<String> shipLocations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    public Ship() {
        this.gamePlayer =   new GamePlayer();
    }

    public Ship(String shipType, List<String> shipLocations, GamePlayer gamePlayer) {
        this.shipType = shipType;
        this.shipLocations = shipLocations;
        this.gamePlayer = gamePlayer;
    }

    public Map<String,  Object> makeShipDTO(){
        Map<String,  Object>    dto =   new LinkedHashMap<>();
        dto.put("shipType", this.getShipType());
        dto.put("locations",    this.getShipLocations());
        return  dto;
    }


    public long getId() {

        return id;
    }

    public String getShipType() {

        return shipType;
    }

    public void setShipType(String shipType) {

        this.shipType = shipType;
    }

    public List<String> getShipLocations() {

        return shipLocations;
    }

    public void setShipLocations(List<String> shipLocations) {

        this.shipLocations = shipLocations;
    }

    public GamePlayer getGamePlayer() {

        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {

        this.gamePlayer = gamePlayer;
    }

}

