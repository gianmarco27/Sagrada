package ingsw.TestServer;

import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Utility.GameException;
import org.junit.Assert;
import org.junit.Test;
import ingsw.Server.Player;

public class PlayerTest {

    @Test
    public void testTokenCorrectly(){
        Player testPlayer = new Player("tester");
        testPlayer.setToken(5);
        testPlayer.spendToken(3);
        Assert.assertEquals(2, testPlayer.getToken());
    }
    @Test(expected = GameException.class)
    public void testTokenException() {//missing exceptions in code
        Player testPlayer = new Player("tester");
        testPlayer.setToken(1);
        testPlayer.spendToken(3);
        Assert.assertEquals(1, testPlayer.getToken());
    }

    @Test
    public void testSameNameAreEquals() {//missing exceptions in code
        Player testPlayer1 = new Player(new String("tester1"));
        Player testPlayer2 = new Player(new String("tester2"));
        Player testPlayer3 = new Player(new String("tester1"));
        Assert.assertEquals(testPlayer1, testPlayer3);
        Assert.assertNotEquals(testPlayer1, testPlayer2);
    }
}
