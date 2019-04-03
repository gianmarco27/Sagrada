package ingsw.Server.Objective;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Grid.Grid;

import java.util.Arrays;
import java.util.Collections;

public class ShadeVariety extends PublicObjective {

    public ShadeVariety() {
        this.description = "Sets of one of each value anywhere";
        this.objValue = 5;
        this.cardName = "Shade Variety";
        this.cardId = 8;
    }

    /**
     * Calculate the score by finding the sets of each value in the
     * @param grid
     */
    public int getScore(Grid grid) {
        Integer[] allValues = {0, 0, 0, 0, 0, 0};
        for (int idx_x = 0; idx_x < Grid.COLUMNS; idx_x++) {
            for (int idx_y = 0; idx_y < Grid.ROWS; idx_y++) {
                if (!grid.isCellEmpty(idx_x, idx_y)) {
                    Dice currDie = grid.getDie(idx_x, idx_y);
                    allValues[currDie.getValue()-1] += 1;
                }
            }
        }
        return Collections.min(Arrays.asList(allValues)) * this.objValue;
    }
}
