package ingsw.Server.Controller;

import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Utility.GameException;
import ingsw.Settings;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Actual implementation of controllerFactory
 */
public class ControllerFactoryImpl extends UnicastRemoteObject implements ControllerFactory {
    private static final Logger log = Logger.getLogger( ControllerFactoryImpl.class.getName() );
    transient GameFlowHandler gfh;

    /**
     * Class constructor that spawns a thread for the TCPFactory
     * @param gfh GameFlowHandler that gets binded to the controller factory
     */
    public ControllerFactoryImpl(GameFlowHandler gfh) throws RemoteException {
        this.gfh = gfh;
        new Thread(() -> {
            try {
                exposeTCPFactory();
            } catch (IOException e) {
                log.warning("Error starting the TCP controller factory : "+e.toString());
                System.exit(1);
            }
        }).start();
    }

    /**
     * @param name the player name
     * @param singlePlayer the type of the current game starting
     * @return
     * @throws RemoteException
     * @throws GameException
     */
    public Controller createController(String name,GameType singlePlayer) throws RemoteException, GameException {
        log.info("Creating new controller");
        ControllerImpl c = new ControllerImpl(gfh, name,singlePlayer);
        return c;
    }

    /**
     * Method that starts the 2 server sockets waiting dor players to connect and runs the handler of each connection
     */
    private void exposeTCPFactory() throws IOException {
        boolean running = true;
        ServerSocket s1 = new ServerSocket(Settings.TCP_PORT);
        ServerSocket s2 = new ServerSocket(Settings.TCP_PORT2);
        try {
            while (running) {
                try {
                    Socket conn1 = s1.accept();
                    Socket conn2 = s2.accept();
                    Runnable handler = new TCPConnectionHandler(conn1, conn2);
                    new Thread(handler).start();
                } catch (IOException e) {
                    log.warning(e.toString());
                    continue;
                }
            }
        } finally {
            try{
                s1.close();
            } finally {
                s2.close();
            }
        }
    }

    /**
     * This Class handles the TCP connection server side
     */
    public class TCPConnectionHandler implements Runnable {
        Socket conn1, conn2;
        public TCPConnectionHandler(Socket conn1, Socket conn2) {
            this.conn1 = conn1;
            this.conn2 = conn2;
        }

        public void run() {
            ControllerTCPServer contrl = null;
            try {
                contrl = new ControllerTCPServer(gfh, conn1,conn2);
            } catch (IOException | ClassNotFoundException e) {
                log.warning("Error creating the TCP server controller : " + e.toString());
            }
            if (contrl == null) {
                return;
            }
            try {
                contrl.runServer();
            } catch (IOException | ClassNotFoundException e) {
                try {
                    log.warning("Error running the ControllerTCPServer, handling disconnection : " + e.toString());
                    gfh.disconnectPlayer(contrl.c.getPlayer(),false);
                } catch (IOException e1) {
                    log.warning(Arrays.toString(e1.getStackTrace()));
                    System.exit(-1);
                }
            }catch (NullPointerException e){
                return;
            }
        }
    }

}
