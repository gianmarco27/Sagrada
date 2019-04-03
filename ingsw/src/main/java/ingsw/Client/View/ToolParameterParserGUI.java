package ingsw.Client.View;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Utility.Coord;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which is responsible for interacting with the GUI selection to create a toolParameterParser
 */
public class ToolParameterParserGUI {

    private static final ToolParameterParserGUI toolParameterParserGUI = new ToolParameterParserGUI();

    private List<Coord> gridCoord = new ArrayList<>();
    private int pickedDice;
    private Coord roundTracker;
    private int pickedTool;
    private int wishValue;
    private int payDice;

    /**
     * Basic constructor
     */
    private ToolParameterParserGUI() {
        pickedDice = -1;
        pickedTool = -1;
        wishValue = -1;
        payDice = -1;
    }

    public static ToolParameterParserGUI getInstance() {
        return toolParameterParserGUI;
    }

    public void addCoord(Coord coord) {
        gridCoord.add(coord);
    }

    public void removeCoord(Coord coord) {
        gridCoord.remove(coord);
    }

    public void setDicePool(int pickedDice) {
        this.pickedDice = pickedDice;
    }

    public void setRoundTracker(Coord coord) {
        this.roundTracker = coord;
    }

    public void setPickedTool(int toolNumber) {
        this.pickedTool = toolNumber;
    }

    public void setWishValue(int wishValue) {
        this.wishValue = wishValue;
    }

    public void setPayDicePool(int pickedDice) {
        this.payDice = pickedDice;
    }

    /**
     * Rest the ToolParameterParserGUI fields to the original values
     */
    public void reset() {
        gridCoord = new ArrayList<>();
        pickedDice = -1;
        roundTracker = null;
        pickedTool = -1;
        wishValue = -1;
        payDice = -1;
    }

    /**
     * @return ToolActionParameter created from the selected fields
     */
    public ToolActionParameter createCommand() {

        ToolActionParameter tap = new ToolActionParameter();

        if (pickedTool != -1) {
            tap.setPickedTool(pickedTool);
        }
        if (roundTracker != null) {
            tap.setWorkOnRoundTracker(true);
            tap.setRoundTrackerTargetRound(roundTracker.x);
            tap.setRoundTrackerTargetOption(roundTracker.y);
        }
        if (pickedDice != -1) {
            tap.setWorkOnDicePool(true);
            tap.setDicePoolTarget(pickedDice);
        }
        if (!gridCoord.isEmpty()) {
            for (Coord c : gridCoord) {
                tap.addCoords(c.x, c.y);
            }
        }
        if (wishValue != -1) {
            tap.setValueChangeTo(wishValue);
        }
        if (payDice != -1) {
            tap.setPayDicePool(payDice);
        }

        return tap;
    }
}

