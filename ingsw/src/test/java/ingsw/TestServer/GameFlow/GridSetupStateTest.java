package ingsw.TestServer.GameFlow;

import ingsw.Server.Dice.DiceBag;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowStates.GridSetupState;
import ingsw.Server.Grid.GridBag;
import ingsw.Server.Player;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

import static java.lang.Thread.sleep;

public class GridSetupStateTest {
    GameFlowHandler gameFH = new GameFlowHandler();
    GameBoardMocker gbMock = new GameBoardMocker();
    GameBoard gb = gbMock.getGameBoard(1, 4);

    public GridSetupStateTest() {
        gameFH.gameBoard = gb;
        gameFH.setState(gameFH.gridSetupState);
    }
    @Test
    public void testGridChoiche() {

        gameFH.gridsBag = new GridBag();

        Thread execution = new Thread(() -> gameFH.getCurrent().execute());
        execution.start();

        Thread playersGridPick = new Thread(() -> {
            Boolean executed = false;
            while (execution.isAlive() && !executed) {
                if (execution.getState() == Thread.State.TIMED_WAITING) {
                    for (Player p : gb.getActivePlayers()) {
                        p.setConnected(true);
                        ((GridSetupState) gameFH.getCurrent()).chooseGrid(p, p.getPossibleGrids()[0]);
                    }
                    executed = true;
                }
            }
        });
        playersGridPick.start();

        Thread playersAck = new Thread(() -> {
            while (execution.isAlive()) {
                if (execution.getState() == Thread.State.TIMED_WAITING) {
                    for (Player p : gb.getActivePlayers()) {
                        p.setConnected(true);
                        gameFH.getCurrent().ack(p);
                    }
                }
            }
        });
        playersAck.start();

        try {
            execution.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(gameFH.toolsSetupState, gameFH.getCurrent());

        for (Player p : gb.getActivePlayers()) {
            Assert.assertEquals(p.getPossibleGrids()[0], p.getGrid());
        }
    }
}
