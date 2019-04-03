package ingsw.Server.Dice;


import java.io.Serializable;

public class InvalidDiceValueException extends RuntimeException implements Serializable {

    public InvalidDiceValueException() {}

    /**
     * @param value, custom error message when a Player try to work with a dice with an invalid value
     */
    public InvalidDiceValueException(int value) {
        super("We can't set the dice to value: " + value);
    }
}