package ingsw.Server.Actions;

import java.io.Serializable;

/**
 * Class that handle all the things related to dice placements
 */
public class DiceActionParameter implements Serializable {
    int picked_idx;
    int target_x, target_y;


    public DiceActionParameter() {
    }

    /**
     * Setter for selecting the dice
     */
    public void chooseDie(int picked_idx) {
        this.picked_idx = picked_idx;
    }

    public void choseTarget(int x, int y) {
        this.target_x = x;
        this.target_y = y;
    }

    /**
     * @return the selected index contained in the DiceActionParameter object
     */
    public int getPicked_idx() {
        return picked_idx;
    }

    /**
     * @return the x coordinate contained in the DiceActionParameter object
     */
    public int getTarget_x() {
        return target_x;
    }

    /**
     * @return the y coordinate contained in the DiceActionParameter object
     */
    public int getTarget_y() {
        return target_y;
    }
}
