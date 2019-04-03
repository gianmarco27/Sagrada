package ingsw.Server.Controller;

import ingsw.Server.Actions.DiceActionParameter;
import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowStates.*;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Objective.PrivateObjective;
import ingsw.Server.Player;
import ingsw.Server.Utility.GameException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ingsw.Server.Utility.CopyObjects.deepCopy;

public class ControllerImpl extends UnicastRemoteObject implements Controller{
    private static final Logger log = Logger.getLogger( ControllerImpl.class.getName() );
    private GameFlowHandler gfh;
    private Player player;
    private RMIPing rmiPing = null;

    public ControllerImpl(GameFlowHandler gfh,String name,GameType gameType) throws RemoteException,GameException {
        this.gfh = gfh;
        if (!registerPlayer(name,gameType)) {
            throw new GameException("Error creating the player");
        }
    }

    /**
     * This method runs the Ping in RMI in loop and if it stops it disconnects the Player in the GameFlowHandler
     */
    private void startPingServer() {
        new Thread(() -> {
            rmiPing.pingServerLoop();
            gfh.disconnectPlayer(player,false);
            log.warning("Player "+ player.getName() + " timed out in the ping mechanism.");
        }).start();
    }

    /**
     * This method starts the RMIPing and removes the lock on the ping object if it fails
     */
    public void ping(){
        if (rmiPing == null){
            rmiPing = new RMIPing();
            startPingServer();
        }
        rmiPing.ping();
    }

    /**
     * prepares a deep copy of the GameFlowHandler Gameboard and removes all the Private objectives that don't belong to the player asking it
     */
    public GameBoard getGameBoard() {
        GameBoard localCopy = (GameBoard) deepCopy(gfh.gameBoard);
        if (localCopy != null) {
            localCopy.removeEveryPrivateObjExcept(this.player);
            return localCopy;
        }
        throw new GameException("Error getting the gameboard.");
    }

    /**
     * This method retrieves the Event Queue of the player asking it
     */
    public GameEvent getEvent() {
        BlockingQueue<GameEvent> queue = gfh.getGameBoard().getQueues(player);
        if (queue == null){
            throw new GameException("Error getting the event queue.");//this mean not connected
        }
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GameException("Event listener interrupted");
        }
    }

    /**
     * @return the player binded to this controller
     */
    public Player getPlayer(){
        return this.player;
    }

    /**
     * THis Method is used to communicate the player choice for the grid to use in the game
     */
    public boolean chooseGrid(Grid possibleGrid) throws GameException {
        if (gfh.getCurrent() instanceof GridSetupState){
            if (this.player.getGrid() == null){
                ((GridSetupState)gfh.gridSetupState).chooseGrid(this.player,possibleGrid);
                if (this.player.getGrid() != null){
                    this.player.setPossibleGrids(null);
                    return true;
                }
                throw new GameException("Failed to assign grid.");
            } else {
                throw new GameException("You are already registered!");
            }
        } else {
            throw new GameException("You cannot choose the grid in this moment of the game!");
        }
    }

    /**
     * General method to handle reconnection to the current Game
     */
    public boolean registerPlayer(String name,GameType gameType) throws GameException {
        //if cannot reconnect try creating a new one
        if (gfh.getCurrent() instanceof PlayerRegistrationState){
            return createNewPlayer(name,gameType);
        } else {
            return reconnectPlayer(name,gameType);
        }
    }

    /**
     * If in the correct state this method is used to register a new player into the game
     */
    private boolean createNewPlayer(String name,GameType gameType){
        if (this.player == null) {
            this.player = ((PlayerRegistrationState) gfh.playerRegistrationState).registerPlayer(name, gameType);
            if (this.player != null) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new GameException("You are already registered!");
        }

    }

    /**
     * this Method is used to reconnect a player the current game
     */
    private boolean reconnectPlayer(String name, GameType gameType){
        //trying to reconnect
        Player oldPlayer = gfh.getGameBoard().getPlayer(name);
        if (oldPlayer != null) {
            if (!oldPlayer.isConnected()) {
                player = oldPlayer;
                if (!gfh.connectPlayer(player)) {
                    throw new GameException("Failed to connect to the game as a disconnected player.");//Should not happen.
                }
                return true;
            } else {
                throw new GameException("A player with this name is already connected to this running game!");
            }
        }
        throw new GameException("New players cannot register during the game!");
    }

    /**
     * Ack method to communicate the server the player received the message sent
     */
    public void ack(){
        gfh.getCurrent().ack(this.player);
    }

    public List<PrivateObjective> getAllPrivateObjective() {
        return gfh.getGameBoard().getAllPrivateObjective(this.player);
    }

    /**
     * This method is used to communicate the server if the player choose to play a dice or a tool making it enter the relative state
     */
    @Override
    public void sendTurnBeginChoice(int c) {
        if (gfh.getCurrent() instanceof PlayerTurnBeginState){
            if (gfh.getGameBoard().getCurrentPlayer().equals(player)) {
                ((PlayerTurnBeginState) gfh.playerTurnBeginState).setChoice(c);
            } else {
                throw new GameException("Cannot do an action in others turn.");
            }
        } else {
            throw new GameException("Not at the beginning of a turn.");
        }
    }

    /**
     * This method is used to communicate what Dice did the player choose and where to place it
     */
    @Override
    public boolean chooseAndPlaceDie(DiceActionParameter p) {
        if (gfh.getCurrent() instanceof DiceActionState){
            if (gfh.getGameBoard().getCurrentPlayer().equals(player)) {
                return ((DiceActionState) gfh.diceActionState).setChoice(p);
            } else {
                throw new GameException("Cannot place a dice in other players turn");
            }
        } else {
            throw new GameException("Cannot place a dice in this state of the game.");
        }
    }

    /**
     * This method is used to communicate what Tool did the player choose and the parameters connected to it
     */
    @Override
    public boolean useTap(ToolActionParameter tap) {
        if (gfh.getCurrent() instanceof ToolActionState) {
            if (gfh.getGameBoard().getCurrentPlayer().equals(player)) {
                return ((ToolActionState) gfh.toolActionState).useTap(tap);
            } else {
                throw new GameException("Cannot use a tool in other players turn");
            }
        } else {
            throw new GameException("Cannot use a tool, not in the correct state.");
        }
    }

    /**
     * This method is used to step back from the specific state we are in
     */
    @Override
    public boolean back() {
        if (gfh.getCurrent() instanceof ToolActionState) {
            return ((ToolActionState) gfh.getCurrent()).back();
        } else if (gfh.getCurrent() instanceof DiceActionState) {
            return ((DiceActionState) gfh.getCurrent()).back();
        }
        return false;
    }

    /**
     * This Methos is used to retrieve the opponents Grids
     */
    public Map<Player,Grid> getOthersGrids(){
        return getGameBoard().getPlayers().stream()
                .filter(p -> !p.equals(this.player))
                .collect(Collectors.toMap(Function.identity(), p -> p.getGrid()));
    }

}
