package ingsw.Server.Objective;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Grid.Grid;

import java.util.HashSet;
import java.util.Set;

public class ColumnShadeVariety extends PublicObjective {

    public ColumnShadeVariety() {
        this.description = "Columns with no repeated values.";
        this.objValue = 4;
        this.cardName = "Column Shade Variety";
        this.cardId = 4;
    }

    /**
     * Calculate the score based on how many columns don't have repeated values in the
     * @param grid
     */
    public int getScore(Grid grid) {
        int scoreHolder = 0;
        for (int idx_x = 0; idx_x < Grid.COLUMNS; idx_x++) { // Iterate the columns
            Set<Integer> seen = new HashSet<>();
            boolean unique = true;
            for (int idx_y = 0; idx_y < Grid.ROWS; idx_y++) { // Iterate the rows
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
