package ingsw.Server.Objective;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Grid.Grid;

public class MediumShades extends PublicObjective {

    public MediumShades() {
        this.description = "Sets of 3 & 4 values anywhere.";
        this.objValue = 2;
        this.cardName = "Medium Shades";
        this.cardId = 6;
    }

    /**
     * Calculate the score by finding the sets of 3&4 in the
     * @param grid
     */
    public int getScore(Grid grid) {
        int nDice3 = 0, nDice4 = 0;
        for (int idx_x = 0; idx_x < Grid.COLUMNS; idx_x++) {
            for (int idx_y = 0; idx_y < Grid.ROWS; idx_y++) {
                if (!grid.isCellEmpty(idx_x, idx_y)) {
                    Dice currDie = grid.getDie(idx_x, idx_y);
                    nDice3 += (currDie.getValue() == 3) ? 1 : 0;
                    nDice4 += (currDie.getValue() == 4) ? 1 : 0;
                }
            }
        }
        return Math.min(nDice3, nDice4) * this.objValue;
    }
}
