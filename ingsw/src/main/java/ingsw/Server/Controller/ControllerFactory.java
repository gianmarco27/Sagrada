package ingsw.Server.Controller;

import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Utility.GameException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Factory used to generate the different Controllers for each player, server side
 */
public interface ControllerFactory extends Remote {
    Controller createController(String name,GameType singlePlayer) throws RemoteException, GameException;
}
