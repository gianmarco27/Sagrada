package ingsw.TestServer.Dice;

import ingsw.Server.*;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceBag;
import ingsw.Server.Dice.DiceColor;
import org.junit.*;


public class BagTest {
    @Test
    public void testExtract() throws NoMoreBagElementException {
        DiceBag testBag = new DiceBag();
        testBag.extractDice();
        Assert.assertEquals(89, testBag.diceLeft());
    }
    @Test
    public void testAdd(){
        Dice testDice = new Dice(DiceColor.RED);
        DiceBag testBag = new DiceBag();
        testBag.addDice(testDice);
        Assert.assertEquals(91, testBag.diceLeft());
    }

    @Test(expected=NoMoreBagElementException.class)
    public void testNoMoreBagElementException() throws NoMoreBagElementException {
        DiceBag testBag = new DiceBag();
        testBag.extractDice(91);
    }

}
