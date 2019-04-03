package ingsw.Server.Dice;

import ingsw.Server.Utility.GameException;
import org.fusesource.jansi.AnsiConsole;

import java.io.Serializable;
import java.util.Random;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Six-faced Dice object used in game has a specific color and value
 */
public class Dice implements Serializable {
    private int value; // Shade
    private DiceColor color; // Color

    public static final int MAX_VALUE = 6;
    public static final int MIN_VALUE = 1;
    /**
     * Dice constructor with set color and randomly generated value
     *
     * @param color
     */
    public Dice(DiceColor color) {
        this.value = new Random().nextInt(MAX_VALUE) + 1;
        this.color = color;
    }

    /**
     * Dice Constructor with set value and color
     *
     * @param value
     * @param color
     */
    public Dice(int value, DiceColor color) {
        if (value < MIN_VALUE || value > MAX_VALUE ) {
            throw new InvalidDiceValueException(value);
        } else {
            this.value = value;
         }
            this.color = color;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean colored) {
        if (!colored) {
            return "[" + value + color.name().charAt(0) + "]"; // [3R];
        } else {
            System.setProperty("jansi.passthrough", "true");
            AnsiConsole.systemInstall();
            return ansi().bg(color.getAnsiColor()).fg(BLACK).a("[" + value + color.name().charAt(0) + "]").reset().toString();
        }
    }

    /**
     * @param toSet value to set the dice to
     */
    public void setValue(int toSet) {
        if(toSet < MIN_VALUE || toSet > MAX_VALUE) {
            throw new GameException("Value out of the range of a dice.");
        }
        this.value = toSet;
    }
    /**
     * @return the value of the this dice
     */
    public int getValue() {
        return value;
    }

    /**
     * @return the color of this dice
     */
    public DiceColor getColor() {
        return color;
    }

    /**
     * Increases the value of the Dice by 1
     *
     * @throws InvalidDiceValueException if the dice value is already at its max
     */
    public void increase() throws InvalidDiceValueException {
        if (this.value == MAX_VALUE) {
            throw new InvalidDiceValueException(7);
        } else {
            this.value += MIN_VALUE;
        }
    }

    /**
     * Decreases the value of the Dice by 1
     *
     * @throws InvalidDiceValueException if the dice value is already at its min
     */
    public void decrease() throws InvalidDiceValueException {
        if (this.value == 1) {
            throw new InvalidDiceValueException(0);
        } else {
            this.value -= 1;
        }
    }

    /**
     * Changes the Dice value to the opposite face value
     */
    public void oppositeSide() {
        this.value = 7 - this.value;
    }

    /**
     * Changes the Dice value to a randomly obtained number in the range 1-6
     */
    public void rerollDice() {
        this.value = new Random().nextInt(6)+1;
    }
}