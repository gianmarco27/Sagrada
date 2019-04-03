package ingsw.Server.Objective;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Grid.Grid;

public abstract class PrivateObjective extends GenericObjective {

    int type = 0;
    DiceColor privateColor;

    @Override
    public String toString() {
        return "Private" + super.toString() + " | SCORE #";
    }

    /**
     * Calculate the score of a given private objective when applied to a certain grid.
     *
     * @param grid on which we calculate the score.
     * @return scoreHolder which is the points scored by this obj.
     */
    @Override
    public int getScore(Grid grid) {
        int scoreHolder = 0;
        for (int idx_x = 0; idx_x < Grid.COLUMNS; idx_x++) {
            for (int idx_y = 0; idx_y < Grid.ROWS; idx_y++) {
                if (!grid.isCellEmpty(idx_x, idx_y)) {
                    Dice currDie = grid.getDie(idx_x, idx_y);
                    scoreHolder += (currDie.getColor() == privateColor) ? currDie.getValue() : 0;
                }
            }
        }
        return scoreHolder;
    }
}
