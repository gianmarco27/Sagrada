package ingsw.TestServer.Utility;

import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Grid.InvalidJsonException;
import ingsw.Server.Player;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ScoreBoardTest {

    GameBoardMocker gbMock = new GameBoardMocker();
    GameBoard gbTest;
    Player player1;
    Player player2;
    Player player3;
    Player player4;

    @Test
    public void testCalculateScore() {
        gbTest = gbMock.getGameBoard(0, 4);
        gbTest.calculateScore();
        player1 = gbTest.getPlayers().get(0);
        player2 = gbTest.getPlayers().get(1);
        player3 = gbTest.getPlayers().get(2);
        player4 = gbTest.getPlayers().get(3);

        Assert.assertEquals(52, gbTest.getScoreBoard().getPlayerOrder().get(player1).intValue()); //52 obj  0 dadi mancanti  0 favortoken
        Assert.assertEquals(46, gbTest.getScoreBoard().getPlayerOrder().get(player2).intValue()); //46 obj  0 dadi mancanti  0 favortoken
        Assert.assertEquals(27, gbTest.getScoreBoard().getPlayerOrder().get(player3).intValue()); //23 obj  0 dadi mancanti  4 favortoken
        Assert.assertEquals(27, gbTest.getScoreBoard().getPlayerOrder().get(player4).intValue()); //30 obj  3 dadi mancanti  0 favortoken
    }

    @Test
    public void emptyGridsScore() {
        gbTest = gbMock.getGameBoard(1, 4);
        gbTest.calculateScore();
        player1 = gbTest.getPlayers().get(0);
        player2 = gbTest.getPlayers().get(1);
        player3 = gbTest.getPlayers().get(2);
        player4 = gbTest.getPlayers().get(3);

        Assert.assertEquals(-20, gbTest.getScoreBoard().getPlayerOrder().get(player1).intValue()); //0 obj  20 dadi mancanti  0 favortoken
        Assert.assertEquals(-20, gbTest.getScoreBoard().getPlayerOrder().get(player2).intValue()); //0 obj  20 dadi mancanti  0 favortoken
        Assert.assertEquals(-20, gbTest.getScoreBoard().getPlayerOrder().get(player3).intValue()); //0 obj  20 dadi mancanti  0 favortoken
        Assert.assertEquals(-20, gbTest.getScoreBoard().getPlayerOrder().get(player4).intValue()); //0 obj  20 dadi mancanti  0 favortoken
    }

    @Test
    public void singlePlayerScoreTest() {
        gbTest = gbMock.getGameBoard(5, 1);
        gbTest.calculateScore();
        player1 = gbTest.getPlayers().get(0);
        System.out.println(player1.getGrid());
        Assert.assertEquals(40, gbTest.getScoreBoard().getPlayerOrder().get(player1).intValue()); //28 pub obj 12 priv obj  0 dadi mancanti  0 favortoken
    }

    @Test
    public void emptySinglePlayerScoreTest() {
        gbTest = gbMock.getGameBoard(4, 1);
        gbTest.calculateScore();
        player1 = gbTest.getPlayers().get(0);
        Assert.assertEquals(-60, gbTest.getScoreBoard().getPlayerOrder().get(player1).intValue()); //0 obj  20 dadi mancanti  0 favortoken
    }

    @Test
    public void scoreboardScoreTest() {
        gbTest = gbMock.getGameBoard(5, 1);
        gbTest.calculateScore();
        //testing score is calculated correctly including dice that are not the first of each round
        Assert.assertEquals(31, gbTest.getScoreBoard().getRoundTrackerScore());

    }
}
