package ingsw.Server.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Player;
import ingsw.Server.Utility.Coord;
import ingsw.Server.Utility.GameException;
import ingsw.Server.Utility.CopyObjects;

import java.util.Arrays;
import java.util.List;

/**
 * This tool can be used in one single operation just by selecting it, selecting the RoundTraacker Die
 * then selecting the first die and where to move it and eventually the second die and where to move it
 * they both must be of the same color of the RoundTracker one
 * It allows the user to move up to two dice in his grid respecting all the placement restrictions as long as they are of the same color of the RT one
 *
 * Usage first run:  T-12 R-N-N [X1-Y1] [X1-Y1] eventually add: [X2-Y2] [X2-Y2]     optional: D-N for single player
 */
public class TapWheel extends GenericTool {

    public TapWheel() {
        this(GameType.MULTIPLAYER);
    }

    public TapWheel(GameType gameType) {
        super(gameType);
        this.toolName = "Tap Wheel";
        this.description = "Move up to two dice of the same\n" +
                "color that match the color of the die\n" +
                "on the Round Track\n" +
                "You must obey all other\n" +
                "placement restrictions.";
        this.toolNumber = 12;
        this.payColor = DiceColor.BLUE;
    }
        @Override
        public boolean isUsable(ToolActionParameter tap, GameBoard gameBoard) {
            List<Coord> coord = tap.getCoords();

            Player currentPlayer = gameBoard.getCurrentPlayer();
            Grid playerGrid = (Grid) CopyObjects.deepCopy(currentPlayer.getGrid());

            if (!correctTool(tap.getPickedTool())) {
                return false;
            }

            if (!canPayPrice(tap, gameBoard)) {
                return false;
            }

            if (!tap.isWorkOnRoundTracker() ||
                    !validRoundTracker(tap, gameBoard.getRoundTracker())) {
                return false;
            }
            if (!(validDiceCellNumber(tap, 2, 4))) {
                return false;
            }

            if (!coordinatesValid(playerGrid, coord)) {
                return false;
            }

            if (!correctSourceDestination(playerGrid, coord)) { //Simultaneous moving
                return false;
            }

            if (!actuallyMoving(coord)) {
                return false;
            }

            Dice toMove1 = playerGrid.removeDie(coord.get(0).x, coord.get(0).y);// removed to correctly evaluate new positioning
            Dice rtDice = peekRoundTracker(tap, gameBoard.getRoundTracker());
            boolean validColor = (toMove1.getColor() == rtDice.getColor());
            boolean validPlacement1;
            boolean validPlacement2 = true;
            if (tap.getCoords().size() == 4) { // Case of two dice moving
                Dice toMove2 = playerGrid.removeDie(coord.get(2).x, coord.get(2).y);
                validColor = (toMove2.getColor() == rtDice.getColor()); // Second dice same color of rtDice
                validPlacement1 = playerGrid.isValidPlacement(toMove1, coord.get(1).x, coord.get(1).y);

                // Repositioning the first dice in the new position for evaluation purposes
                playerGrid.placeDieWithoutRestrictions(toMove1, coord.get(1).x, coord.get(1).y);
                // Check if after the first dice has been moved the second action is valid
                validPlacement2 = playerGrid.isValidPlacement(toMove2, coord.get(3).x, coord.get(3).y);
            } else {
                validPlacement1 = playerGrid.isValidPlacement(toMove1, coord.get(1).x, coord.get(1).y);
            }
            if (validPlacement1 && validPlacement2 && validColor) {
                return true;
            } else {
                throw new GameException("You should still obey all the placements restrictions!");
            }
        }

    @Override
    public int use(ToolActionParameter tap, GameBoard gameBoard) { // in case of only two dice on the Grid, 1 must be placed on the border

        if (!isUsable(tap, gameBoard)) {
            throw new GameException("The tool is not usable");
        }

        Player currentPlayer = gameBoard.getCurrentPlayer();
        List<Coord> coords = tap.getCoords();
        Grid playerGrid = currentPlayer.getGrid();

        Dice temp1 = playerGrid.removeDie(coords.get(0).x, coords.get(0).y);

        if (tap.getCoords().size() == 4) {
            Dice temp2 = playerGrid.removeDie(coords.get(2).x, coords.get(2).y);
            if (!playerGrid.placeDie(temp2, coords.get(3).x, coords.get(3).y)) {
                // If we hit a case where we can't proceed we set the grid back and return the error.
                playerGrid.placeDieWithoutRestrictions(temp2, coords.get(2).x, coords.get(2).y);
                playerGrid.placeDieWithoutRestrictions(temp1, coords.get(0).x, coords.get(0).y);
                return -1;
            }
        }

        if (!playerGrid.placeDie(temp1, coords.get(1).x, coords.get(1).y)) {
            playerGrid.placeDieWithoutRestrictions(temp1, coords.get(0).x, coords.get(0).y);
            return -1;
        }

        payPrice(tap, gameBoard);

        return 0;
    }
}
