package ingsw.TestServer.GameFlow;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceBag;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowStates.ToolActionState;
import ingsw.Server.GameFlow.GameType;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

import static ingsw.Server.Dice.DiceColor.BLUE;
import static ingsw.Server.Dice.DiceColor.RED;
import static java.lang.Thread.sleep;

public class ToolActionStateTest {
    GameFlowHandler gameFH = new GameFlowHandler();
    GameBoardMocker gbMock = new GameBoardMocker();
    GameBoard gb = gbMock.getGameBoard(1, 4);

    public ToolActionStateTest() {
        gameFH.gameBoard = gb;
        gameFH.setState(gameFH.toolActionState);
        gameFH.setGameType(GameType.MULTIPLAYER);
    }

    @Test
    public void testToolActionState() {
        gameFH.diceBag = new DiceBag();
        gb.getDicePool().addDice(new Dice(RED));
        gb.getDicePool().addDice(new Dice(BLUE));
        ToolActionParameter tap = new ToolActionParameter();
        gb.getCurrentPlayer().setToken(3);
        tap.setPickedTool(9);
        tap.addCoords(4, 0);
        tap.setDicePoolTarget(0);
        tap.setWorkOnDicePool(true);

        Thread execution = new Thread(() -> gameFH.getCurrent().execute());
        execution.start();

        Thread playerChoiche = new Thread(() -> {
            Boolean executed = false;
            while (execution.isAlive() && !executed) {
                if (execution.getState() == Thread.State.TIMED_WAITING) {
                    ((ToolActionState) gameFH.getCurrent()).useTap(tap);
                    executed = true;
                }
            }
        });
        playerChoiche.start();

        try {
            execution.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(gameFH.playerTurnBeginState, gameFH.getCurrent());

    }
}
