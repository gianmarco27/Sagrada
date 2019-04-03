package ingsw.Server.Actions;

import ingsw.Server.Grid.Grid;

/**
 * Class that contains and handle the parameters related to the choice during the GridSetup
 */
public class SetupActionParameter {
    Grid[] choseFrom;
    int picked;

    public int getPickedGrid() {
        return picked;
    }

    public void setPickedGrid(int toSet) {
        picked = toSet;
    }

    public SetupActionParameter() {
        choseFrom = new Grid[4];
    }

    /**
     * Add a couple of grid to choseFrom
     * @return boolean explaining if the operation was successful
     */
    public boolean addGridCouple(Grid[] gridCouple) {
        if (getLength(gridCouple) != 2) {
            return false;
        } else {
            int offset = getLength(choseFrom);
            for (int idx = 0; idx < 2; idx++) {
                choseFrom[idx+offset] = gridCouple[idx];
            }
            return true;
        }
    }

    /**
     * Getter for the possible GridChoices
     */
    public Grid[] getGridChoice() {
        return choseFrom;
    }

    private static <T> int getLength(T[] arr) {
        int count = 0;
        for(T el : arr)
            if (el != null) {
                count++;
            }
        return count;
    }
}
