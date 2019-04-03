package ingsw.Server.Controller;

import ingsw.Server.Actions.DiceActionParameter;
import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.GameBoard;
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
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import static ingsw.Server.Utility.CopyObjects.deserializeFromB64;
import static ingsw.Server.Utility.CopyObjects.serializeToB64;

/**
 * Client implementation of the TCP controller
 */
public class ControllerTCPClient implements Controller {
    private static final Logger log = Logger.getLogger( ControllerTCPClient.class.getName() );
    Socket s1;
    Socket s2;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    ObjectOutputStream eventOutStream;
    ObjectInputStream eventInStream;
    int port;

    /**
     * The constructor opens the sockets with the server and sets the events depending on the server sockets response
     * @throws GameException on server error message
     */
    public ControllerTCPClient(String url,int port,int port2,String username,GameType gameType) throws IOException, ClassNotFoundException, GameException {
        s1 = new Socket(url,port);
        s2 = new Socket(url,port2);

        outStream = new ObjectOutputStream(s1.getOutputStream());
        outStream.flush();
        inStream = new ObjectInputStream(s1.getInputStream());

        outStream.writeObject(username);
        outStream.writeObject(gameType);
        outStream.flush();
        String result = (String)inStream.readObject();
        if(result.startsWith("ERROR|")){
            throw new GameException(result.replace("ERROR|",""));
        }
        eventOutStream = new ObjectOutputStream(s2.getOutputStream());
        eventInStream = new ObjectInputStream(s2.getInputStream());
    }

    /**
     * Implementation of RMI through TCP creates a json containing the method to call recieved through reflection and its parameters
     */
    private synchronized Serializable remoteFunctionCall(String functionName,List<Serializable> params) throws GameException, IOException {
        Serializable returnValue = null;
        GameException exception = null;
        try {
            // Call the remote function
            JSONObject request = new JSONObject()
                    .put("functionName", functionName);

            JSONArray array = new JSONArray();
            for (Serializable o : params) {
                array.put(serializeToB64(o));
            }

            request.put("params", array);
            String toSend = request.toString();

            outStream.writeObject(toSend);
            outStream.flush();
            String strAnswer = (String) inStream.readObject();
            JSONObject answer = new JSONObject(strAnswer);


            returnValue = deserializeFromB64(answer.get("result").toString());
            exception = (GameException) deserializeFromB64(answer.get("exception").toString());

        } catch (IOException e) {
            log.warning("Network error : "+e.toString());
            throw e;
        } catch (ClassNotFoundException e) {
            log.warning("Error using reflection to get the method : "+e.toString());
        }
        if (exception != null)
            throw exception;
        return returnValue;
    }

    /**
     * Remote Call used to retrieve the Gameboard
     */
    public GameBoard getGameBoard() throws GameException, IOException {
        return (GameBoard)remoteFunctionCall(Thread.currentThread().getStackTrace()[1].getMethodName(), new CopyOnWriteArrayList<>());
    }

    /**
     * Remote Call to the register player Method
     */
    @Override
    public boolean registerPlayer(String name,GameType singlePlayer) throws GameException, IOException {
        return (Boolean) remoteFunctionCall(Thread.currentThread().getStackTrace()[1].getMethodName(), Arrays.asList(name,singlePlayer));
    }

    /**
     * Remote Call used to retrieve the Player the controller is binded to
     */
    public Player getPlayer() throws GameException, IOException {
        return (Player)remoteFunctionCall(Thread.currentThread().getStackTrace()[1].getMethodName(), new CopyOnWriteArrayList<Serializable>());
    }

    /**
     * Remote Call to the Grid Choice Method
     */
    public boolean chooseGrid(Grid possibleGrid) throws GameException, IOException {
        return (Boolean) remoteFunctionCall(Thread.currentThread().getStackTrace()[1].getMethodName(), Collections.singletonList(possibleGrid));
    }

    /**
     * Method used to retrieve the GameEvents, asynchronous on the second socket
     */
    @Override
    public GameEvent getEvent() throws IOException {
        try {
            eventOutStream.writeObject(new GameEvent(""));
            return (GameEvent) eventInStream.readObject();
        } catch (IOException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            log.warning(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }



    /**
     * Remote Call to the ack Method
     */
    public void ack() throws GameException, IOException {
        remoteFunctionCall(Thread.currentThread().getStackTrace()[1].getMethodName(), new CopyOnWriteArrayList<Serializable>());
    }

    /**
     * Remote Call to retrieve the player private objective(s)
     */
    public List<PrivateObjective> getAllPrivateObjective() throws GameException, IOException {
        return (List<PrivateObjective>) remoteFunctionCall(Thread.currentThread().getStackTrace()[1].getMethodName(), new CopyOnWriteArrayList<Serializable>());
    }

    /**
     * Remote Call to the TurnBeginChoice Method
     */
    @Override
    public void sendTurnBeginChoice(int c) throws GameException, IOException {
        remoteFunctionCall(Thread.currentThread().getStackTrace()[1].getMethodName(), Collections.singletonList(c));
    }

    /**
     * Remote Call to the placingDie Method
     */
    @Override
    public boolean chooseAndPlaceDie(DiceActionParameter p) throws GameException, IOException {
        return (boolean) remoteFunctionCall(Thread.currentThread().getStackTrace()[1].getMethodName(), Collections.singletonList(p));
    }

    /**
     * Remote Call to the Tool Using Method
     */
    @Override
    public boolean useTap(ToolActionParameter tap) throws GameException, IOException {
        return (boolean) remoteFunctionCall(Thread.currentThread().getStackTrace()[1].getMethodName(), Collections.singletonList(tap));
    }

    /**
     * Remote Call to the back Method
     */
    public boolean back() throws GameException, IOException {
        return (boolean) remoteFunctionCall(Thread.currentThread().getStackTrace()[1].getMethodName(),new CopyOnWriteArrayList<Serializable>());
    }

    /**
     * Remote Call to retrieve the opponents Grids
     */
    public Map<Player,Grid> getOthersGrids() throws GameException, IOException {
        return (Map<Player,Grid>) remoteFunctionCall(Thread.currentThread().getStackTrace()[1].getMethodName(),new CopyOnWriteArrayList<Serializable>());
    }

    /**
     * Remote Call to the Ping Method
     */
    @Override
    public void ping() throws GameException{
        return;
    }
}
