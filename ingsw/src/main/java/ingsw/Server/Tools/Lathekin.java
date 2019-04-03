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
 * This tool can be used in one single operation just by selecting it, and selecting the first die and where to move it and then the second die and where to move it
 * It allows the user to move two dice in his grid respecting all the placement restrictions
 **
 * Usage first run:  T-4 P-N [X1-Y1] [X1-Y1] [X2-Y2] [X2-Y2] optional: D-N for single player
 */
public class Lathekin extends GenericTool {

    public Lathekin() {
        this(GameType.MULTIPLAYER);
    }

    public Lathekin(GameType gameType) {
        super(gameType);
        this.toolName = "Lathekin";
        this.description = "Move exactly two dice, obeying\n" +
                "all placement restrictions.";
        this.toolNumber = 4;
        this.payColor = DiceColor.YELLOW;
    }

    @Override
    public boolean isUsable(ToolActionParameter tap, GameBoard gameBoard) {

        List<Coord> coords = tap.getCoords();

        Player currentPlayer = gameBoard.getCurrentPlayer();
        Grid playerGrid = (Grid) CopyObjects.deepCopy(currentPlayer.getGrid());

        if (!correctTool(tap.getPickedTool())) {
            return false;
        }

        if (!validDiceCellNumber(tap, 4)) {
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

        Dice toMove1 = playerGrid.removeDie(coords.get(0).x, coords.get(0).y);
        Dice toMove2 = playerGrid.removeDie(coords.get(2).x, coords.get(2).y);

        boolean validPlacement1 = playerGrid.isValidPlacement(toMove1, coords.get(1).x, coords.get(1).y);

        // Repositioning the first dice in the new position for evaluation purposes
        playerGrid.placeDieWithoutRestrictions(toMove1, coords.get(1).x, coords.get(1).y);
        // Check if after the first dice has been moved the second action is valid
        boolean validPlacement2 = playerGrid.isValidPlacement(toMove2, coords.get(3).x, coords.get(3).y);

        if (validPlacement1 && validPlacement2) {
            return true;
        } else {
            throw new GameException("You must respect all the placement restrictions");
        }
    }

    @Override
    public int use(ToolActionParameter tap, GameBoard gameBoard) {

        if (!isUsable(tap, gameBoard)) {
            throw new GameException("The tool is not usable");
        }

        List<Coord> coords = tap.getCoords();

        Player currentPlayer = gameBoard.getCurrentPlayer();

        Grid playerGrid = currentPlayer.getGrid();

        Dice popDice1 = playerGrid.removeDie(coords.get(0).x, coords.get(0).y);
        Dice popDice2 = playerGrid.removeDie(coords.get(2).x, coords.get(2).y);

        payPrice(tap, gameBoard);

        playerGrid.placeDieWithoutRestrictions(popDice1, coords.get(1).x, coords.get(1).y);
        playerGrid.placeDieWithoutRestrictions(popDice2, coords.get(3).x, coords.get(3).y);

        return 0;
    }
}
