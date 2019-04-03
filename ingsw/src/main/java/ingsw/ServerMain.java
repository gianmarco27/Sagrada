package ingsw;

import ingsw.Server.Controller.ControllerFactoryImpl;
import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.Utility.LoggerUtils;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static void main( String[] args ) throws RemoteException {
        LoggerUtils.initLoggerSettings();
        System.setProperty( "java.rmi.server.hostname", Settings.SERVER_IP);
        System.out.println( "Hello World! This is Scloza, Schiavon & Poggi Project" );

        GameFlowHandler gfh = new GameFlowHandler();

        Registry registry = LocateRegistry.createRegistry(Settings.RMI_PORT);
        ControllerFactoryImpl controllerFactory = new ControllerFactoryImpl(gfh);
        registry.rebind("controllerFactory", controllerFactory);
        gfh.run();
        System.exit(0);//Forcing an exit
    }
}
