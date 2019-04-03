package ingsw.TestServer.Utility;

import ingsw.Server.Player;
import org.junit.Assert;
import org.junit.Test;
import ingsw.Server.Utility.TurnHandler;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Arrays;
import java.util.List;


public class TurnHandlerTest {
    @Test
    public  void testPlayersInsertion(){
        List<Player> players = new CopyOnWriteArrayList<>();
        Player testPlayer1 = new Player("test1");
        Player testPlayer2 = new Player("test2");
        Player testPlayer3 = new Player("test3");
        players.add(testPlayer1);
        players.add(testPlayer2);
        players.add(testPlayer3);
        TurnHandler testHandler = new TurnHandler(players);
        List<Player> shuffledPlayers =  testHandler.getPlayersOrder();
        Assert.assertEquals(shuffledPlayers.get(0),testHandler.getCurrentPlayer());
    }

    @Test
    public  void testHasNextPlayer(){
        List<String> testPlayers = Arrays.asList("Federico", "Giovanni", "Gianmarco", "Pippo");
        for(int i = 0; i < 4 ; i++) {
            List<Player> playersInGame = new CopyOnWriteArrayList<>();
            for (int j = 0; j <= i; j++) {
                playersInGame.add(new Player(testPlayers.get(j)));
            }
            TurnHandler testHandler = new TurnHandler(playersInGame);
            List<Player> playersBeforeRotation = new CopyOnWriteArrayList<>(testHandler.getPlayersOrder());
            for (int j = 0; j <= i; j++) {
                Assert.assertEquals(playersBeforeRotation.get(j), testHandler.getCurrentPlayer());
                Assert.assertEquals(true, testHandler.hasNextPlayer());
            }
            for (int j = i; j > 0; j--){
                Assert.assertEquals(playersBeforeRotation.get(j), testHandler.getCurrentPlayer());
                Assert.assertEquals(true, testHandler.hasNextPlayer());
            }
            Assert.assertEquals(false, testHandler.hasNextPlayer());
            List<Player> playersAfterRotation = new CopyOnWriteArrayList<>(testHandler.getPlayersOrder());
            //System.out.println(playersBeforeRotation);
            //System.out.println(playersAfterRotation);
            for (int j = 0; j < i ; j++) {
                Assert.assertEquals(playersBeforeRotation.get(j), playersAfterRotation.get((j + i) % (i + 1)));
            }
        }
    }

    @Test
    public  void testHasNextSinglePlayer(){
        List<Player> players = new CopyOnWriteArrayList<>();
        Player testPlayer1 = new Player("test1");
        players.add(testPlayer1);
        TurnHandler testHandler = new TurnHandler(players);
        testHandler.hasNextPlayer();
        Assert.assertEquals(false,testHandler.hasNextPlayer());
        Assert.assertEquals(true,testHandler.hasNextPlayer());
        Assert.assertEquals(false,testHandler.hasNextPlayer());
        Assert.assertEquals(true,testHandler.hasNextPlayer());
    }
}
