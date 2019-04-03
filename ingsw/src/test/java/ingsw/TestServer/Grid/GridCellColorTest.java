package ingsw.TestServer.Grid;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Grid.GridCellColor;
import ingsw.Server.Utility.GameException;
import org.junit.Assert;
import org.junit.Test;
import ingsw.Server.Grid.NoSuchConstrainException;

import static ingsw.Server.Dice.DiceColor.BLUE;
import static ingsw.Server.Dice.DiceColor.RED;

public class GridCellColorTest {
    GridCellColor testCell = new GridCellColor(RED);
    Dice rightDice = new Dice(1, RED);
    Dice wrongDice = new Dice(5, BLUE);

    @Test
    public void testColorConstrain() throws NoSuchConstrainException {
        Assert.assertEquals(RED, testCell.getColorConstrain());
    }

    @Test (expected = NoSuchConstrainException.class)
    public void testShadeConstrain() throws NoSuchConstrainException {
        testCell.getShadeConstrain();
    }

    @Test
    public void testConstrain() {
        Assert.assertEquals(true, testCell.hasColorConstrain());
        Assert.assertEquals(false, testCell.hasShadeConstrain());
    }

    @Test
    public void testPlacing() {
        try{
            testCell.isPlaceable(wrongDice);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertTrue(true);
        }
        Assert.assertEquals(true, testCell.isPlaceable(rightDice));
    }
}
