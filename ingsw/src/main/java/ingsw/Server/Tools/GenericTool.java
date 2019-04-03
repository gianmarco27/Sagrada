package ingsw.Server.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Grid.Grid;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Player;
import ingsw.Server.Utility.Coord;
import ingsw.Server.Utility.GameException;
import ingsw.Server.Utility.RoundTracker;

import java.util.List;
import java.util.Objects;

import static java.lang.Math.max;

public abstract class GenericTool implements Tool {

    private GameType gameType;
    String description;
    String toolName;
    boolean used;
    int toolNumber;
    DiceColor payColor;

    public GenericTool() {
        gameType = GameType.MULTIPLAYER;
        this.used = false; // Avoided to rely on the default Java behaviour for clarity
    }

    public GenericTool(GameType gameType) {
        this();
        this.gameType = gameType;
    }

    /**
     * Utility function used to fill a line up to a certain lenght
     */
    private String padLine(String toPad, int size, char padWith) {
        return toPad + new String(new char[size - toPad.length()]).replace('\0', padWith);
    }

    @Override
    public String toString() {
        // We first compute the biggest line size we'll encounter so that we can then pad everything accordingly
        int lineSize = 0;
        for (String descLine : description.split("\\r?\\n")) {
            lineSize = max(lineSize, descLine.length() + 4);
        }
        lineSize = max(toolName.length() + 20, lineSize);
        StringBuilder rep = new StringBuilder();
        rep.append("__" + padLine("", lineSize, '_') + "_\n");
        rep.append("| " + padLine(toolName, lineSize, ' ') + "|\n");
        rep.append("| " + padLine((payColor + "| N°" + toolNumber + " | Cost: " + ((used) ? 2 : 1)), lineSize, ' ') + "|\n");
        rep.append("| " + padLine("", lineSize, ' ') + "|\n");
        for (String descLine : description.split("\\r?\\n")) {
            rep.append("| " + padLine(descLine, lineSize, ' ') + "|\n");
        }
        rep.append("|__" + padLine("", lineSize - 1, '_') + "|\n");
        return rep.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GenericTool)) {
            return false;
        }
        GenericTool other = (GenericTool) o;
        return this.toolNumber == other.getToolNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hash(toolNumber);
    }

    /**
     * @return a textual description of a given tool
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the card name of a given tool
     */
    public String getToolName() {
        return toolName;
    }

    /**
     * @return the cost for using the tool, either 2 or 1, in multiPlayer
     */
    public int getCost() {
        return (used ? 2 : 1);
    }

    /**
     * @return the color of the die it's used as a payment in singlePlayer
     */
    public DiceColor getColorCost() {
        return payColor;
    };

    /**
     * @return the corresponding tool number
     */
    public int getToolNumber() {
        return toolNumber;
    }

    /**
     * @param tap the tool action parameter the tool is being used with
     * @param gameBoard the gameboard the tool is being used on
     * @throws GameException after calling isUsable if the tool couldn't be used with the actual parameters containing in the exception message the actual motivation why
     */
    abstract public boolean isUsable(ToolActionParameter tap, GameBoard gameBoard);

    /**
     * @param tap the tool action parameter the tool is being used with
     * @param gameBoard the gameboard the tool is being used on
     * @throws GameException after calling isUsable if the tool couldn't be used with the actual parameters containing in the exception message the actual motivation why
     */
    abstract public int use(ToolActionParameter tap, GameBoard gameBoard);

    /**
     * Check if the user can afford to pay for the tool, for the singlePlayer check we also make sure
     * the user never played with the tool
     */
    public boolean canPayPrice(ToolActionParameter tap, GameBoard gameBoard) {
        if (this.gameType == GameType.MULTIPLAYER) {
            return canPayMultiplayer(gameBoard.getCurrentPlayer());
        } else if (this.gameType == GameType.SINGLEPLAYER) {
            return canPaySinglePlayer(tap, gameBoard);
        }
        return true; // Edge case covered in case we add more gamemode type
    }

    /**
     * overloading the method payPrice that if called without the offset runs payPrice with the default value "false"
     */
    public void payPrice(ToolActionParameter tap, GameBoard gameBoard) {
        payPrice(tap, gameBoard, false);
    }

    /**
     * Determines if the current match is MultiPlayer or SinglePlayer
     * then calls the relative payPrice function to pay the price of the selected tool
     *
     * @param offset a boolean Value to be used in the singlePlayer payPrice
*              to determine if the payDice index need to be corrected (after another PoolDice has been used))
     */
    public void payPrice(ToolActionParameter tap, GameBoard gameBoard, boolean offset) {
        if (this.gameType == GameType.MULTIPLAYER) {
            payPriceMultiplayer(gameBoard.getCurrentPlayer());
        } else if (this.gameType == GameType.SINGLEPLAYER) {
            payPriceSinglePlayer(tap, gameBoard, offset);
        }
    }

    /**
     * Check if the user can afford to pay for the tool in the multiplayer version
     */
    public boolean canPayMultiplayer(Player currentPlayer) {
        if (currentPlayer.getToken() >= (this.used ? 2 : 1)) {
            return true;
        } else {
            throw new GameException("You can't afford to pay the price of the tool!");
        }
    }

    /**
     * Actually make the user pay for the price and set the tool to already used once so next time we pay more $$
     * in the multiplayer version
     */
    public void payPriceMultiplayer(Player currentPlayer) {
        currentPlayer.spendToken((used ? 2 : 1));
        this.used = true;
        return;
    }

    /**
     * Check if the tool is usable in a single player
     * **/
    public boolean canPaySinglePlayer(ToolActionParameter tap, GameBoard gameBoard) {
        if (this.used) {
            throw new GameException("You already used this tool!");
        }

        try { // Catch an "Invalid dice pool" exception and throw it with a more relevant message
            validDicePoolTarget(tap.getPayDicePool(), gameBoard.getDicePool());
        } catch (GameException e) {
            throw new GameException("The selected dicePool die for paying the tool is invalid");
        }

        if (tap.isWorkOnDicePool() && tap.getPayDicePool() == tap.getDicePoolTarget()) {
            throw new GameException("You can't pay with a die you intend to use!");
        }

        try {
            if (peekDicePoolTarget(tap.getPayDicePool(), gameBoard.getDicePool()).getColor() != this.payColor) {
                throw new GameException("You should pay the price of the tool with a dice with the same color");
            }
        } catch (NoMoreBagElementException e ) { // Should never happen.. just in case
            throw new GameException("There are no more element in the dicePool");
        }

        if (tap.getPayDicePool() < 0) {
            throw new GameException("You didn't select any dice to pay with");
        }

        return true;
    }

    /**
     * Actually spend the die selected from the pool to pay the usage of a tool on single player
     * @param tap the current toolActionParameter
     * @param gm the game gameBoard
     * @param offset a boolean value that tells me if i need to offset the payDice index or not
     *               depending on the fact that another Dice in the DicePool has been removed by the tool
     */
    public void payPriceSinglePlayer(ToolActionParameter tap, GameBoard gm, boolean offset) {

        DicePool dp = gm.getDicePool();
        boolean diceIsAfter = (tap.getPayDicePool() > tap.getDicePoolTarget());

        int toSpend = (diceIsAfter && offset) ? (tap.getPayDicePool()-1) : tap.getPayDicePool();

        try {
            dp.getSingleDice(toSpend);
        } catch (NoMoreBagElementException e) {
            throw new GameException("The dicePool is empty, we cannot proceed"); // Seems unlikely since we check beforehands...
        }

        this.used = true;

        return;
    }

    /**
     * Fast check if the tool number is the selected tool number
     */
    public boolean correctTool(int selectedTool) {
        if (selectedTool == this.toolNumber) {
            return true;
        } else {
            throw new GameException("The tool selected does not match the given one");
        }
    }

    /**
     * Check if the number of provided diceCell is reasonable, aka, for each X there is a Y and there are
     *
     * @param expectedSize n of expected tuples
     */
    public static boolean validDiceCellNumber(ToolActionParameter tap, int expectedSize) {
        if (tap.getCoords().size() == expectedSize) {
            return true;
        } else {
            throw new GameException("The number of coordinates passed is different from the expected one for this tool!");
        }
    }

    /**
     * Check if the number of provided diceCell is reasonable, aka, for each X there is a Y and there are
     *
     * @param expectedSize n of expected tuples or
     * @param otherOption n of expected tuples
     */
    public static boolean validDiceCellNumber(ToolActionParameter tap, int expectedSize, int otherOption) {
        if (tap.getCoords().size() == expectedSize || tap.getCoords().size() == otherOption) {
            return true;
        } else {
            throw new GameException("The number of coordinates passed is different from the expected one for this tool!");
        }
    }

    /**
     * Check if the couple of coordinates sadisfy the grid valid check
     */
    public static boolean coordinatesValid(Grid validOn, List<Coord> coords) {

        for (Coord c : coords) {
            if (!validOn.areCoordinatesValids(c.x, c.y)) {
                throw new GameException("At least one of the provided coords is invalid:\t" + c.x + "-" + c.y);
            }
        }

        return true;
    }

    /**
     * Sanity check that all the provided pair(x,y) have destination != source. Work on even sized tuples of coordinates
     */
    public static boolean actuallyMoving(List<Coord> coords) {
        for (int i = 0; i < coords.size(); i += 2) {
            if (coords.get(i) == coords.get(i + 1)) {
                throw new GameException("One of the provided movement has the same source and destination!");
            }
        }
        return true;
    }

    /**
     * Assert that all provided pair(x,y) move from a full cell to an empty one. Work on even sized tuples of coordinates
     * Used for movements command
     */
    public static boolean correctSourceDestination(Grid validOn, List<Coord> coords) {
        for (int i = 0; i < coords.size(); i += 2) {
            if (validOn.isCellEmpty(coords.get(i).x, coords.get(i).y) ||
                    !validOn.isCellEmptyVerbose(coords.get(i+1).x, coords.get(i+1).y)) {
                throw new GameException("One of the provided movement work from an empty cell/to a full cell");
            }
        }
        return true;
    }

    /**
     * Check if a choice from the DicePool is a valid choice or is a dice which is not there
     */
    public static boolean validDicePoolTarget(int poolChoice, DicePool dicePool) {
        if (0 <= poolChoice && poolChoice >= dicePool.getAllDice().size()) {
            throw new GameException("The selected dice from the DicePool is not a valid choice");
        }
        return true;
    }

    /**
     * @return obtain a die from the DicePool without removing it
     */
    public static Dice peekDicePoolTarget(int poolChoice, DicePool dicePool) throws NoMoreBagElementException {
        return dicePool.peekDice(poolChoice);
    }

    /**
     * Check that make sure we are not trying to work with an empty place on the roundTracker
     */
    public static boolean validRoundTracker(ToolActionParameter tap, RoundTracker rt) {
    if (rt.peekPosition(tap.getRoundTrackerTargetRound(), tap.getRoundTrackerTargetOption()) == null) {
        throw new GameException("The roundtracker selected die is not there!");
        }
    return true;
    }

    /**
     * @return obtain a die from the roundTracker without removing it
     */
    public static Dice peekRoundTracker(ToolActionParameter tap, RoundTracker rt) {
        return rt.peekPosition(tap.getRoundTrackerTargetRound(), tap.getRoundTrackerTargetOption());
    }

    /**
     * Helper function that check if a die is playable on a given grid
     * @return the result of the check
     */
    public static boolean isPlaceable(Grid placeOn, Dice toPlace) {
        return placeOn.suggestPlacements((toPlace)).size() > 0;
    }
    public static boolean isPlaceableColor(Grid placeOn, DiceColor toPlace) {
        return placeOn.suggestPlacementsByColor((toPlace)).size() > 0;
    }

    /**
     * Check that make sure by using a tool which imply the placement we have not already did a placement this turn
     */
    public static boolean alreadyPlayedDie(Player player) {
        if (player.isDicePlayed()) {
            throw new GameException("You cannot use this tool if you have already placed a die this turn!");
        } else {
            return false;
        }
    }

    /**
     * Generic check that takes care a player can use a tool only after a give turn
     */
    public static boolean useFromTurn(Player player, int turn) {
        if (player.getTurnPlayed() < turn - 1) {
            throw new GameException("You can't use this tool until your turn N° " + turn);
        } else {
            return false;
        }
    }
}