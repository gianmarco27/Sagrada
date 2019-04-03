package ingsw.Server.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Player;
import ingsw.Server.Utility.GameException;

/**
 * This tool can be used in one single operation just by selecting it on your second turn before drafting
 * before we check if the tool is usable by verifying the current player hasn't already played a die and is in his second turn
 * It allows the user to reroll all the values of the Dice in the DicePool
 *
 * Usage first run:  T-7 optional: D-N for single player
 */
public class GlazingHammer extends GenericTool {

    public GlazingHammer() {
        this(GameType.MULTIPLAYER);
    }

    public GlazingHammer(GameType gameType) {
        super(gameType);
        this.toolName = "Glazing Hammer";
        this.description = "Re-roll all dice in the Draft Pool\n" +
                "This may only be used on your second turn before\n" +
                "drafting.";
        this.toolNumber = 7;
        this.payColor = DiceColor.BLUE;
    }

    /**
     * @throws GameException
     */
    @Override
    public boolean isUsable(ToolActionParameter tap, GameBoard gameBoard) throws GameException {

        Player currentPlayer = gameBoard.getCurrentPlayer();

        if (!correctTool(tap.getPickedTool())) {
            return false;
        }

        if (alreadyPlayedDie(currentPlayer)) {
            return false;
        }

        if (useFromTurn(currentPlayer, 2)) {
            return false;
        }

        if (!canPayPrice(tap, gameBoard)) {
            return false;
        }

        return true;
    }

    /**
     * @throws GameException
     */
    @Override
    public int use(ToolActionParameter tap, GameBoard gameBoard) throws GameException {

        if (!isUsable(tap, gameBoard)) {
            throw new GameException("The tool is not usable");
        }

        DicePool dp = gameBoard.getDicePool();

        int poolSize = dp.getAllDice().size();

        for (int idx = 0; idx < poolSize; idx++) { // Reroll all Dice in the pool
            try {
                dp.peekDice(idx).rerollDice();
            } catch (NoMoreBagElementException e) {
                return -1;  // Shouldn't happen since we check beforehand
            }
        }

        payPrice(tap, gameBoard);
        return 0;
    }
}