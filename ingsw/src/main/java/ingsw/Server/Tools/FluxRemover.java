package ingsw.Server.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceBag;
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
 * the die is replaced by a fresh draft from the DiceBag with a set color and on the second run you can choose its value
 * and where to place it by selecting the GridCell
 * if it's not placeable at all it will be returned to the Dicepool with the wishedValue
 * before we check if the tool is usable by verifying the current player hasn't already played a die in this turn and
 * depending on the fact that this is the first or second call we check if its value respects game rules
 * It allows the user to reroll the color of a Die in the DicePool and change its value to a desired one,
 * then to place it respecting all the constraints
 *
 * Usage first run:  T-11 P-N           optional: D-N for single player
 *       second run: T-11 V-N [X-Y]
 *
 */
public class FluxRemover extends GenericTool {

    DiceColor colorHolder;
    Dice oldDice;

    public FluxRemover() {
        this(GameType.MULTIPLAYER);
    }

    public FluxRemover(GameType gameType) {
        super(gameType);
        this.toolName = "Flux Remover";
        this.description = "After drafting, return the die to the\n" +
        "Dice Bag and pull 1 die from the bag\n" +
        "Choose a value and place the new die,\n" +
        "obeying all placement restrictions, or\n" +
        "return it to the Draft Pool.";
        this.toolNumber = 11;
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
            // We preemptively empty the colorHolder variable to account
            // if someone disconnected during a previous callback execution
            colorHolder = null;
            oldDice = null;
            return true;

        } else {
            try {
                List<Coord> coords = tap.getCoords();
                Grid playerGrid = currentPlayer.getGrid();

                int wishedValue = tap.getValueChangeTo();

                if (wishedValue > Dice.MAX_VALUE || wishedValue < Dice.MIN_VALUE) {
                    throw new GameException("Error in the tool callback parameters");
                }

                if (colorHolder == null) {
                    return false;
                }

                if (!validDiceCellNumber(tap, 1)) {

                    throw new GameException("Error in the tool callback parameters");
                }

                if (!coordinatesValid(playerGrid, coords)) {

                    throw new GameException("Error in the tool callback parameters");
                }

                if (!playerGrid.isValidPlacement(new Dice(wishedValue, colorHolder), coords.get(0).x, coords.get(0).y)) {
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

            Dice poolDice;
            try {
                poolDice = oldDice = dp.peekDice(tap.getDicePoolTarget());
            } catch (NoMoreBagElementException e) {
                return -1;
            }

            DiceBag db = gameBoard.getDiceBag();
            // We add to the dice bag a die of the same color to keep a fair probability for the next extraction
            db.addDice(new Dice(poolDice.getColor()));

            try {
                this.colorHolder = db.extractDice().getColor(); // Take a die and save the color
            } catch (NoMoreBagElementException e) {
                return -1;
            }

            if (!isPlaceableColor(playerGrid, poolDice.getColor())) { // If the dice is not placeable we notify it to the user and go on!
                try {
                    dp.swapDice(new Dice(colorHolder), dp.getAllDice().indexOf(oldDice));
                } catch (NoMoreBagElementException e) {
                    throw new GameException("Ops, something unexpected happened!");
                }
                payPrice(tap, gameBoard);
                currentPlayer.setToolPlayed(true); // we manually set the user action related to tool true since this is an exceptional behaviour
                throw new GameException("We extracted a new die with color: " + colorHolder + " which is not placeable, sorry kiddo");
            } else {
                payPrice(tap, gameBoard);
                throw new GameException("We extracted a new die with color: " + colorHolder + " where do you want to place it?", true);
            }

        } else {

            Dice toPlace = new Dice(tap.getValueChangeTo(), colorHolder);

            if (playerGrid.suggestPlacements(toPlace).isEmpty()) {
                try {
                    dp.swapDice(toPlace, dp.getAllDice().indexOf(oldDice));
                } catch (NoMoreBagElementException e) {
                    throw new GameException("Ops, something unexpected happened!");
                }
                currentPlayer.setToolPlayed(true); // we manually set the user action related to tool true since this is an exceptional behaviour
                throw new GameException("The dice is unplaceable, returning it to the pool...");
            } else {
                dp.getAllDice().remove(oldDice);
                playerGrid.placeDie(toPlace, coords.get(0).x, coords.get(0).y);
                currentPlayer.setDicePlayed(true);
            }

            oldDice = null;
            colorHolder = null;
        }
        return 0;
    }
}
