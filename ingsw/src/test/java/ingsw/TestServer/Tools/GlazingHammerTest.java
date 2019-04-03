package ingsw.TestServer.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.GameBoard;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Tools.GlazingHammer;
import ingsw.Server.Utility.GameException;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

import static ingsw.Server.Dice.DiceColor.*;

public class GlazingHammerTest {

    GameBoardMocker gbMocker = new GameBoardMocker();
    GameBoard gbTest0 = gbMocker.getGameBoard(0, 4);
    GameBoard gbTest1 = gbMocker.getGameBoard(1, 4);
    GameBoard gbTest2 = gbMocker.getGameBoard(2, 4);
    ToolActionParameter tap = new ToolActionParameter();
    DicePool dicePoolTest = new DicePool();
    Dice testDice1 = new Dice(1, BLUE);
    Dice testDice2 = new Dice(3, YELLOW);

    @Test
    public void testInvalidGlazingHammer() throws NoMoreBagElementException {
        gbTest2.getTools().set(0, new GlazingHammer());
        tap.setPickedTool(7);
        gbTest2.getCurrentPlayer().setToken(3);
        tap.setWorkOnDicePool(true);
        gbTest2.getDicePool().addDice(new Dice(1, RED));
        gbTest2.getDicePool().addDice(new Dice(2, BLUE));
        gbTest2.getDicePool().addDice(new Dice(3, GREEN));
        //testing not able to use before second turn
        try {
            gbTest2.getTools().get(0).use(tap, gbTest2);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertTrue(true);
        }
        Assert.assertEquals(3, gbTest2.getCurrentPlayer().getToken());
        Assert.assertEquals(1, gbTest2.getDicePool().peekDice(0).getValue());
        Assert.assertEquals(RED, gbTest2.getDicePool().peekDice(0).getColor());
        Assert.assertEquals(2, gbTest2.getDicePool().peekDice(1).getValue());
        Assert.assertEquals(BLUE, gbTest2.getDicePool().peekDice(1).getColor());
        Assert.assertEquals(3, gbTest2.getDicePool().peekDice(2).getValue());
        Assert.assertEquals(GREEN, gbTest2.getDicePool().peekDice(2).getColor());
    }

    @Test
    public void testValidGlazingHammer() throws NoMoreBagElementException {
        gbTest2.getTools().set(0, new GlazingHammer());
        tap.setPickedTool(7);
        gbTest2.getCurrentPlayer().setToken(3);
        tap.setWorkOnDicePool(true);
        gbTest2.getDicePool().addDice(new Dice(1, RED));
        gbTest2.getDicePool().addDice(new Dice(2, BLUE));
        gbTest2.getDicePool().addDice(new Dice(3, GREEN));

        gbTest2.getCurrentPlayer().turnPlayed = 1;

        Assert.assertEquals(0, gbTest2.getTools().get(0).use(tap, gbTest2));
        Assert.assertEquals(2, gbTest2.getCurrentPlayer().getToken());
        Assert.assertEquals(RED, gbTest2.getDicePool().peekDice(0).getColor());
        Assert.assertEquals(BLUE, gbTest2.getDicePool().peekDice(1).getColor());
        Assert.assertEquals(GREEN, gbTest2.getDicePool().peekDice(2).getColor());
    }

}
