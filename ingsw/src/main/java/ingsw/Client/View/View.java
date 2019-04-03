package ingsw.Client.View;

import ingsw.Server.Controller.Controller;
import ingsw.Server.Controller.GameEvent;
import ingsw.Server.GameBoard;
import ingsw.Server.Utility.GameException;
import ingsw.Settings;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import static ingsw.Client.View.HandleChoices.handleChoices;

public abstract class View extends UnicastRemoteObject implements Serializable {
    private static final Logger log = Logger.getLogger( View.class.getName() );
    volatile Controller c =  null;
    CountDownLatch controllerSet = new CountDownLatch(1);
    GameBoard gameBoard;
    protected GameConnection conn;


    protected View() throws RemoteException {
    }

    public abstract void run() throws IOException, InterruptedException, ClassNotFoundException;

    public void initConnection(){
        conn.setRemoteIP(chooseRemoteIP());
        conn.setConnectionType(chooseConnectionType());
        conn.setSinglePlayer(chooseModality());
    }

    String chooseRemoteIP() {
        Scanner scan = new Scanner(new InputStreamReader(System.in));
        System.out.println("Insert server URL or IP, press enter for using the default settings");
        String ip = scan.nextLine();
        if (ip.equals("")) {
            ip = Settings.SERVER_IP;
        }
        return ip;
    }
    ConnectionType chooseConnectionType() {
        System.out.println("Choose a connection method :\n1 - TCP\n2 - RMI");
        int choice = handleChoices(1, 2);
        if (choice == 1)
            return ConnectionType.TCP;
        else
            return ConnectionType.RMI;
    }
    private boolean chooseModality() {
        System.out.println("Choose a modality:\n1 - Singleplayer\n2 - Multiplayer");
        int choice = handleChoices(1, 2);
        if (choice == 1)
            return true;
        else
            return false;
    }

    public void update() {
        if (this.c == null) {
            return;
        }
        try {
            GameEvent ge = c.getEvent();
            if (ge.isUpdate())
                updateState();
            if (ge.isDisconnection())
                showDisconnectionNotification(ge.getInfo());
            if (ge.isTimeout())
                showTimeoutNotification(ge.getInfo());
            c.ack();
        } catch (IOException e) {
            log.warning("Connection error " + e.toString());
            conn.setConnected(false);
        }
    }

    protected abstract void showDisconnectionNotification(String info);
    protected abstract void showTimeoutNotification(String info);

    private void updateState() throws IOException {
        pullUpdates();

        switch (this.gameBoard.getCurrentState()) {
            case PLAYERREGISTRATION:
                playerRegistration();
                break;
            case SETUP:
                setup();
                break;
            case PRIVATEOBJSETUP:
                privateObjSetup();
                break;
            case GRIDSETUP:
                gridSetup();
                break;
            case TOOLSETUP:
                toolSetup();
                break;
            case COMMONOBJSETUP:
                commonObjSetup();
                break;
            case TURNHANDLER:
                turnHandler();
                break;
            case PLAYERTURNBEGIN:
                playerTurnBegin();
                break;
            case DICEACTION:
                diceAction();
                break;
            case TOOLACTION:
                toolAction();
                break;
            case PLAYERTURNEND:
                playerTurnEnd();
                break;
            case GAMEFINISH:
                gameFinish();
                break;
            default:
                break;
        }
    }

    public void setController(Controller c) {
            this.c = c;
            controllerSet.countDown();
    }

    protected abstract void gameFinish() throws IOException;

    protected abstract void playerTurnEnd() throws IOException;

    protected abstract void toolAction() throws IOException;

    protected abstract void diceAction() throws IOException;

    protected abstract void playerTurnBegin() throws IOException;

    protected abstract void turnHandler() throws IOException;

    protected abstract void commonObjSetup() throws IOException;

    protected abstract void toolSetup() throws IOException;

    protected abstract void gridSetup() throws IOException;

    protected abstract void privateObjSetup() throws IOException;

    protected abstract void setup() throws IOException;

    protected abstract void playerRegistration() throws IOException;

    public void pullUpdates() throws GameException, IOException {
        this.gameBoard = c.getGameBoard();
    }

}
