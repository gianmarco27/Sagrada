package ingsw.Client.View;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Utility.Coord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  Object able to parse a string command into a coherent ToolActionParameter. Makes use of a specific syntax
 *
 *  T-N, where N is the number is the toolNumber
 *  [X-Y] provide a pair of coordinates, X and Y for selecting a cell
 *  P-N, specify a target dice in the dicePool
 *  R-N-N, specify a target dice in the roundTrackerTarget
 *  V-N, specify a value, to use in the callback Way
 *  D-N, specify a DicePay to use in a single player match to pay the tool with
 */
public class ToolParameterParserCLI {

    public static final ToolParameterParserCLI toolParameterParserCLI = new ToolParameterParserCLI();

    private List<Coord> gridCoord = new ArrayList<>();
    private int pickedDice;
    private Coord roundTracker;
    private int pickedTool;
    private int wishValue;
    private int payDice;
    private boolean callBack;

    /**
     * Base Constructor
     */
    private ToolParameterParserCLI() {
        pickedDice = -1;
        pickedTool = -1;
        wishValue = -1;
        payDice = -1;
    }

    public  static ToolParameterParserCLI getInstance() { return toolParameterParserCLI;}

    /**
     * Create a new ToolActionParameter from a string
     */
    public boolean toolFromString(String toParse) {
        return editToolParameter(toParse);
    }

    /**
     * Edit a ToolActionParameter by overwriting the set field.
     */
    public boolean editToolParameter(String toParse) {
        List<String> commands = Arrays.asList(toParse.trim().split(" +"));
        boolean setDice = false;
        for (String s : commands) {
            if (s.length() < 3 || s.length() > 5) {
                return false;
            }
            try {
                switch (Character.toUpperCase(s.charAt(0))) {
                    case 'T': {
                        this.pickedTool = Integer.parseInt(s.substring(2, s.length()));
                        break;
                    }
                    case '[': {
                        if (!setDice) {
                            this.gridCoord = new ArrayList<>();
                            setDice = true;
                        }
                        int x = Character.getNumericValue(s.charAt(1));
                        int y = Character.getNumericValue(s.charAt(3));
                        this.gridCoord.add(new Coord(x, y));
                        break;
                    }
                    case 'P': {
                        this.pickedDice = Character.getNumericValue(s.charAt(2));
                        break;
                    }
                    case 'R': {
                        this.roundTracker = new Coord(Character.getNumericValue(s.charAt(2)), Character.getNumericValue(s.charAt(4)));
                        break;
                    }
                    case 'V': {
                        this.wishValue = Character.getNumericValue(s.charAt(2));
                        break;
                    }
                    case 'D': {
                        this.payDice = Character.getNumericValue(s.charAt(2));
                        break;
                    }
                    default: {
                        return false;
                    }
                }
            }
            catch (IndexOutOfBoundsException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return a String text detailing how to format the command
     */
    public String getInstruction() {
        return  "\nPlease provide the TOOL settings following this specific format" +
                " *  T-N, where N is the number is the toolNumber\n" +
                " *  [X-Y] provide a pair of coordinates, X and Y for selecting a cell\n" +
                " *  P-N, specify a target dice in the dicePool\n" +
                " *  R-N-N, specify a target dice in the roundTrackerTarget, Starting from R-0-0\n" +
                " *  V-N, specify a value, to use in the callback Way\n" +
                " *  D-N, specify a DicePay to use in a single player match to pay the tool with\n";
    }

    /**
     * Setter for specify if there is this ToolAction parameter is a callBack one
     */
    public void setCallback(boolean callBack) {
        this.callBack = callBack;
    }

    public void reset() {
        gridCoord = new ArrayList<>();
        pickedDice = -1;
        roundTracker = null;
        pickedTool = -1;
        wishValue = -1;
        payDice = -1;
    }

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
        if (callBack) {
            tap.setCallBack(true);
        }

        reset();

        return tap;
    }
}