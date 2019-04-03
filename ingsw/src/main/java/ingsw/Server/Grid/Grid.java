package ingsw.Server.Grid;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Utility.Coord;
import ingsw.Server.Utility.GameException;
import javafx.scene.paint.Color;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * This class represent one of the players grid, it can hold dice and restriction.
 * It contains the logic which allow/deny placements and other utility function.
 * The grid can be loaded by a JSONObject which allow for flexible and quick edit to the
 * game boards.
 */
public class Grid implements Serializable {

    public static final int COLUMNS = 5;
    public static final int ROWS = 4;

    private String cardName;
    private int cardId;
    private int matchId;
    private int favorPoints;
    private GridCell[][] cells = new GridCell[COLUMNS][ROWS];

    public Grid(JSONObject configuration) throws InvalidJsonException {

        this.cardName = (String) configuration.get("cardName");
        this.cardId = (Integer) configuration.get("cardId");
        this.matchId = (Integer) configuration.get("matchId");
        this.favorPoints = (Integer) configuration.get("favorPoints");
        JSONArray grid = configuration.getJSONArray("restrictions");
        int row_idx, col_idx;
        row_idx = -1;
        for (Object x : grid) {
            row_idx ++;
            col_idx = -1;
            JSONArray row = (JSONArray) x;
            for (Object y : row) {
                col_idx ++;
                String cell = (String) y;
                if (cell.equals("")) {
                    cells[col_idx][row_idx] = new GridCell();
                } else if (cell.matches("[1-6]")) {
                    cells[col_idx][row_idx] = new GridCellShade(Integer.parseInt(cell));
                } else if ("red".equals(cell)) {
                    cells[col_idx][row_idx] = new GridCellColor(DiceColor.RED);
                } else if ("yellow".equals(cell)) {
                    cells[col_idx][row_idx] = new GridCellColor(DiceColor.YELLOW);
                } else if ("purple".equals(cell)) {
                    cells[col_idx][row_idx] = new GridCellColor(DiceColor.PURPLE);
                } else if ("blue".equals(cell)) {
                    cells[col_idx][row_idx] = new GridCellColor(DiceColor.BLUE);
                } else if ("green".equals(cell)) {
                    cells[col_idx][row_idx] = new GridCellColor(DiceColor.GREEN);
                } else {
                    throw new InvalidJsonException();
                }
            }
        }
    }

    /**
     * Check if the color given is the same of the dice at position [X,Y]
     * @return the result of the comparison
     */
    private boolean isSameColor(DiceColor color, int x, int y) {
        if (!areCoordinatesValids(x, y) || isCellEmpty(x, y)) {
            return false;
        } else {
            return getDie(x, y).getColor() == color;
        }
    }

    /**
     * Check for all adjacent non-diagonal cells(+) that no-one has the same color of the one we are trying to place
     */
    public boolean isValidNearColor(Dice die, int x, int y) {
        if (!areCoordinatesValids(x, y)) {
            throw new GameException("Invalid coordinates");
        }
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                if ((dx != 0 || dy != 0) && (abs(dx) + abs(dy)) != 2) { // All strictly adjacent cells
                    if (isSameColor(die.getColor(), x + dx, y + dy)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Check if the value given is the same of the dice at position [X,Y]
     * @return the result of the comparison
     */
    private boolean isSameValue(int value, int x, int y) {
        try {
            if (!areCoordinatesValids(x, y) || isCellEmpty(x, y)) {
                return false;
            } else {
                return getDie(x, y).getValue() == value;
            }
        } catch (GameException e) {
            return false;
        }
    }

    /**
     * Check for all adjacent non-diagonal cells(+) that no-one has the same value of the one we are trying to place
     */
    public boolean isValidNearShade(Dice die, int x, int y) {
        if (!areCoordinatesValids(x, y)) {
            return false;
        }
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                if ((dx != 0 || dy != 0) && (abs(dx) + abs(dy)) != 2) { // All strictly adjacent cells
                    if (isSameValue(die.getValue(), x + dx, y + dy)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * @return if the target placement is on the grid border
     */
    public boolean isValidBorderPlacement(int x, int y) {
        if (!areCoordinatesValids(x, y)) {
            return false;
        }
        return ((x == 0) || (x == cells.length - 1) || (y == 0) || (y == cells[x].length - 1));
    }

    /**
     * Helper function to use to check if a position has dice nearby
     */
    private boolean nearDice(int x, int y) {
        if (!areCoordinatesValids(x, y)) {
            return false;
        }
        return !isCellEmpty(x, y);
    }

    /**
     * Check the 8 cells diagonal nearby
     * @return is at least one of them contains a die
     */
    public boolean isValidNearAnotherPlacement(Dice die, int x, int y) {
        return isValidDiagonalPlacement(die, x, y) ||
                isValidAdjacentPlacement(die, x, y);
    }

    /**
     * Check the 4 cells diagonal nearby (X)
     * @return is at least one of them contains a die
     */
    public boolean isValidDiagonalPlacement(Dice die, int x, int y) {
        if (!areCoordinatesValids(x, y)) {
            return false;
        }
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                if ((abs(dx) + abs(dy)) == 2) { // Strictly diagonal
                    if (nearDice(x + dx, y + dy)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check the 4 cells adjacent nearby (+)
     * @return is at least one of them contains a die
     */
    public boolean isValidAdjacentPlacement(Dice die, int x, int y) {
        if (!areCoordinatesValids(x, y)) {
            return false;
        }
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                if ((abs(dx) + abs(dy)) == 1) { // Strictly adjacent
                    if (nearDice(x + dx, y + dy)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return the number of dice placed in the grid
     */
    public int getDiceNum() {
        int sum = 0;
        for(int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                if (!isCellEmpty(x,y))
                    sum ++;
            }
        }
        return sum;
    }

    /**
     * Check if the target cell is empty
     */
    public boolean isCellEmpty(int x,int y) {
        if (areCoordinatesValids(x,y)) {
            if (cells[x][y].isEmpty())
                return true;
        }
        return false;
    }
    /**
     * Check if the target cell is empty
     */
    public boolean isCellEmptyVerbose(int x,int y) {
        if (areCoordinatesValids(x,y)) {
            if (cells[x][y].isEmpty())
                return true;
        }
        throw new GameException("The selected cell is not empty");
    }

    public boolean areCoordinatesValids(int x, int y) {
        return (0 <= x && x < cells.length) && (0 <= y && y < cells[x].length);
    }

    /**
     * Check the placements restriction of the dice in a target cell
     * @return if is dice obey all gridCell restriction
     */
    public boolean isPlaceable(Dice die, int x, int y) {
        return cells[x][y].isPlaceable(die);
    }

    public boolean isValidPlacement(Dice die, int x, int y) {
        if (areCoordinatesValids(x, y)) {
            if (isCellEmptyVerbose(x, y) &&
                    isValidNearColor(die, x, y) &&
                    isValidNearShade(die, x, y) &&
                    isPlaceable(die, x, y)) {
                if (getDiceNum() == 0) { // If this is the first placement we can place it not near anything else.
                    return isValidBorderPlacement(x, y);
                } else {
                    return isValidNearAnotherPlacement(die, x, y);
                }
            }
        }
        return false;
    }
    public boolean isValidPlacementColor(DiceColor color, int x, int y) {
        if (areCoordinatesValids(x, y)) {
            if (isCellEmptyVerbose(x, y) &&
                    isValidNearColor(new Dice(color), x, y)) {
                try {
                  if (!getCell(x,y).getColorConstrain().equals(color)){
                      return false;
                  }
                } catch (NoSuchConstrainException e){
                    //
                }
                if (getDiceNum() == 0) { // If this is the first placement we can place it not near anything else.
                    return isValidBorderPlacement(x, y);
                } else {
                    return isValidNearAnotherPlacement(new Dice(color), x, y);
                }
            }
        }
        return false;
    }

    public boolean isValidPlacementVerbose(Dice die, int x, int y) throws GameException {
        if (!areCoordinatesValids(x, y)) {
            throw new GameException("Invalid Coordinates");
        }

        if (!isCellEmpty(x, y)) {
            throw new GameException("The cell is not empty");
        }

        if (!isValidNearShade(die, x, y)) {
            throw new GameException("Placement would violate shade restrictions");
        }

        if (!isValidNearColor(die, x, y)) {
            throw new GameException("Placement would violate color restrictions");
        }

        if (!isPlaceable(die, x, y)) {
            throw new GameException("The die is not playable in this cell due to restriction");
        }

        if (getDiceNum() == 0) {
            if (!isValidBorderPlacement(x, y)) {
                throw new GameException("Since the grid is empty you should place the first die in the border");
            } else {
                return true;
            }
        } else {
            if (!isValidNearAnotherPlacement(die, x, y)) {
                throw new GameException("The die should be placed near another die!");
            } else {
                return true;
            }
        }

    }

    /**
     * Get a List of suggested placements place where you can place a
     * @param die
     */
    public List<Coord> suggestPlacements(Dice die) {
        List<Coord> possibilePlacements = new CopyOnWriteArrayList<>();
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                try {
                    if (isCellEmpty(x, y) &&
                            isValidPlacement(die, x, y)) {
                        possibilePlacements.add(new Coord(x, y));
                    }
                } catch (GameException e) {
                    //we prevent the placement suggestion from throwing exceptions we don't care about
                }
            }
        }
        return possibilePlacements;
    }

    public List<Coord> suggestPlacementsByColor(DiceColor color) {
        List<Coord> possibilePlacements = new CopyOnWriteArrayList<>();
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                try {
                    if (isCellEmpty(x, y) &&
                            isValidPlacementColor(color, x, y)) {
                        possibilePlacements.add(new Coord(x, y));
                    }
                } catch (GameException e) {
                    //we prevent the placement suggestion from throwing exceptions we don't care about
                }
            }
        }
        return possibilePlacements;
    }

    /**
     * Place a die, obeying all existing rules
     */
    public boolean placeDie(Dice die, int x, int y) throws GameException {
        if (isValidPlacementVerbose(die, x, y)) {
            cells[x][y].setDie(die);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Place a die, checking only if the coordinates are valid
     */
    public boolean placeDieWithoutRestrictions(Dice die, int x, int y) {
        if (areCoordinatesValids(x, y)) {
            cells[x][y].setDie(die);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Peek a dice from a given cell
     */
    public Dice getDie(int x, int y) {
        try {
            if (areCoordinatesValids(x, y) && !isCellEmpty(x, y)) {
                return cells[x][y].getDie();
            }
        } catch (GameException e) {
            //If the cell is empty an exception is thrown and we continue
        }
        return null;
    }

    public GridCell getCell(int x, int y) {
        if (areCoordinatesValids(x, y)) {
            return cells[x][y];
        }
        return null;
    }

    /**
     * Remove and return a dice from a given cell
     */
    public Dice removeDie(int x, int y) {
        if (areCoordinatesValids(x, y) && !isCellEmpty(x, y)) {
            return cells[x][y].removeDie();
        }
        return null;
    }

    public String getCardName() {
        return cardName;
    }

    public int getCardId() {
        return cardId;
    }

    public int getMatchId() {
        return matchId;
    }

    public int getFavorPoints() {
        return favorPoints;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean colored) {
        StringBuilder rep = new StringBuilder();
        rep.append("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
        rep.append("xx  " + this.getCardName() + "\t" + this.getFavorPoints() +  "  xx\n");
        rep.append("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
        rep.append(" * |");
        for (int idx_c = 0; idx_c < cells.length; idx_c++) {
            rep.append("  " + idx_c + "  ");
        }
        rep.append("\n---|----+----+----+----+----|\n");
        for (int y = 0; y < cells[0].length; y++) {
            rep.append(" " + y + " |");
            for (int x = 0; x < cells.length; x++) {
                rep.append(cells[x][y].toString(colored));
                rep.append("|");
            }
            rep.append("\n   |----+----+----+----+----|\n");
        }
        rep.deleteCharAt(rep.length() - 1);
        return rep.toString();
    }
}
