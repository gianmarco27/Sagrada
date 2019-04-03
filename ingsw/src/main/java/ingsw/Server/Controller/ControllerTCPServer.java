package ingsw.Server.Controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ingsw.Server.Actions.DiceActionParameter;
import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Objective.PrivateObjective;
import ingsw.Server.Player;
import ingsw.Server.Utility.GameException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static ingsw.Server.Utility.CopyObjects.*;

public class ControllerTCPServer {
    private static final Logger log = Logger.getLogger( ControllerTCPServer.class.getName() );
    Controller c;
    GameFlowHandler gfh;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    ObjectInputStream eventsInStream;
    ObjectOutputStream eventsOutStream;
    String username;
    GameType gameType;

    /**
     * Constructor initializing the server side controller and opening the sockets, it updates the events
     * @throws GameException on initialization of the controller error
     */
    public ControllerTCPServer(GameFlowHandler g, Socket s1,Socket s2) throws IOException, ClassNotFoundException {
        gfh = g;
        outStream = new ObjectOutputStream(s1.getOutputStream());
        outStream.flush();
        inStream = new ObjectInputStream(s1.getInputStream());

        this.username = (String)inStream.readObject();
        this.gameType = (GameType)inStream.readObject();
        String error = "ERROR|";
        try {
            this.c = initController();
        } catch (GameException ge){
            error = error.concat(ge.getMessage());
        }
        if (this.c == null){
            outStream.writeObject(error);
            s1.close();
            s2.close();
        } else {
            outStream.writeObject("SUCCESS");
        }
        eventsOutStream = new ObjectOutputStream(s2.getOutputStream());
        eventsInStream = new ObjectInputStream(s2.getInputStream());
    }

    /**
     * returns a new controller with the given parameters set
     */
    private Controller initController() throws GameException, RemoteException {
        return new ControllerImpl(gfh,username,gameType);
    }

    /**
     * Main method of the server controller, receiving the Json objects from the client containing the method to execute
     * and replies to the client with the results of the method call
     */
    public void runServer() throws IOException, ClassNotFoundException {
        boolean runserver = true;
        new Thread(() -> runEventQueueServer()).start();
        while(runserver){
            String str = (String)inStream.readObject();
            //log.info(str);
            JSONObject json = new JSONObject(str);
            Method method = null;
            Object returnValue = null;
            GameException exception = null;
            try {
                method = this.getClass().getMethod(json.getString("functionName"),List.class);

            } catch (SecurityException | NoSuchMethodException e) {
                log.warning("Exception in getting the method : "+e.toString());
            }

            JSONArray array = (JSONArray) json.get("params");
            CopyOnWriteArrayList<Serializable> parameters = new CopyOnWriteArrayList<>();
            for (Object o : array.toList()){
                parameters.add(deserializeFromB64((String)o));
            }

            try {
                //log.info("REFLECTION calling : "+method.getName());
                if(method != null) {
                    returnValue = method.invoke(this, parameters);
                } else {
                    throw new NullPointerException();
                }
            } catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
                exception = new GameException();
            } catch (InvocationTargetException e) {
                try{
                    throw e.getCause();
                } catch (GameException ge){
                    exception = ge;
                } catch (Throwable ex) {
                    exception = new GameException();
                    log.warning("ERROR : "+ ex.toString());
                }
            }
            JSONObject answer = new JSONObject()
                    .put("result", serializeToB64((Serializable) returnValue))
                    .put("exception",serializeToB64(exception));
            String toSend = answer.toString();
            //log.info("result of function "+method.getName()+"\nreturn value : "+returnValue+"\nretunValue = "+toSend);
            outStream.writeObject(toSend);
            outStream.flush();
        }
    }

    /**
     * Runs the asynchronous server EventQueue available on the second socket communicating to the player the controller events
     */
    private void runEventQueueServer(){
        boolean runningEventQueue = true;
        try {
            while(runningEventQueue){
                eventsInStream.readObject();//We don't actually care about the request
                eventsOutStream.writeObject(c.getEvent());
            }
        } catch (IOException e) {
            try {
                log.info("Disconnecting player "+c.getPlayer().getName());
                gfh.disconnectPlayer(c.getPlayer(),false);
            } catch (IOException r){
                log.warning(Arrays.toString(r.getStackTrace()));
            }
        } catch (ClassNotFoundException e) {
            log.warning(Arrays.toString(e.getStackTrace()));
        }
    }


    /**
     * returns the updated controller gameboard by retrieving it from the controller
     */
    public GameBoard getGameBoard(List<Serializable> p) throws IOException, GameException{
        if (p.size() == 0) {
            return c.getGameBoard();
        }
        return null;
    }

    /**
     * returns the player the controller is binded to by retrieving it from the controller
     */
    public Player getPlayer(List<Serializable> p) throws IOException, GameException {
        if (p.size() == 0) {
            return c.getPlayer();
        }
        return null;
    }

    /**
     * calls the chooseGrid method on the controller
     */
    public boolean chooseGrid(List<Serializable> p) throws IOException, GameException {
        if (p.size() == 1) {
            return c.chooseGrid((Grid) p.get(0));
        }
        return false;
    }

    /**
     * calls the registerPlayer method on the controller
     */
    public boolean registerPlayer(List<Serializable> p) throws IOException, GameException {
        if (p.size() == 2) {
            return c.registerPlayer((String) p.get(0),(GameType) p.get(1));
        }
        return false;
    }

    /**
     * calls the ack method on the controller
     */
    public void ack(List<Serializable> p) throws IOException, GameException {
        if (p.size() == 0) {
            c.ack();
        }
    }

    /**
     * returns the player's private objective(s) retrieved from the controller
     */
    public List<PrivateObjective> getAllPrivateObjective(List<Serializable> p) throws IOException, GameException {
        if (p.size() == 0) {
            return c.getAllPrivateObjective();
        }
        return null;
    }

    /**
     * calls the turnBeginChoice method on the controller
     */
    public void sendTurnBeginChoice(List<Serializable> p) throws IOException, GameException {
        if (p.size() == 1) {
            c.sendTurnBeginChoice((int)p.get(0));
        }
    }

    /**
     * calls the chooseAndPlaceDie method on the controller
     */
    public boolean chooseAndPlaceDie(List<Serializable> p) throws IOException, GameException {
        if (p.size() == 1){
            return c.chooseAndPlaceDie((DiceActionParameter) p.get(0));
        }
        return false;
    }

    /**
     * calls the useTap method on the controller
     */
    public boolean useTap(List<Serializable> p) throws IOException, GameException {
        if (p.size() == 1){
            return c.useTap((ToolActionParameter) p.get(0));
        }
        return false;
    }

    /**
     * calls the back method on the controller
     */
    public boolean back(List<Serializable> p) throws IOException, GameException {
        if (p.size() == 0){
            return c.back();
        }
        return false;
    }

    /**
     * returns the opponents grids after retrieving them from  the controller
     */
    public Map<Player,Grid> getOthersGrids(List<Serializable> p) throws IOException, GameException {
        if (p.size() == 0){
            return c.getOthersGrids();
        }
        return null;
    }
}
