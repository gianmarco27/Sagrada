package ingsw.TestServer.GameFlow;

import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Objective.ObjectiveBag;
import ingsw.Server.Player;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

import java.lang.management.PlatformLoggingMXBean;

public class GameFlowHandlerTest {

    GameFlowHandler gameFH = new GameFlowHandler();
    GameBoardMocker gbMock = new GameBoardMocker();
    GameBoard gb = gbMock.getGameBoard(1, 4);

    Player timedOutPlayer;
    Player disconnectedPlayer ;

    public GameFlowHandlerTest() {
        gameFH.gameBoard = gb;
        gameFH.setState(gameFH.playerRegistrationState);
        gameFH.setGameType(GameType.MULTIPLAYER);
        gameFH.objectiveBag = new ObjectiveBag();
        timedOutPlayer = gameFH.gameBoard.getCurrentPlayer();
        for (Player p : gameFH.gameBoard.getPlayers())
            if (gameFH.gameBoard.getCurrentPlayer() != p)
                disconnectedPlayer = p;
    }

    @Test
    public void testGameEnd() {
        gameFH.setGameStatus(true);
        Assert.assertEquals(true, gameFH.isGameDone());
        gameFH.run();
    }

    @Test
    public void testConnection() {
        gameFH.disconnectPlayer(timedOutPlayer, true);
        gameFH.disconnectPlayer(disconnectedPlayer, false);
        gameFH.connectPlayer(timedOutPlayer);
        gameFH.connectPlayer(timedOutPlayer);
        Assert.assertNotEquals(null, gameFH.gameBoard.getQueues(timedOutPlayer));
        Assert.assertEquals(null, gameFH.gameBoard.getQueues(disconnectedPlayer));
        Assert.assertEquals(true, timedOutPlayer.isConnected());
    }

    @Test
    public void testDisconnection() {
        gameFH.disconnectPlayer(timedOutPlayer, true);
        gameFH.disconnectPlayer(timedOutPlayer, true);
        Assert.assertEquals(false, timedOutPlayer.isConnected());

        gameFH.setState(gameFH.gridSetupState);
        gameFH.disconnectPlayer(disconnectedPlayer, false);
        Assert.assertEquals(false, disconnectedPlayer.isConnected());
        Assert.assertEquals(disconnectedPlayer.getPossibleGrids()[0].getCardId(), disconnectedPlayer.getGrid().getCardId());
        Assert.assertEquals(null, gameFH.gameBoard.getQueues(disconnectedPlayer));

    }
}

