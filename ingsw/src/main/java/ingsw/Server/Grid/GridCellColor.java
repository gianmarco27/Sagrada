package ingsw.Server.Grid;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Utility.GameException;
import org.fusesource.jansi.AnsiConsole;

import java.io.Serializable;

import static org.fusesource.jansi.Ansi.Color.BLACK;
import static org.fusesource.jansi.Ansi.Color.DEFAULT;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * This class is specific for Color constrained GridCells
 */
public class GridCellColor extends GridCell implements Serializable {
    private DiceColor color;
    public GridCellColor(DiceColor color) {
        this.color = color;
    }


    public boolean isPlaceable(Dice die) {
        if (die.getColor() == this.color) {
            return true;
        }
        throw new GameException("The placement doesn't respect color constraints");
    }

    public int getShadeConstrain() throws NoSuchConstrainException {
        throw new NoSuchConstrainException();
    }

    public DiceColor getColorConstrain() throws NoSuchConstrainException {
        return this.color;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean colored) {
        if (this.isEmpty() ) {
            if (!colored) {
                return " C" + this.color.name().charAt(0) + " ";
            } else {
                System.setProperty("jansi.passthrough", "true");
                AnsiConsole.systemInstall();
                return ansi().bg(color.getAnsiColor()).a("    ").reset().toString();
            }
        } else {
            return this.die.toString(colored);
        }
    }

    @Override
    public boolean hasShadeConstrain() {
        return false;
    }
    @Override
    public boolean hasColorConstrain() {
        return true;
    }
}
