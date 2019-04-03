package ingsw.Server.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Player;
import ingsw.Server.Utility.GameException;

import static java.lang.Math.abs;

/**
 * This tool can be used in one single operation just by selecting it, the target DicePool die and insert the wished value to change it into
 * It allows the user to increase or decrease the dice value by one in the dicepool (this allows wider interactions)
 *
 * Usage first run:  T-1 P-N V-N optional: D-N for single player
 */
public class GrozingPliers extends GenericTool {

    public GrozingPliers() {
        this(GameType.MULTIPLAYER);
    }

    public GrozingPliers(GameType gameType) {
        super(gameType);
        this.toolName = "Grozing Pliers";
        this.description = "After selecting a die,\n" +
                "increase or decrease the value of the drafted die by 1\n" +
                "1 may not change to 6, or 6 to 1.";
        this.toolNumber = 1;
        this.payColor = DiceColor.PURPLE;
    }

    @Override
    public boolean isUsable(ToolActionParameter tap, GameBoard gameBoard) {

        if (!correctTool(tap.getPickedTool())) {
            return false;
        }

        if (!canPayPrice(tap, gameBoard)) {
            return false;
        }

        if (!tap.isWorkOnDicePool() ||
                !validDicePoolTarget(tap.getDicePoolTarget(), gameBoard.getDicePool())) {
            return false;
        }

        Dice toChange;
        try {
            toChange = peekDicePoolTarget(tap.getDicePoolTarget(), gameBoard.getDicePool());
        } catch (NoMoreBagElementException e) {
            return false;
        }

        int wishedValue = tap.getValueChangeTo();

        if (wishedValue > Dice.MAX_VALUE || wishedValue < Dice.MIN_VALUE) {
            throw new GameException("You should wish to change to a value between 1 and 6");
        }

        if (abs(toChange.getValue() - wishedValue) != 1) {
            throw new GameException("The wished value differ is more than 1 from the original");
        }

        return true;
    }

    @Override
    public int use(ToolActionParameter tap, GameBoard gameBoard) {

        if (!isUsable(tap, gameBoard)) {
            throw new GameException("The tool is not usable");
        }

        Dice toChange;

        try {
            toChange = peekDicePoolTarget(tap.getDicePoolTarget(), gameBoard.getDicePool());
        } catch (NoMoreBagElementException e) {
            return -1;
        }

        payPrice(tap, gameBoard);

        toChange.setValue(tap.getValueChangeTo());

        return 0;
    }
}
