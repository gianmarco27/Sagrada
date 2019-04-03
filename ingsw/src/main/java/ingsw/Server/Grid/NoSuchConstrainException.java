package ingsw.Server.Grid;


import java.io.Serializable;

public class NoSuchConstrainException extends Exception implements Serializable {

    public NoSuchConstrainException() {
        super("This cell does not have this constrain");
    }
}