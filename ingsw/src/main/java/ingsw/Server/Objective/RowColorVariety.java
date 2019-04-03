package ingsw.Server.Objective;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Grid.Grid;

import java.util.HashSet;
import java.util.Set;

public class RowColorVariety extends PublicObjective {

    public RowColorVariety() {
        this.description = "Rows with no repeated colors";
        this.objValue = 6;
        this.cardName = "Row Color Variety";
        this.cardId = 1;
    }

    /**
     * Calculate the score based on how many rows don't have repeated colors in the
     * @param grid
     */
    public int getScore(Grid grid) {
        int scoreHolder = 0;
        for (int idx_y = 0; idx_y < Grid.ROWS; idx_y++) { // Iterate the rows
            Set<DiceColor> seen = new HashSet<>();
            boolean unique = true;
            for (int idx_x = 0; idx_x < Grid.COLUMNS; idx_x++) { // Iterate the columns
                if (grid.isCellEmpty(idx_x, idx_y)) {
                    unique = false;
                } else {
                    Dice currDice = grid.getDie(idx_x, idx_y);
                    if (seen.contains(currDice.getColor())) {
                        unique = false;
                    } else {
                        seen.add(currDice.getColor());
                    }
                }
            }
            scoreHolder += (unique) ? this.objValue : 0;
        }
        return scoreHolder;
    }
}
