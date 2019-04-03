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

import java.util.List;

/**
 * This tool can be used in one single operation just by selecting it, the target DicePool die and the gridcell you want to place it
 * before we check if the tool is usable by verifying the current player hasn't already played a die in this turn
 * It allows the user to flip the value of the Die in the DicePool
 *
 * Usage first run:  T-10 P-N [X-Y] optional: D-N for single player     optional: D-N for single player
 */
public class GrindingStone extends GenericTool{

    public GrindingStone() {
        this(GameType.MULTIPLAYER);
    }

    public  GrindingStone(GameType gameType) {
        super(gameType);
        this.toolName = "Grinding Stone";
        this.description = "After drafting, flip the die\n" +
                "to its opposite side\n" +
                "6 flips to 1, 5 to 2, 4 to 3 etc.";
        this.toolNumber = 10;
        this.payColor = DiceColor.GREEN;
    }

    @Override
    public boolean isUsable(ToolActionParameter tap, GameBoard gameBoard) throws GameException {

        Player currentPlayer = gameBoard.getCurrentPlayer();
        Grid playerGrid = currentPlayer.getGrid();
        List<Coord> coords = tap.getCoords();

        if (!correctTool(tap.getPickedTool())) {
            return false;
        }

        if (!canPayPrice(tap, gameBoard)) {
            return false;
        }

        if (alreadyPlayedDie(currentPlayer)) {
            return false;
        }

        if (!tap.isWorkOnDicePool() ||
                !validDicePoolTarget(tap.getDicePoolTarget(), gameBoard.getDicePool())) {
            return false;
        }

        if (!validDiceCellNumber(tap, 1)) {
            return false;
        }

        if (!coordinatesValid(playerGrid, coords)) {
            return false;
        }

        Dice toChange;
        try {
            toChange = peekDicePoolTarget(tap.getDicePoolTarget(), gameBoard.getDicePool());
        } catch (NoMoreBagElementException e) {
            return false; // This should never be possible with the standard ruleset
        }

        // We work with a copy so we don't mess with the original mutable one
        Dice flipped = new Dice(toChange.getValue(), toChange.getColor());
        flipped.oppositeSide();

        if (!playerGrid.isValidPlacement(flipped, coords.get(0).x , coords.get(0).y)) {
            throw new GameException("The flipped die is not playable in that spot");
        }

        return true;

    }

    @Override
    public int use(ToolActionParameter tap, GameBoard gameBoard) throws GameException {

        if (!isUsable(tap, gameBoard)) {
            throw new GameException("The tool is not usable");
        }

        Player currentPlayer = gameBoard.getCurrentPlayer();
        Grid playerGrid = currentPlayer.getGrid();
        List<Coord> coords = tap.getCoords();
        DicePool dicePool = gameBoard.getDicePool();

        Dice toChange;
        try {
            toChange = dicePool.getSingleDice(tap.getDicePoolTarget());
        } catch (NoMoreBagElementException e) {
            return -1;
        }

        currentPlayer.setDicePlayed(true);

        toChange.oppositeSide();
        playerGrid.placeDieWithoutRestrictions(toChange, coords.get(0).x , coords.get(0).y);

        payPrice(tap, gameBoard, true);

        return 0;
    }

}
