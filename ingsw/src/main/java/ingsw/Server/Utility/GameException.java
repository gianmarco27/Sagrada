package ingsw.Server.Utility;

import java.io.Serializable;

/**
 * GameException is a custom exception used to communicate a special behaviors that need to be accounted for
 * It's mainly used when a placements/usage of a tool would violate some game logic condition
 * and we display the reasoning back to the client.
 */
public class GameException extends RuntimeException implements Serializable {

    public static final String genericError = "Generic game error.";
    boolean callBack;
    /**
     * Generic constructor for the exception
     */
    public GameException() {
        super(genericError);
    }

    /**
     * Specific constructor that specify the type of error, as a String
     */
    public GameException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructor that setup an exception with a given error message and eventually a callback
     */
    public GameException(String errorMessage, boolean callBack) {
        super(errorMessage);
        this.callBack = callBack;
    }

    /**
     * @return the value of callBack
     */
    public boolean isCallBack() {
        return callBack;
    }
}