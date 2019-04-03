package ingsw.Server.Controller;

import ingsw.Settings;

import java.io.Serializable;
import java.util.logging.Logger;

public class RMIPing implements Serializable {
    private static final Logger log = Logger.getLogger( RMIPing.class.getName() );
    private Object pingLock;
    private boolean pinged;

    public RMIPing(){
        pinged = false;
        pingLock = new Object();
    }

    /**
     * Loop that keeps waiting for the player to ping and waits for the ping back if it times out returns
     */
    public void pingServerLoop() {
        synchronized(pingLock) {
            while (true) {
                try {
                    pingLock.wait(Settings.PING_TIMEOUT);
                    if(!pinged){
                        return;
                    }
                    //log.info("PING RECEIVED!");
                    pinged = false;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warning("Error waiting inside pingServer error : " + e.toString());
                }
            }
        }
    }

    /**
     * Called by the Client to Ping the server
     */
    public void ping(){
        synchronized (pingLock){
            pinged = true;
            pingLock.notifyAll();
        }
    }
}
