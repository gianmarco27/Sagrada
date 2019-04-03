package ingsw.Server.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Player;
import ingsw.Server.Utility.GameException;

/**
 * This tool can be used in one single operation just by selecting it
 * It allows the user play a second die immediately after the first one
 * before we check if the player already placed a first Die and if it's not already at his second turn
 *
 *
 * Usage first run:  T-8 optional: D-N for single player
 */
public class RunningPliers extends GenericTool{

    public RunningPliers() {
        this(GameType.MULTIPLAYER);
    }

    public RunningPliers(GameType gameType) {
        super(gameType);
        this.toolName = "Running Pliers";
        this.description = "After your first turn,\n" +
        "immediately draft a die\n" +
        "Skip your next turn this round.";
        this.toolNumber = 8;
        this.payColor = DiceColor.RED;
    }

    @Override
    public boolean isUsable(ToolActionParameter tap, GameBoard gameBoard) {
        Player currentPlayer = gameBoard.getCurrentPlayer();

        if (!correctTool(tap.getPickedTool())) {
            return false;
        }

        if (!canPayPrice(tap, gameBoard)) {
            return false;
        }

        if (currentPlayer.turnPlayed == 1 || !currentPlayer.isDicePlayed()) {
            throw new GameException("You can't use this tool if you are on the 2° turn or have not placed a die");
        }

        return true;
    }

    @Override
    public int use(ToolActionParameter tap, GameBoard gameBoard) {

        if (!isUsable(tap, gameBoard)) {
            throw new GameException("The tool is not usable");
        }

        Player currentPlayer = gameBoard.getCurrentPlayer();

        currentPlayer.turnPlayed++;
        currentPlayer.setDicePlayed(false);

        payPrice(tap, gameBoard, true);

        return 0;
    }
}
