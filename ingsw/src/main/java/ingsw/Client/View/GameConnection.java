package ingsw.Client.View;

import ingsw.Server.Controller.Controller;
import ingsw.Server.Controller.ControllerFactory;
import ingsw.Server.Controller.ControllerTCPClient;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Utility.GameException;
import ingsw.Settings;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

/**
 * Class that provide an abstraction around the connection between Client and Server and handle the parameter for setting up the connection
 */
public class GameConnection{
    private static final Logger log = Logger.getLogger( GameConnection.class.getName() );
    private String username;
    private boolean connected;
    private ConnectionType connectionType;
    private String remoteIP;
    private View v;
    private GameType gameType;
    private boolean gameFinished;
    public GameConnection(View v){
        this.v = v;
        username = null;
        connected = false;
        connectionType = ConnectionType.RMI;
        remoteIP = Settings.SERVER_IP;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public void connect(String name) throws IOException, NotBoundException, GameException, ClassNotFoundException {
        this.username = name;
        if (connectionType == ConnectionType.RMI) {
            v.setController(connectRMI());
        }
        if (connectionType ==  ConnectionType.TCP) {
            v.setController(connectTCP());
        }
    }
    private Controller connectTCP() throws IOException, ClassNotFoundException,GameException {
        log.info("Connecting at \"" + remoteIP + ":"+Settings.TCP_PORT+":"+Settings.TCP_PORT2);
        Controller myController = new ControllerTCPClient(remoteIP,Settings.TCP_PORT,Settings.TCP_PORT2,username,gameType);
        this.connected = true;
        return myController;
    }
    private Controller connectRMI() throws IOException, NotBoundException, GameException {
        Registry registry;
        log.info("Connecting at \"" + remoteIP+ ":" +Settings.RMI_PORT);
        registry = LocateRegistry.getRegistry(remoteIP, Settings.RMI_PORT);
        ControllerFactory cf = (ControllerFactory) registry.lookup("controllerFactory");
        Controller myController = cf.createController(username,gameType);
        this.connected = true;
        new Thread(() -> {
            while (true) {
                try {
                    myController.ping(); // Sending the ping to the server
                    Thread.sleep(Settings.PING_TIMEOUT/2);
                } catch (RemoteException e) {
                    this.connected = false;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
        return myController;
    }

    public boolean isSinglePlayer() {
        return gameType == GameType.SINGLEPLAYER;
    }

    public void setSinglePlayer(boolean singlePlayer) {
        if (singlePlayer)
            this.gameType = GameType.SINGLEPLAYER;
        else
            this.gameType = GameType.MULTIPLAYER;
    }

    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }

    public boolean getGameFinished() {
        return gameFinished;
    }
}
