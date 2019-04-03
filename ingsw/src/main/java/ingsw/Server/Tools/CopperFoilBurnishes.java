package ingsw.Server.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Player;
import ingsw.Server.Utility.Coord;
import ingsw.Server.Utility.CopyObjects;
import ingsw.Server.Utility.GameException;

import java.util.List;


/**
 * This tool can be used in one single operation by choosing the source and destination cells on the grid
 * It allows the user to move a Die from a cell to another without respecting shade constraints
 *
 * Usage first run:  T-3 [X1-Y1] [X2-Y2]    optional: D-N for single player
 */
public class CopperFoilBurnishes extends GenericTool{

    public CopperFoilBurnishes() {
        this(GameType.MULTIPLAYER);
    }

    public  CopperFoilBurnishes(GameType gameType) {
        super(gameType);
        this.toolName = "Copper Foil Burnishes";
        this.description = "Move any one die in your window\n" +
        "ignoring shade restrictions\n" +
        "you must obey all other\n" +
        "placement restrictions.";
        this.toolNumber = 3;
        this.payColor = DiceColor.RED;
    }

    /**
     @throws GameException
     */
    @Override
    public boolean isUsable(ToolActionParameter tap, GameBoard gameBoard) throws GameException {

        List<Coord> coords = tap.getCoords();

        Player currentPlayer = gameBoard.getCurrentPlayer();
        Grid playerGrid = (Grid) CopyObjects.deepCopy(currentPlayer.getGrid());

        if (!correctTool(tap.getPickedTool())) {
            return false;
        }

        if (!validDiceCellNumber(tap, 2)) {
            return false;
        }

        if (!actuallyMoving(coords)) {
            return false;
        }

        if (!canPayPrice(tap, gameBoard)) {
            return false;
        }

        if (!coordinatesValid(playerGrid, coords)) {
            return false;
        }

        if (!correctSourceDestination(playerGrid, coords)) {
            return false;
        }

        Dice toMove = playerGrid.removeDie(coords.get(0).x, coords.get(0).y);

        boolean check1 = playerGrid.isValidNearColor(toMove, coords.get(1).x, coords.get(1).y);
        boolean check2;
        if (playerGrid.getDiceNum() == 0) { // If this is the first placement we can place it not near anything else.
            check2 = playerGrid.isValidBorderPlacement(coords.get(1).x, coords.get(1).y);
        } else {
            check2 = playerGrid.isValidNearAnotherPlacement(toMove, coords.get(1).x, coords.get(1).y);
        }

        if (check1 && check2) {
            return true;
        } else {
            throw new GameException("The usage would violate the placements restriction you have to follow");
        }
    }

    @Override
    public int use(ToolActionParameter tap, GameBoard gameBoard) {

        if (!isUsable(tap, gameBoard)) {
            throw new GameException("The tool is not usable");
        }

        List<Coord> coords = tap.getCoords();

        Player currentPlayer = gameBoard.getCurrentPlayer();

        payPrice(tap, gameBoard);

        Grid playerGrid = currentPlayer.getGrid();

        Dice popDice = playerGrid.getDie(coords.get(0).x, coords.get(0).y);
        playerGrid.removeDie(coords.get(0).x, coords.get(0).y);
        playerGrid.placeDieWithoutRestrictions(popDice, coords.get(1).x, coords.get(1).y);

        return 0;
    }
}

