package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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


    //Determinar cual es mi oponente
    @JsonIgnore
    public GamePlayer   getOpponent(){
        return  this.game.getGamePlayers()
                .stream()
                .filter(gamePlayer -> gamePlayer.getId() !=  this.getId())
                .findFirst()
                .orElse(new GamePlayer());
    }

//Diferenciar Hits a mis barcos y al oponente
    public Map <String, Object>barcosgolpeados(){
        Map<String,Object>  hits =   new LinkedHashMap<>();
        if (this.getGame().getGamePlayers().size() > 1) {
            hits.put("opponent", calculateHits(this.getOpponent(), this));
        }else{
            hits.put("opponent", new ArrayList<>());
        }
        hits.put("self", calculateHits(this, this.getOpponent()));
        return hits;
    }

    //Calcular a qué barcos le pegué
    private List<Map> calculateHits(GamePlayer self,
                              GamePlayer opponent) {
        List<Map> dto = new ArrayList<>();

        int carrierDamage = 0;
        int destroyerDamage = 0;
        int patrol_boatDamage = 0;
        int submarineDamage = 0;
        int battleshipDamage = 0;
        List<String> carrierLocations = new ArrayList<>();
        List<String> destroyerLocations = new ArrayList<>();
        List<String> submarineLocations = new ArrayList<>();
        List<String> patrol_boatLocations = new ArrayList<>();
        List<String> battleshipLocations = new ArrayList<>();

        for (Ship ship: self.getShips()) {
            switch (ship.getShipType()){
                case "carrier":
                    carrierLocations = ship.getShipLocations();
                    break ;
                case "battleship" :
                    battleshipLocations = ship.getShipLocations();
                    break;
                case "destroyer":
                    destroyerLocations = ship.getShipLocations();
                    break;
                case "submarine":
                    submarineLocations = ship.getShipLocations();
                    break;
                case "patrol_boat":
                    patrol_boatLocations = ship.getShipLocations();
                    break;
            }
    }

        List<Salvo> opponentSalvo = opponent.getSalvoes().stream().sorted(Comparator.comparing(Salvo::getTurn)).collect(Collectors.toList());
        for (Salvo salvo : opponentSalvo){
            Integer carrierHitsInTurn = 0;
            Integer battleshipHitsInTurn = 0;
            Integer submarineHitsInTurn = 0;
            Integer destroyerHitsInTurn = 0;
            Integer patrol_boatHitsInTurn = 0;
            Integer missedShots = salvo.getSalvoLocations().size();
            Map<String, Object> hitsMapPerTurn = new LinkedHashMap<>();
            Map<String, Object> damagesPerTurn = new LinkedHashMap<>();
            List<String> salvoLocationsList = new ArrayList<>();
            List<String> hitCellsList = new ArrayList<>();
            salvoLocationsList.addAll(salvo.getSalvoLocations());
            for (String salvoShot : salvoLocationsList) {
                if (carrierLocations.contains(salvoShot)) {
                    carrierDamage++;
                    carrierHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
                if (battleshipLocations.contains(salvoShot)) {
                    battleshipDamage++;
                    battleshipHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
                if (submarineLocations.contains(salvoShot)) {
                    submarineDamage++;
                    submarineHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
                if (destroyerLocations.contains(salvoShot)) {
                    destroyerDamage++;
                    destroyerHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
                if (patrol_boatLocations.contains(salvoShot)) {
                    patrol_boatDamage++;
                    patrol_boatHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
            }


            damagesPerTurn.put("carrierHits", carrierHitsInTurn);
            damagesPerTurn.put("battleshipHits", battleshipHitsInTurn);
            damagesPerTurn.put("submarineHits", submarineHitsInTurn);
            damagesPerTurn.put("destroyerHits", destroyerHitsInTurn);
            damagesPerTurn.put("patrol_boatHits", patrol_boatHitsInTurn);
            damagesPerTurn.put("carrier", carrierDamage);
            damagesPerTurn.put("battleship", battleshipDamage);
            damagesPerTurn.put("submarine", submarineDamage);
            damagesPerTurn.put("destroyer", destroyerDamage);
            damagesPerTurn.put("patrol_boat", patrol_boatDamage);
            hitsMapPerTurn.put("turn", salvo.getTurn());
            hitsMapPerTurn.put("hitLocations", hitCellsList);
            hitsMapPerTurn.put("damages", damagesPerTurn);
            hitsMapPerTurn.put("missed", missedShots);
            dto.add(hitsMapPerTurn);
        }

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
