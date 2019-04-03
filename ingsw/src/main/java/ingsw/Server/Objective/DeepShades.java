package ingsw.Server.Objective;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Grid.Grid;

public class DeepShades extends PublicObjective {

    public DeepShades() {
        this.description = "Sets of 5 & 6 values anywhere.";
        this.objValue = 2;
        this.cardName = "Deep Shades";
        this.cardId = 7;
    }

    /**
     * Calculate the score by finding the sets of 5&6 in the
     * @param grid
     */
    public int getScore(Grid grid) {
        int nDice5 = 0, nDice6 = 0;
        for (int idx_x = 0; idx_x < Grid.COLUMNS; idx_x++) {
            for (int idx_y = 0; idx_y < Grid.ROWS; idx_y++) {
                if (!grid.isCellEmpty(idx_x, idx_y)) {
                    Dice currDie = grid.getDie(idx_x, idx_y);
                    nDice5 += (currDie.getValue() == 5) ? 1 : 0;
                    nDice6 += (currDie.getValue() == 6) ? 1 : 0;
                }
            }
        }
        return Math.min(nDice5, nDice6) * this.objValue;
    }
}
