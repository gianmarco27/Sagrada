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
import static ingsw.Server.Dice.DiceColor.RED;
import static ingsw.Server.Dice.DiceColor.YELLOW;

public class CopperFoilBrushTest {

    GameBoardMocker gbMocker = new GameBoardMocker();
    GameBoard gbTest0 = gbMocker.getGameBoard(0, 4);
    GameBoard gbTest1 = gbMocker.getGameBoard(1, 4);
    GameBoard gbTest2 = gbMocker.getGameBoard(2, 4);
    ToolActionParameter tap = new ToolActionParameter();
    DicePool dicePoolTest = new DicePool();
    Dice testDice1 = new Dice(1, BLUE);
    Dice testDice2 = new Dice(3, YELLOW);
    Dice sameShade = new Dice(1, RED);
    Dice temp;

    @Test
    public void testValidCopperFoilBurnishes() throws IOException, InvalidJsonException {
        gbTest1.getCurrentPlayer().setGrid(GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testKaleidoscope.json"));
        gbTest1.getCurrentPlayer().setToken(3);
        gbTest1.getCurrentPlayer().getGrid().placeDie(testDice1, 2, 3);
        tap.setPickedTool(3);
        tap.addCoords(2, 3);
        tap.addCoords(0, 3);

        try {
            gbTest1.getTools().get(0).use(tap, gbTest1);
            Assert.assertTrue(true);
        } catch (GameException e) {
            Assert.assertTrue(false);
        }
        Assert.assertEquals(testDice1, gbTest1.getCurrentPlayer().getGrid().getDie(0, 3));
        Assert.assertEquals(null, gbTest1.getCurrentPlayer().getGrid().getDie(2, 3));
        Assert.assertEquals(2, gbTest1.getCurrentPlayer().getToken());
    }
    @Test
    public void testBreakingShadeRestrictions() throws IOException, InvalidJsonException {
        gbTest1.getCurrentPlayer().setGrid(GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testKaleidoscope.json"));
        gbTest1.getCurrentPlayer().setToken(3);
        gbTest1.getCurrentPlayer().getGrid().placeDieWithoutRestrictions(testDice1, 0, 3);
        gbTest1.getCurrentPlayer().getGrid().placeDieWithoutRestrictions(sameShade, 3, 1);

        tap.setPickedTool(3);
        tap.addCoords(0, 3);
        tap.addCoords(3, 1);
        //testing placement near a dice of the same shade
        try {
            gbTest1.getTools().get(0).use(tap, gbTest1);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertTrue(true);
        }
        Assert.assertEquals(testDice1, gbTest1.getCurrentPlayer().getGrid().getDie(0, 3));
        Assert.assertEquals(sameShade, gbTest1.getCurrentPlayer().getGrid().getDie(3, 1));
        Assert.assertEquals(3, gbTest1.getCurrentPlayer().getToken());
    }
    @Test
    public void testOtherDiceAreNotAffected() throws IOException, InvalidJsonException {
        gbTest1.getCurrentPlayer().setGrid(GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testKaleidoscope.json"));
        gbTest1.getCurrentPlayer().getGrid().placeDieWithoutRestrictions(testDice1, 0, 3);
        gbTest1.getCurrentPlayer().getGrid().placeDieWithoutRestrictions(sameShade, 3, 1);
        gbTest1.getCurrentPlayer().setToken(3);

        tap.setPickedTool(3);
        tap.addCoords(0, 3);
        tap.addCoords(2, 1);

        try {
            gbTest1.getTools().get(0).use(tap, gbTest1);
            Assert.assertTrue(true);
        } catch (GameException e) {
            Assert.assertTrue(false);
        }
        Assert.assertEquals(testDice1, gbTest1.getCurrentPlayer().getGrid().getDie(2, 1));
        Assert.assertEquals(sameShade, gbTest1.getCurrentPlayer().getGrid().getDie(3, 1));
        Assert.assertEquals(null, gbTest1.getCurrentPlayer().getGrid().getDie(1, 0));
        Assert.assertEquals(2, gbTest1.getCurrentPlayer().getToken());
    }
}
