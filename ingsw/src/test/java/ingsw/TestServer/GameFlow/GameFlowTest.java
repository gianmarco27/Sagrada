package ingsw.TestServer.GameFlow;

import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameFlowStates.PlayerTurnBeginState;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;
import ingsw.Server.GameFlow.*;

public class GameFlowTest {
    GameFlowHandler gameFH = new GameFlowHandler();
    GameBoardMocker gbMock = new GameBoardMocker();
    GameBoard gb = gbMock.getGameBoard(1, 4);

    public GameFlowTest() {
        gameFH.gameBoard = gb;
    }

    @Test
    public void testSetter() {
        gameFH.setState(gameFH.playerTurnBeginState);
        Assert.assertEquals(gameFH.playerTurnBeginState, gameFH.getCurrent());
    }
}