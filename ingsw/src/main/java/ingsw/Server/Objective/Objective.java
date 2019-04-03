package ingsw.Server.Objective;


import java.io.Serializable;
import ingsw.Server.Grid.Grid;

/**
 * Interface that all Objectives implement
 */
public interface Objective extends Serializable {

    public String getDescription();

    public int getScore(Grid grid); // Passing Board

    public String getCardName();

    public int getObjValue();

    public int getCardId();
}
