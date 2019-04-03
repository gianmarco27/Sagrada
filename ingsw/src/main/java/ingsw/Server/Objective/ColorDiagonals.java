package ingsw.Server.Objective;

import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Grid.Grid;

public class ColorDiagonals extends PublicObjective {

    private boolean[][] seen;
    private Grid currGrid;

    public ColorDiagonals() {
        this.description = "Count of diagonally adjacent same color dice.";
        this.objValue = 1;
        this.cardName = "Color Diagonals";
        this.cardId = 9;
    }

    /**
     * DFS Recursive solution to find all diagonally adjacent same color dice from
     * a given position on the grid. It travels deep-down until the edge or when
     * it finds a die which does not meet the requirement.
     */
    private int recursiveCheck(DiceColor currColor, int idx_x, int idx_y) {
        if (!currGrid.areCoordinatesValids(idx_x, idx_y) ||
                seen[idx_x][idx_y] ||
                this.currGrid.isCellEmpty(idx_x, idx_y) ||
                this.currGrid.getDie(idx_x, idx_y).getColor() != currColor) {
            return 0;
        } else {
            this.seen[idx_x][idx_y] = true;
            return 1 +
                    this.recursiveCheck(currColor, idx_x + 1, idx_y + 1) +
                    this.recursiveCheck(currColor, idx_x - 1, idx_y - 1) +
                    this.recursiveCheck(currColor, idx_x + 1, idx_y - 1) +
                    this.recursiveCheck(currColor, idx_x - 1, idx_y + 1);
        }
    }

    /**
     * Calculates the score of this card by iterating all possible cells, keeping
     * track of already visited ones through a boolean 2D mask
     */
    public int getScore(Grid grid) {
        this.seen = new boolean[Grid.COLUMNS][Grid.ROWS];
        this.currGrid = grid;
        int currScore = 0;
        for (int idx_y = 0; idx_y < Grid.ROWS; idx_y++) { // Iterates the rows
            for (int idx_x = 0; idx_x < Grid.COLUMNS; idx_x++) { // Iterates the columns
                if (!grid.isCellEmpty(idx_x, idx_y)) {
                    DiceColor currColor = grid.getDie(idx_x, idx_y).getColor();
                    int partialScore = recursiveCheck(currColor, idx_x, idx_y) * this.objValue;
                    currScore += (partialScore > 1) ? partialScore : 0;
                }
            }
        }
        return currScore;
    }
}
