package ingsw.Server;

import java.io.Serializable;

public class NoMoreBagElementException extends Exception implements Serializable {

    /**
     * Generic constructor for the exception to throw when someone try to pull items out of an empty bag
     */
    public NoMoreBagElementException() {
        super("The Bag is Empty");
    }

    /**
     * Specific constructor that specify the type of bag, as a String, where the exception occurs
     * @param bagType type of bag where we are trying to take items from
     */
    public NoMoreBagElementException(String bagType) {
        super("This Bag of " + bagType + " is Empty");
    }
}