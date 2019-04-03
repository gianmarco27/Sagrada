package ingsw.Server.Grid;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Utility.GameException;
import org.fusesource.jansi.AnsiConsole;

import java.io.Serializable;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * This Class is specific for Shade Constrained GridCells
 */
public class GridCellShade extends GridCell implements Serializable {
    private int shade;
    public GridCellShade(int shade) {
        this.shade = shade;
    }


    public boolean isPlaceable(Dice die) {
        if (die.getValue() == this.shade) {
            return true;
        }
        throw new GameException("The placement doesn't respect shade constraints");
    }

    public int getShadeConstrain() {
        return this.shade;
    }

    public DiceColor getColorConstrain() throws NoSuchConstrainException {
        throw new NoSuchConstrainException();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean colored) {
        if (this.isEmpty()) {
            if (!colored) {
                return " C" + this.shade + " ";
            } else {
                System.setProperty("jansi.passthrough", "true");
                AnsiConsole.systemInstall();
                return ansi().bg(BLACK).a("  " + this.shade + " ").reset().toString();
            }
        } else {
            return this.die.toString(colored);
        }
    }

    @Override
    public boolean hasShadeConstrain() {
        return true;
    }

    @Override
    public boolean hasColorConstrain() {
        return false;
    }
}
