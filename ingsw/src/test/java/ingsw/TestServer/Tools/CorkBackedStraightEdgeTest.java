package ingsw.TestServer.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.GameBoard;
import ingsw.Server.Grid.GridLoader;
import ingsw.Server.Grid.InvalidJsonException;
import ingsw.Server.Utility.GameException;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static ingsw.Server.Dice.DiceColor.BLUE;
import static ingsw.Server.Dice.DiceColor.YELLOW;

public class CorkBackedStraightEdgeTest {

    GameBoardMocker gbMocker = new GameBoardMocker();
    GameBoard gbTest0 = gbMocker.getGameBoard(0, 4);
    GameBoard gbTest1 = gbMocker.getGameBoard(1, 4);
    GameBoard gbTest2 = gbMocker.getGameBoard(2, 4);
    ToolActionParameter tap = new ToolActionParameter();
    DicePool dicePoolTest = new DicePool();
    Dice testDice1 = new Dice(1, BLUE);
    Dice testDice2 = new Dice(3, YELLOW);

    @Test
    public void testBreakingShadeConstrain() throws IOException, InvalidJsonException {
        gbTest1.getCurrentPlayer().setGrid(GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testVirtus.json"));
        gbTest1.getCurrentPlayer().setToken(3);
        gbTest1.getCurrentPlayer().getGrid().placeDie(testDice1, 4, 3);
        gbTest1.getCurrentPlayer().getGrid().placeDie(testDice2, 3, 3);

        gbTest1.getDicePool().addDice(testDice1);
        gbTest1.getDicePool().addDice(testDice2);
        gbTest1.getDicePool().addDice(testDice1);

        tap.setPickedTool(9);
        tap.setWorkOnDicePool(true);
        tap.setDicePoolTarget(0);
        tap.addCoords(0, 0);
        try {
            gbTest1.getTools().get(2).use(tap, gbTest1);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertTrue(true);
        }
        Assert.assertEquals(testDice1, gbTest1.getDicePool().getAllDice().get(0));
        Assert.assertEquals(null, gbTest1.getCurrentPlayer().getGrid().getDie(0, 0));
        Assert.assertEquals(testDice1, gbTest1.getCurrentPlayer().getGrid().getDie(4, 3));
        Assert.assertEquals(testDice2, gbTest1.getCurrentPlayer().getGrid().getDie(3, 3));
        Assert.assertEquals(3, gbTest1.getCurrentPlayer().getToken());
    }
    @Test
    public void testValidCorkBackedStraightEdge() throws IOException, InvalidJsonException {
        gbTest1.getCurrentPlayer().setGrid(GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testVirtus.json"));
        gbTest1.getCurrentPlayer().setToken(3);
        gbTest1.getCurrentPlayer().getGrid().placeDie(testDice1, 4, 3);
        gbTest1.getCurrentPlayer().getGrid().placeDie(testDice2, 3, 3);

        gbTest1.getDicePool().addDice(testDice1);
        gbTest1.getDicePool().addDice(testDice2);
        gbTest1.getDicePool().addDice(testDice1);

        tap.setPickedTool(9);
        tap.setWorkOnDicePool(true);
        tap.setDicePoolTarget(0);

        tap.addCoords(1, 0);
        Assert.assertEquals(0, gbTest1.getTools().get(2).use(tap, gbTest1));
        Assert.assertEquals(testDice1, gbTest1.getCurrentPlayer().getGrid().getDie(1, 0));
        Assert.assertEquals(testDice1, gbTest1.getCurrentPlayer().getGrid().getDie(4, 3));
        Assert.assertEquals(testDice2, gbTest1.getCurrentPlayer().getGrid().getDie(3, 3));
        Assert.assertEquals(testDice2, gbTest1.getDicePool().getAllDice().get(0));
        Assert.assertEquals(2, gbTest1.getCurrentPlayer().getToken());
    }

    @Test
    public void testBreakingColorConstrain() throws IOException, InvalidJsonException {
        gbTest1.getCurrentPlayer().setGrid(GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testVirtus.json"));
        gbTest1.getCurrentPlayer().setToken(3);
        gbTest1.getCurrentPlayer().getGrid().placeDie(testDice1, 4, 3);
        gbTest1.getCurrentPlayer().getGrid().placeDie(testDice2, 3, 3);

        gbTest1.getDicePool().addDice(testDice1);
        gbTest1.getDicePool().addDice(testDice2);
        gbTest1.getDicePool().addDice(testDice1);

        tap.setPickedTool(9);
        tap.setWorkOnDicePool(true);
        tap.setDicePoolTarget(2);

        tap.addCoords(4, 0);
        try {
            gbTest1.getTools().get(2).use(tap, gbTest1);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertTrue(true);
        }
        Assert.assertEquals(null, gbTest1.getCurrentPlayer().getGrid().getDie(4, 0));
        Assert.assertEquals(testDice1, gbTest1.getDicePool().getAllDice().get(0));
        Assert.assertEquals(3, gbTest1.getCurrentPlayer().getToken());
    }

    @Test public void testOtherDiceAreNotAffected() throws IOException, InvalidJsonException {
        gbTest1.getCurrentPlayer().setGrid(GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testVirtus.json"));
        gbTest1.getCurrentPlayer().setToken(3);
        gbTest1.getCurrentPlayer().getGrid().placeDie(testDice1, 4, 3);
        gbTest1.getCurrentPlayer().getGrid().placeDie(testDice2, 3, 3);

        gbTest1.getDicePool().addDice(testDice1);
        gbTest1.getDicePool().addDice(testDice2);
        gbTest1.getDicePool().addDice(testDice1);

        tap.setPickedTool(9);
        tap.setWorkOnDicePool(true);
        tap.setDicePoolTarget(1);

        tap.addCoords(1, 2);

        Assert.assertEquals(0, gbTest1.getTools().get(2).use(tap, gbTest1));
        Assert.assertEquals(testDice2, gbTest1.getCurrentPlayer().getGrid().getDie(1, 2));
        Assert.assertEquals(testDice1, gbTest1.getCurrentPlayer().getGrid().getDie(4, 3));
        Assert.assertEquals(testDice2, gbTest1.getCurrentPlayer().getGrid().getDie(3, 3));
        Assert.assertEquals(testDice1, gbTest1.getDicePool().getAllDice().get(0));
        Assert.assertEquals(2, gbTest1.getCurrentPlayer().getToken());
    }

}
