
package ingsw.TestServer.Utility;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Utility.RoundTracker;
import org.junit.*;


public class RoundTrackerTest {

    Dice testDice1 = new Dice(DiceColor.RED);
    Dice testDice2 = new Dice(DiceColor.BLUE);

    @Test
    public void testPlaceDice() {
        RoundTracker rtTest = new RoundTracker();
        rtTest.placeDice(5,testDice1);
        Assert.assertEquals(testDice1,rtTest.peekPosition(5,0));
    }

    @Test
    public void testPeekPosition() {
        RoundTracker rtTest = new RoundTracker();
        rtTest.placeDice(5,testDice1);
        rtTest.placeDice(5,testDice2);
        rtTest.placeDice(5,testDice1);
        Assert.assertEquals(null,rtTest.peekPosition(5,3));
        Assert.assertEquals(testDice1,rtTest.peekPosition(5,2));
        Assert.assertEquals(testDice2,rtTest.peekPosition(5,1));
        Assert.assertEquals(testDice1,rtTest.peekPosition(5,0));
    }

    @Test
    public void testEmptyPosition() {
        RoundTracker rtTest = new RoundTracker();
        rtTest.placeDice(5,testDice1);
        rtTest.emptyPosition(5,0);
        Assert.assertEquals(null,rtTest.peekPosition(5,0));
        }

    @Test
    public void testSwapDice() {
        Dice oldTestDice1;
        RoundTracker rtTest = new RoundTracker();
        rtTest.placeDice(5,testDice1);
        oldTestDice1 = rtTest.swapDice(5, 0, testDice2);
        Assert.assertEquals(testDice1, oldTestDice1);
        Assert.assertEquals(testDice2, rtTest.peekPosition(5,0));
    }

    @Test
    public void testPopDice() {
        RoundTracker rtTest = new RoundTracker();
        rtTest.placeDice(5,testDice1);
        Assert.assertEquals(testDice1,rtTest.popDice(5, 0));
        Assert.assertEquals(null,rtTest.peekPosition(5, 0));
        rtTest.placeDice(0,testDice1);
        rtTest.placeDice(0,testDice2);
        Assert.assertEquals(testDice1,rtTest.popDice(0, 0));
        Assert.assertEquals(testDice2,rtTest.peekPosition(0, 0));
    }

    @Test
    public void testPlacePosition(){
        RoundTracker rtTest = new RoundTracker();
        rtTest.placePosition(5, 3, testDice1);
        Assert.assertEquals(null,rtTest.peekPosition(5,0));
        rtTest.placeDice(9,testDice1);
        rtTest.placeDice(9,testDice1);
        rtTest.placeDice(10,testDice1);
        rtTest.placePosition(9,0, testDice2);
        Assert.assertEquals(testDice2,rtTest.peekPosition(9,0));
        Assert.assertEquals(testDice1,rtTest.peekPosition(9,1));
        Assert.assertEquals(null,rtTest.peekPosition(10,0));
    }
}
