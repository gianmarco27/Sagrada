package ingsw.TestServer.GameFlow;

import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Objective.ObjectiveBag;
import ingsw.Server.Player;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

public class CommonObjSetupStateTest {
    GameFlowHandler gameFH = new GameFlowHandler();
    GameBoardMocker gbMock = new GameBoardMocker();
    GameBoard gb = gbMock.getGameBoard(1, 4);

    public CommonObjSetupStateTest() {
        gameFH.gameBoard = gb;
        gameFH.setState(gameFH.commonObjSetupState);
        gameFH.setGameType(GameType.MULTIPLAYER);
        gameFH.objectiveBag = new ObjectiveBag();
    }

    @Test
    public void testSetup() {

        Thread execution = new Thread(() -> gameFH.getCurrent().execute());
        execution.start();

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

        Assert.assertEquals(gameFH.turnHandlerState, gameFH.getCurrent());
    }
}
