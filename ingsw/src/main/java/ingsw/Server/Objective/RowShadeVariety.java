package ingsw.Server.Objective;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Grid.Grid;

import java.util.HashSet;
import java.util.Set;

public class RowShadeVariety extends PublicObjective {

    public RowShadeVariety() {
        this.description = "Rows with no repeated values.";
        this.objValue = 5;
        this.cardName = "Row Shade Variety";
        this.cardId = 3;
    }

    /**
     * Calculate the score based on how many rows don't have repeated values in the
     * @param grid
     */
    public int getScore(Grid grid) {
        int scoreHolder = 0;
        for (int idx_y = 0; idx_y < Grid.ROWS; idx_y++) { // Iterate the rows
            Set<Integer> seen = new HashSet<>();
            boolean unique = true;
            for (int idx_x = 0; idx_x < Grid.COLUMNS; idx_x++) { // Iterate the columns
                if (grid.isCellEmpty(idx_x, idx_y)) {
                    unique = false;
                } else {
                    Dice currDice = grid.getDie(idx_x, idx_y);
                    if (seen.contains(currDice.getValue())) {
                        unique = false;
                    } else {
                        seen.add(currDice.getValue());
                    }
                }
            }
            scoreHolder += (unique) ? this.objValue : 0;
        }
        return scoreHolder;
    }
}
