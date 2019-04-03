package ingsw.Server.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Grid.Grid;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Player;
import ingsw.Server.Utility.Coord;
import ingsw.Server.Utility.GameException;

import java.util.List;

/**
 * This tool can be used in one single operation by choosing the DicePool Die and destination cell on the grid
 *It allows the user to place a drafted Die in a cell non-adjacent to any other Die
 *
 *  Usage first run:  T-9 P-N [X-Y] optional: D-N for single player
 */
public class CorkBackedStraightedge extends GenericTool{

    public CorkBackedStraightedge() {
        this(GameType.MULTIPLAYER);
    }


    public CorkBackedStraightedge(GameType gameType) {
        super(gameType);
        this.toolName = "Cork Backed Straightedge";
        this.description = "After drafting, place the\n" +
        "die in a spot that is not adjacent to\n" +
        "another die.\n" +
        "You must obey all other\n" +
        "placement restrictions.";
        this.toolNumber = 9;
        this.payColor = DiceColor.YELLOW;
    }

  @Override
    public boolean isUsable(ToolActionParameter tap, GameBoard gameBoard) throws GameException {

        List<Coord> coords = tap.getCoords();

        Player currentPlayer = gameBoard.getCurrentPlayer();
        Grid playerGrid = currentPlayer.getGrid();

        if (!correctTool(tap.getPickedTool())) {
            return false;
        }

        if (alreadyPlayedDie(currentPlayer)) {
            return false;
        }

        if (!validDiceCellNumber(tap, 1)) {
            return false;
        }

        if (!canPayPrice(tap, gameBoard)) {
            return false;
        }

        if (!coordinatesValid(playerGrid, coords)) {
            return false;
        }

        if (!playerGrid.isCellEmptyVerbose(coords.get(0).x, coords.get(0).y)) {
            return false;
        }

        if (!tap.isWorkOnDicePool() ||
                !validDicePoolTarget(tap.getDicePoolTarget(), gameBoard.getDicePool())) {
            return false;
        }

        Dice poolDice;
        try {
            poolDice = peekDicePoolTarget(tap.getDicePoolTarget(), gameBoard.getDicePool());
        } catch (NoMoreBagElementException e) {
            return false;
        }

        if (playerGrid.isValidAdjacentPlacement(poolDice, coords.get(0).x, coords.get(0).y)) {
            return false;
        }

        if (!playerGrid.isPlaceable(poolDice, coords.get(0).x, coords.get(0).y)) {
            return false;
        }

        return true;
    }

    @Override
    public int use(ToolActionParameter tap, GameBoard gameBoard) {

        if (!isUsable(tap, gameBoard)) {
            throw new GameException("The tool is not usable");
        }

        List<Coord> coords = tap.getCoords();

        Player currentPlayer = gameBoard.getCurrentPlayer();

        Grid playerGrid = currentPlayer.getGrid();

        currentPlayer.setDicePlayed(true);

        Dice poolDice;
        try {
            poolDice = gameBoard.getDicePool().getSingleDice((tap.getDicePoolTarget()));
        } catch (NoMoreBagElementException e) {
            return -1;
        }

        payPrice(tap, gameBoard, true);

        playerGrid.placeDieWithoutRestrictions(poolDice, coords.get(0).x, coords.get(0).y);

        return 0;
    }
}
