package ingsw.TestServer.Dice;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.InvalidDiceValueException;
import org.junit.*;

import static ingsw.Server.Dice.DiceColor.RED;


public class DiceTest {

    @Test(expected = InvalidDiceValueException.class)
    public void testIncrease() throws InvalidDiceValueException {
        Dice testDice = new Dice(5,RED);
        testDice.increase();
        Assert.assertEquals(6,testDice.getValue());
        testDice.increase();
    }
    @Test(expected = InvalidDiceValueException.class)
    public void testDecrease() throws InvalidDiceValueException {
        Dice testDice = new Dice(2,RED);
        testDice.decrease();
        Assert.assertEquals(1,testDice.getValue());
        testDice.decrease();
    }
    @Test
    public void testOppositeSide() {
        Dice testDice = new Dice(2,RED);
        testDice.oppositeSide();
        Assert.assertEquals(5,testDice.getValue());
    }
    @Test
    public void testSetValue() {
        Dice testDice = new Dice(2, RED);
        testDice.setValue(1);
        Assert.assertEquals(1, testDice.getValue());
        Assert.assertEquals(RED, testDice.getColor());
        testDice.setValue(3);
        Assert.assertEquals(3, testDice.getValue());
        Assert.assertEquals(RED, testDice.getColor());
    }
}
