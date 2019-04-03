package ingsw.Server.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Player;
import ingsw.Server.Utility.GameException;
import ingsw.Server.Utility.RoundTracker;

/**
 * This tool can be used in one single operation just by selecting it, the target DicePool die and the target RoundTracker die
 * It allows the user to swap the drafted die with one from the RoundTracker
 *
 * Usage first run:  T-5 P-N R-N-N optional: D-N for single player
 */
public class LensCutter extends GenericTool {

    public LensCutter() {
        this(GameType.MULTIPLAYER);
    }

    public LensCutter(GameType gameType) {
        super(gameType);
        this.toolName = "Lens Cutter";
        this.description = "After selecting a die\n" +
                "swap it with a die from the\n" +
                "Round Track.";
        this.toolNumber = 5;
        this.payColor = DiceColor.GREEN;
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

        if (!tap.isWorkOnRoundTracker() ||
                !validRoundTracker(tap, gameBoard.getRoundTracker())) {
            return false;
        }

        return true;
    }

    @Override
    public int use(ToolActionParameter tap, GameBoard gameBoard) {

        if (!isUsable(tap, gameBoard)) {
            throw new GameException("The tool is not usable");
        }

        RoundTracker roundTracker = gameBoard.getRoundTracker();
        DicePool dicePool = gameBoard.getDicePool();

        Dice poolDice;
        try {
            poolDice = dicePool.peekDice(tap.getDicePoolTarget());
        } catch (NoMoreBagElementException e) {
            return -1;
        }

        Dice swapped = roundTracker.swapDice(tap.getRoundTrackerTargetRound(), tap.getRoundTrackerTargetOption(), poolDice);
        try {
            dicePool.swapDice(swapped, tap.getDicePoolTarget());
        } catch (NoMoreBagElementException e) {
            return -1;
        }

        payPrice(tap, gameBoard);

        return 0;
    }
}
