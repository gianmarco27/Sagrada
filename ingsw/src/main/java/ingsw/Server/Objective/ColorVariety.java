package ingsw.Server.Objective;

import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Grid.Grid;

import java.util.Collections;
import java.util.EnumMap;

public class ColorVariety extends PublicObjective {

    public ColorVariety() {
        this.description = "Sets of one of each color anywhere.";
        this.objValue = 4;
        this.cardName = "Color Variety";
        this.cardId = 10;
    }

    /**
     * Calculate the score by finding the sets of each color in the
     * @param grid
     */
    public int getScore(Grid grid) {
        EnumMap<DiceColor, Integer> allColor = new EnumMap<>(DiceColor.class);
        for (int idx_x = 0; idx_x < Grid.COLUMNS; idx_x++) {
            for (int idx_y = 0; idx_y < Grid.ROWS; idx_y++) {
                if (!grid.isCellEmpty(idx_x, idx_y)) {
                    DiceColor currColor = grid.getDie(idx_x, idx_y).getColor();
                    if (!allColor.containsKey(currColor)) { // Initialization for not missing color
                        allColor.put(currColor, 0);
                    }
                    allColor.compute(currColor, (k, v) -> v + 1);
                }
            }
        }
        return allColor.isEmpty() ? 0 : Collections.min(allColor.values()) * this.objValue; // Special case for empty List
    }
}
