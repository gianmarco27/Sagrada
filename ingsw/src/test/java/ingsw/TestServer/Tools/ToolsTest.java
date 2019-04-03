package ingsw.TestServer.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.GameBoard;
import ingsw.Server.Grid.GridLoader;
import ingsw.Server.Grid.InvalidJsonException;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Tools.*;
import ingsw.Server.Utility.GameException;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static ingsw.Server.Dice.DiceColor.*;

public class ToolsTest {
    GameBoardMocker gbMocker = new GameBoardMocker();
    GameBoard gbTest0 = gbMocker.getGameBoard(0, 4);
    GameBoard gbTest1 = gbMocker.getGameBoard(1, 4);
    GameBoard gbTest2 = gbMocker.getGameBoard(2, 4);
    GameBoard gbTest3 = gbMocker.getGameBoard(5, 1);
    ToolActionParameter tap = new ToolActionParameter();
    DicePool dicePoolTest = new DicePool();
    Dice testDice1 = new Dice(1, BLUE);
    Dice testDice2 = new Dice(3, YELLOW);
    Dice testDice3 = new Dice(2, GREEN);

    @Test
    public void testCustomComparator() {
        Tool t1 = new EglomiseBrush();
        Tool t2 = new EglomiseBrush();
        Tool t3 = new FluxRemover();
        Assert.assertTrue((t1.equals(t2)));
        Assert.assertFalse((t3.equals(t1)));
    }

    @Test
    public void testMultiToolSpending() {
        //pick the player that has 4 tokens in the mocked gameboard
        while (gbTest0.getCurrentPlayer().getToken() < 3) {
            gbTest0.nextPlayer();
        }
        tap.setRoundTrackerTargetRound(0);
        tap.setRoundTrackerTargetOption(0);
        tap.setDicePoolTarget(0);
        tap.setWorkOnDicePool(true);
        tap.setWorkOnRoundTracker(true);
        tap.setPickedTool(5);
        dicePoolTest = gbTest0.getDicePool();
        dicePoolTest.addDice(testDice1);
        dicePoolTest.addDice(testDice2);
        //check the correct consuption of tokens price:1
        Assert.assertEquals(0, gbTest0.getTools().get(0).use(tap, gbTest0));
        Assert.assertEquals(3, gbTest0.getCurrentPlayer().getToken());
        //check the correct consuption of tokens price:2
        Assert.assertEquals(0, gbTest0.getTools().get(0).use(tap, gbTest0));
        Assert.assertEquals(1, gbTest0.getCurrentPlayer().getToken());
        try {
            //check an exception is thrown if the tool is not affordable, cost:2 , actual tokens:1
            gbTest0.getTools().get(0).use(tap, gbTest0);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertTrue(true);
        }
        //check that the token remains unspent
        Assert.assertEquals(1, gbTest0.getCurrentPlayer().getToken());
        //check that the dice are unaffected since the move was not permitted
        try {
            Assert.assertEquals(testDice1, dicePoolTest.peekDice(0));
        } catch (NoMoreBagElementException e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testInvalidSingleToolSpending() {

        tap.setRoundTrackerTargetRound(0);
        tap.setRoundTrackerTargetOption(0);
        tap.setDicePoolTarget(0);
        tap.setWorkOnDicePool(true);
        tap.setWorkOnRoundTracker(true);
        tap.setPickedTool(5);
        tap.setPayDicePool(1);
        dicePoolTest = gbTest3.getDicePool();
        dicePoolTest.addDice(testDice1);
        dicePoolTest.addDice(testDice2);
        dicePoolTest.addDice(testDice3);

        //check that a tool can only be paid with its color
        try {
            gbTest3.getTools().get(0).use(tap, gbTest0);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertTrue(true);
        }
        Assert.assertEquals(testDice2, gbTest3.getDicePool().getAllDice().get(1));
    }

    @Test
    public void testValidSingleToolSpending() {
        tap.setRoundTrackerTargetRound(0);
        tap.setRoundTrackerTargetOption(0);
        tap.setDicePoolTarget(0);
        tap.setWorkOnDicePool(true);
        tap.setWorkOnRoundTracker(true);
        tap.setPickedTool(5);
        tap.setPayDicePool(2);
        dicePoolTest = gbTest3.getDicePool();
        dicePoolTest.addDice(testDice1);
        dicePoolTest.addDice(testDice2);
        dicePoolTest.addDice(testDice3);
        //check the correct consuption of Dice and Tool
        Assert.assertEquals(0, gbTest3.getTools().get(0).use(tap, gbTest3));
        try {
            Assert.assertEquals(null, gbTest3.getDicePool().getAllDice().get(2));
            Assert.assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            Assert.assertTrue(true);
        }

        dicePoolTest.addDice(testDice3);

        //check the tool isn't usable anymore
        try {
            //check the tool isn't usable anymore
            gbTest3.getTools().get(0).use(tap, gbTest3);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertTrue(true);
        }
        //check that the dice remains unspent
        Assert.assertEquals(testDice3, gbTest3.getDicePool().getAllDice().get(2));
        //check that the dice are unaffected since the move was not permitted
        try {
            Assert.assertNotEquals(testDice1, dicePoolTest.peekDice(0)); //in position 0 we still will have a RT Dice
        } catch (NoMoreBagElementException e) {
            Assert.assertTrue(false);
        }
    }
}
