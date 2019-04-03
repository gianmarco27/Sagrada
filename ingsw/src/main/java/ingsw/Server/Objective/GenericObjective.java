package ingsw.Server.Objective;

import ingsw.Server.Grid.Grid;

public abstract class GenericObjective implements Objective {

    int objValue;
    int cardId;
    String description;
    String cardName;

    @Override
    public String toString() {
        return "Obj: " + getDescription();
    }

    /**
     * @return obtain the description of how the tool provide the points
     */
    public String getDescription() {
        return description;
    };

    /**
     * @return obtain the card name of the objective
     */
    public String getCardName() {
        return cardName;
    }

    /**
     * @return the value provided by the Objective
     */
    public int getObjValue() {
        return objValue;
    }

    /**
     * @param grid we want to calculate the score on
     * @return the score given by this objective
     */
    public abstract int getScore(Grid grid);

    /**
     * @return the unique identifier of the objective card
     */
    public int getCardId() {
        return cardId;
    }; // Use to match card and graphics
}
