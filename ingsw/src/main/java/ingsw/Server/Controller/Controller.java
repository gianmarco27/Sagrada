package ingsw.Server.Controller;

import ingsw.Server.Actions.DiceActionParameter;
import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Objective.PrivateObjective;
import ingsw.Server.Player;
import ingsw.Server.Utility.GameException;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Controller interface that is implemented by ControllerImpl and ControllerTCPClient
 */
public interface Controller extends Remote {

    GameBoard getGameBoard() throws GameException, IOException;

    boolean registerPlayer(String name,GameType singlePlayer) throws GameException, IOException;

    Player getPlayer() throws GameException, IOException;

    boolean chooseGrid(Grid possibleGrid) throws GameException, IOException;

    GameEvent getEvent() throws IOException;

    void ack() throws GameException, IOException;

    List<PrivateObjective> getAllPrivateObjective() throws GameException, IOException;

    void sendTurnBeginChoice(int c) throws GameException, IOException;

    boolean chooseAndPlaceDie(DiceActionParameter p) throws GameException, IOException;

    boolean useTap(ToolActionParameter tap) throws GameException, IOException;

    boolean back() throws GameException, IOException;

    Map<Player,Grid> getOthersGrids() throws GameException, IOException;

    void ping() throws GameException, RemoteException;
}
