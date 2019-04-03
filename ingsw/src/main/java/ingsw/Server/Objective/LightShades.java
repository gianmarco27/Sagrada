package ingsw.Server.Objective;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Grid.Grid;

public class LightShades extends PublicObjective {

    public LightShades() {
        this.description = "Sets of 1 & 2 values anywhere.";
        this.objValue = 2;
        this.cardName = "Light Shades";
        this.cardId = 5;
    }

    /**
     * Calculate the score by finding the sets of 1&2 in the
     * @param grid
     */
    public int getScore(Grid grid) {
        int nDice1 = 0, nDice2 = 0;
        for (int idx_x = 0; idx_x < Grid.COLUMNS; idx_x++) {
            for (int idx_y = 0; idx_y < Grid.ROWS; idx_y++) {
                if (!grid.isCellEmpty(idx_x, idx_y)) {
                    Dice currDie = grid.getDie(idx_x, idx_y);
                    nDice1 += (currDie.getValue() == 1) ? 1 : 0;
                    nDice2 += (currDie.getValue() == 2) ? 1 : 0;
                }
            }
        }
    return Math.min(nDice1, nDice2) * this.objValue;
    }
}
