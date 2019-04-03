package ingsw.Server.Grid;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;

import java.io.Serializable;

public class GridCell implements Serializable {

    protected Dice die;

    /**
     * @return get the die contained in this cell
     */
    public Dice getDie() {
        return this.die;
    }

    /**
     * @param die set this GridCell to a specific die
     */
    public void setDie(Dice die) {
            this.die = die;
    }

    /**
     * remove and
     * @return the current die
     */
    public Dice removeDie() {
        Dice d = this.die;
        this.die = null;
        return d;
    }

    /**
     * @return check if the cell is empty
     */
    public boolean isEmpty() {
        return (this.die == null);
    }

    /**
     * @param die
     * @return if a certain die would violate grid own restriction
     */
    public boolean isPlaceable(Dice die) {
        return true;
    }

    /**
     * @return the cell constraint related to the shade
     * @throws NoSuchConstrainException
     */
    public int getShadeConstrain() throws NoSuchConstrainException{
        throw new NoSuchConstrainException();
    }

    /**
     * @return the cell constraint related to the color
     * @throws NoSuchConstrainException
     */
    public DiceColor getColorConstrain() throws NoSuchConstrainException{
        throw new NoSuchConstrainException();
    }

    public boolean hasShadeConstrain() {
        return false;
    }
    public boolean hasColorConstrain() {
        return false;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean colored) {
        if (this.isEmpty()) {
            return "    ";
        } else {
            return this.die.toString(colored);
        }
    }
}
