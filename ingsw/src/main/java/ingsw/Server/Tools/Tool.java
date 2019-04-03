package ingsw.Server.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.GameBoard;

import java.io.Serializable;

/**
 * Interface that all tools implement
 */
public interface Tool extends Serializable {

    /**
     * @return a string representation of the tool effect
     */
    public String getDescription();

    /**
     * @return the tools id
     */
    public int getToolNumber();

    /**
     * @return the tools cost
     */
    public int getCost();

    /**
     * @return the cost color of the tool to be used for singlePlayer functionality
     */
    public DiceColor getColorCost();

    /**
     * @return the tools name
     */
    public String getToolName();

    /**
     * @return boolean that determines if such tool is usable from a certain player on a given board
     */
    public boolean isUsable(ToolActionParameter tap, GameBoard gameBoard); // Passing Board, Player

    /**
     * Applies the tool effect onto the gamestate
     */
    public int use(ToolActionParameter tap, GameBoard gameBoard); // Passing Board, Player

}
