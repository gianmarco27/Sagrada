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
 * This tool has two different stages, at the first run you have to choose the DicePool Die you want to operate on
 * the die gets rerolled and on the second run you can choose where to place it by selecting the GridCell,
 * if it's not placeable at all it will be returned to the Dicepool
 * It allows the user to reroll the value of a Die in the DicePool and then place it respecting all the constraints
 * before using it we check if the tool is usable by verifying the current player hasn't already played a die in this turn and
 * depending on the fact that this is the first or second call we check if the die is being placed correctly
 *
 * Usage first run:  T-6 P-N        optional: D-N for single player
 *       second run: T-6 [X-Y]
 */
public class FluxBrush extends GenericTool{

    Dice holder;

    public FluxBrush() {
        this(GameType.MULTIPLAYER);
    }

    public FluxBrush(GameType gameType) {
        super(gameType);
        this.toolName = "Flux Brush";
        this.description = "After drafting,\n" +
        "re-roll the drafted die\n" +
        "if it cannot be placed,\n" +
        "return it to the Draft Pool.";
        this.toolNumber = 6;
        this.payColor = DiceColor.PURPLE;
    }

   @Override
    public boolean isUsable(ToolActionParameter tap, GameBoard gameBoard) throws GameException {

        Player currentPlayer = gameBoard.getCurrentPlayer();

        if (!correctTool(tap.getPickedTool())) {
            return false;
        }

        if (alreadyPlayedDie(currentPlayer)) {
            return false;
        }

        if (!tap.isCallBack()) { // First set of action

            if (!tap.isWorkOnDicePool() ||
                    !validDicePoolTarget(tap.getDicePoolTarget(), gameBoard.getDicePool())) {
                return false;
            }

            if (!canPayPrice(tap, gameBoard)) {
                return false;
            }

            // We preemptively empty the holder variable to account
            // if someone disconnected during a previous callback execution
            holder = null;
           return true;

        } else {
            try {
                List<Coord> coords = tap.getCoords();
                Grid playerGrid = currentPlayer.getGrid();

                if (!validDiceCellNumber(tap, 1)) {
                    return false;
                }

                 if (!coordinatesValid(playerGrid, coords)) {
                     return false;
                 }

                 if (holder == null) {
                    return false;
                 }

                 if (!playerGrid.isValidPlacement(holder, coords.get(0).x, coords.get(0).y)) {
                    return false;
                 }

            } catch (GameException e) {
                throw new GameException(e.getMessage(),true);
            }
            return true;
        }
    }

    @Override
    public int use(ToolActionParameter tap, GameBoard gameBoard) {

        Player currentPlayer = gameBoard.getCurrentPlayer();
        Grid playerGrid = currentPlayer.getGrid();
        DicePool dp = gameBoard.getDicePool();
        List<Coord> coords = tap.getCoords();

        if (!isUsable(tap, gameBoard)) {
            throw new GameException("The tool is not usable");
        }

        if (!tap.isCallBack()) {
            // We reroll the die and tell the user the result


            Dice poolDice;
            try {
                poolDice = dp.peekDice(tap.getDicePoolTarget());

            } catch (NoMoreBagElementException e) {
                return -1;
            }

            poolDice.rerollDice();

            if (!isPlaceable(playerGrid, poolDice)) { // If the dice is not placeable we notify it to the user and go on!
                payPrice(tap, gameBoard);
                currentPlayer.setToolPlayed(true); // we manually set the user action related to tool true since this is an exceptional behaviour
                throw new GameException("We rerolled into " + poolDice + " which is not placeable, sorry kiddo");
            } else {
                holder = poolDice;
                payPrice(tap, gameBoard);
                throw new GameException("We rerolled into " + poolDice + " where do you want to place it?", true);
            }

        } else {
            dp.getAllDice().remove(holder);
            playerGrid.placeDie(holder, coords.get(0).x, coords.get(0).y);

            currentPlayer.setDicePlayed(true);
            holder = null;

        }

        return 0;
    }
}
