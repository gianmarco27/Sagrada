package ingsw.Server.GameFlow;

import ingsw.Server.Dice.DiceBag;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameFlowStates.*;
import ingsw.Server.Grid.GridBag;
import ingsw.Server.Objective.ObjectiveBag;
import ingsw.Server.Player;
import ingsw.Server.Tools.ToolsBag;
import ingsw.Server.Utility.GameException;

import java.io.Serializable;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * This is the StateMachine handler which is responsible for the transition between the whole state set
 */
public class GameFlowHandler {
    private static final Logger log = Logger.getLogger( GameFlowHandler.class.getName() );

    private boolean gameDone;
    public GameBoard gameBoard;
    // Bag of Things
    public DiceBag diceBag;
    public ObjectiveBag objectiveBag;
    public ToolsBag toolsBag;
    public GridBag gridsBag;
    public boolean startOfTurn;
    public GameType gameType;

    public GenericState setupState;
    public GenericState playerRegistrationState;
    public GenericState playerTurnBeginState;
    public GenericState turnHandlerState;
    public GenericState diceActionState;
    public GenericState toolActionState;
    public GenericState playerTurnEndState;
    public GenericState gameFinishState;
    public GenericState privateObjeSetupState;
    public GenericState gridSetupState;
    public GenericState toolsSetupState;
    public GenericState commonObjSetupState;

    private GenericState current;

    public GameFlowHandler() {
        this.gameBoard = new GameBoard();
        this.playerRegistrationState = new PlayerRegistrationState(this);
        this.setupState = new SetupState(this);
        this.privateObjeSetupState = new PrivateObjSetupState(this);
        this.gridSetupState = new GridSetupState(this);
        this.toolsSetupState = new ToolsSetupState(this);
        this.commonObjSetupState = new CommonObjSetupState(this);
        this.turnHandlerState = new TurnHandlerState(this);
        this.playerTurnBeginState = new PlayerTurnBeginState(this);
        this.diceActionState = new DiceActionState(this);
        this.toolActionState = new ToolActionState(this);
        this.playerTurnEndState = new PlayerTurnEndState(this);
        this.gameFinishState = new GameFinishState(this);

        this.setState(playerRegistrationState);
        setGameStatus(false);
    }

    /**
     * @return if the game is ended or not
     */
    public boolean isGameDone() {
        return gameDone;
    }

    /**
     * Setter for specifying if the game is ended or not
     * @param status
     */
    public void setGameStatus(boolean status) {
        gameDone = status;
    }

    /**
     * Transition function to handle the change between different state
     * @param toSet state to switch to
     */
    public void setState(GenericState toSet) {
        log.info("Changing state to : " + toSet.getState());
        this.current = toSet;
        this.gameBoard.setCurrentState(toSet.getState());
    }

    public int handleChoices(int minValue, int maxValue) {
        Scanner input = new Scanner(System.in);
        int val;
        do {
            System.out.print("Pick a choice from " + minValue + " to " + maxValue + "\n");
            val = input.nextInt();
        } while (val < minValue || val > maxValue);
        return val;
    }

    /**
     * Until the game is not ended we execute the instructions contained in the current states
     */
    public void run() {
        while (!isGameDone()) {
            current.execute();
        }
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public GenericState getCurrent() {
        return current;
    }

    /**
     * Method which handle the disconnection of a given player
     * @param p player to disconnect
     * @param timedOut specify if the user timedOut or disconnected
     */
    public void disconnectPlayer(Player p, boolean timedOut) {
        if (!gameBoard.getPlayers().contains(p)) {
            log.warning("Trying to disconnect a player that is not in the game!");
            return;
        }
        Player actual = gameBoard.getPlayers().get(gameBoard.getPlayers().indexOf(p));
        if (!actual.isConnected()) { //when a player with tcp connenction fails we get two calls to the disconnect player
            return;
        }
        log.info("Disconnecting : " + p.getName());
        actual.setConnected(false);
        gameBoard.deleteQueue(p);
        if (timedOut) {
            gameBoard.notifyTimeout(p);
        }
        else {
            gameBoard.notifyDisconnection(p);
        }
        if (current.getState() ==  GameFlowState.PLAYERREGISTRATION) {
            ((PlayerRegistrationState)playerRegistrationState).removePlayer(p);
            return;
        }
        if (current.getState() == GameFlowState.GRIDSETUP) {
            if (p.getGrid() == null){
                try {
                    ((GridSetupState)current).chooseGrid(p, p.getPossibleGrids()[0]);
                } catch (GameException e) {
                    actual.setGrid(actual.getPossibleGrids()[0]);
                }
            }
            return;
        }
        if (gameBoard.getCurrentPlayer().equals(p)) {
            current.endTurn();
        }
        current.ack(p);
    }

    /**
     * Connect a player to the game
     * @param p player to connect
     * @return if the operation was successful
     */
    public boolean connectPlayer(Player p) {
        if (!gameBoard.getPlayers().contains(p)) {
            log.warning("Trying to connect a player that is not in the game!");
            return false;
        }
        Player actual = gameBoard.getPlayers().get(gameBoard.getPlayers().indexOf(p));
        if (actual.isConnected()) {
            log.warning("Player is already connected.");
            return false;
        }
        log.info("Disconnecting : " + p.getName());
        gameBoard.notifyConnection(actual);
        actual.setConnected(true);
        gameBoard.createQueue(actual);
        gameBoard.notifySinglePlayer(actual);
        return true;
    }

    /**
     * Setter for setting up the GameType, currently SINGLEPLAYER and MULTIPLAYER
     * @param type
     */
    public void setGameType(GameType type) {
        this.gameType = type;
        gameBoard.setGameType(type);
    }
}
