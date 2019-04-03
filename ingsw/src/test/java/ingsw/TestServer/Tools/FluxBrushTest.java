package ingsw.TestServer.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.GameBoard;
import ingsw.Server.Grid.GridLoader;
import ingsw.Server.Grid.InvalidJsonException;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Tools.FluxBrush;
import ingsw.Server.Utility.GameException;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static ingsw.Server.Dice.DiceColor.BLUE;
import static ingsw.Server.Dice.DiceColor.YELLOW;

public class FluxBrushTest {

    GameBoardMocker gbMocker = new GameBoardMocker();
    GameBoard gbTest0 = gbMocker.getGameBoard(0, 4);
    GameBoard gbTest1 = gbMocker.getGameBoard(1, 4);
    GameBoard gbTest2 = gbMocker.getGameBoard(2, 4);
    ToolActionParameter tap = new ToolActionParameter();
    DicePool dicePoolTest = new DicePool();
    Dice testDice1 = new Dice(1, BLUE);
    Dice testDice2 = new Dice(3, YELLOW);
    DiceColor tempColor;
    @Test
    public void testValidUse() throws IOException, InvalidJsonException, NoMoreBagElementException {
        gbTest2.getTools().set(0, new FluxBrush());
        tap.setPickedTool(6);
        tap.setWorkOnDicePool(true);
        tap.setDicePoolTarget(0);
        gbTest2.getDicePool().addDice(testDice1);
        gbTest2.getDicePool().addDice(testDice2);
        gbTest2.getDicePool().addDice(testDice1);
        tap.addCoords(0, 0);
        tempColor = gbTest2.getDicePool().peekDice(0).getColor();
        gbTest2.getCurrentPlayer().setGrid(GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testGrid.json"));
        gbTest2.getCurrentPlayer().setToken(3);

        try {
            gbTest2.getTools().get(0).use(tap, gbTest2);
            Assert.assertTrue(false);
        } catch (GameException e) {
            if (e.isCallBack()) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        }
        tap.setCallBack(true);
        Assert.assertEquals(0, gbTest2.getTools().get(0).use(tap, gbTest2));
        Assert.assertEquals(2, gbTest2.getCurrentPlayer().getToken());
        // Check rerolling it preserves the color
        Assert.assertEquals(tempColor, gbTest2.getCurrentPlayer().getGrid().getDie(0, 0).getColor());
    }

    @Test
    public void testInvalidUse() throws IOException, InvalidJsonException {
        gbTest2.getTools().set(0, new FluxBrush());
        tap.setPickedTool(6);
        tap.setWorkOnDicePool(true);
        tap.setDicePoolTarget(0);
        gbTest2.getDicePool().addDice(testDice1);
        gbTest2.getDicePool().addDice(testDice2);
        gbTest2.getDicePool().addDice(testDice1);
        gbTest2.getCurrentPlayer().setGrid(GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testGrid.json"));
        gbTest2.getCurrentPlayer().setToken(3);

        try {
            gbTest2.getTools().get(0).use(tap, gbTest2);
            Assert.assertTrue(false);
        } catch (GameException e) {
            if (e.isCallBack()) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        }
        tap.setCallBack(true);
        tap.addCoords(1,1);

        try {
            gbTest2.getTools().get(0).use(tap, gbTest2);
            Assert.assertTrue(false);
        } catch (GameException e) {
            if (e.isCallBack()) {
                Assert.assertTrue(false);
            } else {
                Assert.assertTrue(true);
            }
        }
        Assert.assertEquals(2, gbTest2.getCurrentPlayer().getToken());
        Assert.assertEquals(null, gbTest2.getCurrentPlayer().getGrid().getDie(1, 1));
    }

}
