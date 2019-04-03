package ingsw.TestServer;

import ingsw.Server.GameBoard;
import ingsw.Server.Player;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

public class GameBoardTest {
    GameBoardMocker gbMock = new GameBoardMocker();
    GameBoard gb = gbMock.getGameBoard(1, 4);
    Player currentPlayer;

    public GameBoardTest() {
        currentPlayer = gb.getCurrentPlayer();
    }

    @Test
    public void testRemovePlayer() {
        gb.removePlayer(currentPlayer);
        Assert.assertEquals(false,gb.getPlayers().contains(currentPlayer));
        }

    @Test
    public void testGetPlayer() {
        Assert.assertEquals(currentPlayer, gb.getPlayer(currentPlayer.getName()));
    }
}
