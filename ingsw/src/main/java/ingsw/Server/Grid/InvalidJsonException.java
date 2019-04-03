package ingsw.Server.Grid;


import java.io.Serializable;

public class InvalidJsonException extends Exception implements Serializable {

    public InvalidJsonException() {
        super("The json restrinctions contain some invalid cells");
    }
}