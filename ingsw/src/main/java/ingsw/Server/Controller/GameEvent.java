package ingsw.Server.Controller;

import java.io.Serializable;

/**
 * Game Event containing all the relative getters and setters
 */
public class GameEvent implements Serializable {
    private boolean update = false;
    private boolean disconnection = false;
    private boolean timeout = false;
    private final String info;
    public GameEvent(String m){
        info = m;
    }
    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isDisconnection() {
        return disconnection;
    }

    public void setDisconnection(boolean disconnection) {
        this.disconnection = disconnection;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    public String getInfo() {
        return info;
    }
}
