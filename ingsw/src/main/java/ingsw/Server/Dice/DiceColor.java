package ingsw.Server.Dice;


import javafx.scene.paint.Color;
import org.fusesource.jansi.Ansi;

import java.io.Serializable;

/**
 * Enum containing all the available Dice colours in game.
 * Also provides easy conversion
 * from DiceColor -> Jansi.Color for terminal colored support
 * from DiceColor -> JavaFx.Color for the GUI colors
 */
public enum DiceColor implements Serializable {
    RED, YELLOW, GREEN, BLUE, PURPLE;

    private Ansi.Color colorPrint;
    private Color displayPrint;

    static {
        RED.colorPrint = Ansi.Color.RED;
        YELLOW.colorPrint = Ansi.Color.YELLOW;
        GREEN.colorPrint = Ansi.Color.GREEN;
        BLUE.colorPrint = Ansi.Color.CYAN;
        PURPLE.colorPrint = Ansi.Color.MAGENTA;
    }

    static {
        RED.displayPrint = Color.rgb(192,57,43);
        YELLOW.displayPrint = Color.rgb(255,215,0);
        GREEN.displayPrint = Color.rgb(16,137,62);
        BLUE.displayPrint = Color.rgb(41,128,185);
        PURPLE.displayPrint = Color.rgb(142,68,173);
    }

    public Ansi.Color getAnsiColor() {
        return colorPrint;
    }

    public Color getDisplayColor() {
        return displayPrint;
    }
}
