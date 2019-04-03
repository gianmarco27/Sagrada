package ingsw.Server;

import ingsw.Server.Controller.GameEvent;
import ingsw.Server.Dice.DiceBag;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Objective.PrivateObjective;
import ingsw.Server.Objective.PublicObjective;
import ingsw.Server.Tools.Tool;
import ingsw.Server.Utility.*;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * GameBoard is the main class used inside the game.
 * It's use is to hold and keep track of the various pieces of information related to the state of a game.
 * It's a snapshot of the current situation, including players, grids and various game components.
 */
public class GameBoard implements Serializable {
    private static final Logger log = Logger.getLogger( GameBoard.class.getName() );

    private List<Player> players;
    private Map<Player, List<PrivateObjective>> privateObjectives;
    private RoundTracker roundTracker;
    private List<PublicObjective> publicObjectives;
    private List<Tool> tools;
    private ScoreBoard scoreBoard;
    private DicePool dicePool;
    private TurnHandler turnHandler;
    private int currentTurn;
    private transient final Map<Player, BlockingQueue<GameEvent>> queues;
    private GameFlowState currentState;
    private DiceBag diceBag;
    private GameType gameType;

    public GameBoard() {
        this.players = new CopyOnWriteArrayList<>();
        this.roundTracker = new RoundTracker();
        this.publicObjectives = new CopyOnWriteArrayList<>();
        this.tools = new CopyOnWriteArrayList<>();
        this.dicePool = new DicePool();
        this.privateObjectives = new HashMap<>();
        this.scoreBoard = new ScoreBoard();
        this.currentTurn = 0;
        this.queues = new HashMap<>();
    }


    public BlockingQueue<GameEvent> getQueues(Player p) {
        return queues.get(p);
    }

    public void deleteQueue(Player p){
        queues.remove(p);
    }

    public void pushUpdate() {
        GameEvent event = new GameEvent("Gameboard update");
        event.setUpdate(true);
        pushGameEvent(event);
    }

    public boolean removePlayer(Player p) {
        boolean removed = players.remove(p);
        if (removed) {
            deleteQueue(p);
            pushUpdate();
            return true;
        }
        return false;
    }

    public void addPlayer(Player p) throws GameException{
        if (!players.contains(p)) {
            players.add(p);
            createQueue(p);
            pushUpdate();
        } else {
            throw new GameException("Player name already in use!");
        }
    }

    public void createQueue(Player p){
        queues.put(p, new LinkedBlockingQueue<>());
    }

    public void setPlayers(){
        this.turnHandler = new TurnHandler(players);
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void nextTurn() {
        currentTurn++;
    }

    public List<Player> getPlayers() {
        return players;
    }
    public List<Player> getActivePlayers() {
        return players.stream().filter(Player::isConnected).collect(Collectors.toList());
    }

    public void setRoundTracker(RoundTracker roundTracker) {
        this.roundTracker = roundTracker;
    }

    public RoundTracker getRoundTracker() {
        return roundTracker;
    }

    public void setPublicObjectives(List<PublicObjective> publicObjectives) {
        this.publicObjectives = publicObjectives;
    }

    public List<PublicObjective> getPublicObjectives() {
        return publicObjectives;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public List<Player> getPlayersOrder() {
        return turnHandler.getPlayersOrder();
    }

    public DicePool getDicePool() {
        return dicePool;
    }
    /* END of Getter and Setter */

    public Player getCurrentPlayer() {
        return turnHandler.getCurrentPlayer();
    }

    public void calculateScore() {
        scoreBoard.calculateScore(this, gameType);
    }

    public String getScoreBoardStanding() {
        return scoreBoard.toString(true);
    }

    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    public boolean nextPlayer() {
        return turnHandler.hasNextPlayer();
    }


    public GameFlowState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameFlowState currentState) {
        this.currentState = currentState;
        //pushUpdate();
    }

    public PrivateObjective getPrivateObjective(Player p) {
        return privateObjectives.get(p).get(0);
    }

    public List<PrivateObjective> getAllPrivateObjective(Player p) { return privateObjectives.get(p); }

    public void setPrivateObjective(Player p, PrivateObjective privateObjective) {
        if (!this.privateObjectives.containsKey(p)) {
            privateObjectives.put(p, new CopyOnWriteArrayList<>());
        }
        privateObjectives.get(p).add(privateObjective);
    }

    public void removeEveryPrivateObjExcept(Player p) {
        privateObjectives.entrySet().removeIf(entry -> !entry.getKey().equals(p));
    }


    public void setDiceBag(DiceBag diceBag) {
        this.diceBag = diceBag;
    }

    public DiceBag getDiceBag() {
        return this.diceBag;
    }

    public Player getPlayer(String name) {
        List<Player> findings = players.stream().filter( p -> p.getName().equals(name)).collect(Collectors.toList());
        if (findings.size() == 1) {
            return findings.get(0);
        }
        if (findings.size() == 0) {
            return null;
        }
        log.warning("Found multiple players with the same name!!!");
        return null;
    }

    private void pushGameEvent(GameEvent ge){
        for (Player p : queues.keySet()){
            try {
                queues.get(p).put(ge);
//                log.info("NEW EVENT for player "+p.getName());
            } catch (InterruptedException e) {
                log.warning("Succedono cose a livello di en-queuing.");
                Thread.currentThread().interrupt();
                }
        }
    }
    public void notifySinglePlayer(Player p) {
        try {
            GameEvent event = new GameEvent("Gameboard update");
            event.setUpdate(true);
            queues.get(p).put(event);
        } catch (InterruptedException e) {
            log.warning("Succedono cose a livello di en-queuing.");
            Thread.currentThread().interrupt();
            }
    }

    public void notifyDisconnection(Player p) {
        GameEvent ge = new GameEvent("Player "+p.getName()+" disconnected.");
        ge.setDisconnection(true);
        pushGameEvent(ge);
    }

    public void notifyTimeout(Player p) {
        GameEvent ge = new GameEvent("Player "+p.getName()+ " timed out and have been disconnected.");
        ge.setTimeout(true);
        pushGameEvent(ge);
    }

    public void notifyConnection(Player p) {
        GameEvent ge = new GameEvent("Player "+p.getName()+" reconnected.");
        ge.setDisconnection(true);
        pushGameEvent(ge);
    }

    public void setGameType(GameType type) {
        this.gameType = type;
    }

    public GameType getGameType() {
        return gameType;
    }
}