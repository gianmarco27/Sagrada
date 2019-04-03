package ingsw.TestServer.Grid;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Grid.GridCellShade;
import ingsw.Server.Utility.GameException;
import org.junit.Assert;
import org.junit.Test;
import ingsw.Server.Grid.NoSuchConstrainException;
public class GridCellShadeTest {

    GridCellShade testCell = new GridCellShade(1);
    Dice rightDice = new Dice(1, DiceColor.RED);
    Dice wrongDice = new Dice(5, DiceColor.RED);

    @Test
    public void testShadeConstrain() {
        Assert.assertEquals(1, testCell.getShadeConstrain());
    }

    @Test (expected = NoSuchConstrainException.class)
    public void testColorConstrain() throws NoSuchConstrainException {
        testCell.getColorConstrain();
    }

    @Test
    public void testConstrain() {
        Assert.assertEquals(false, testCell.hasColorConstrain());
        Assert.assertEquals(true, testCell.hasShadeConstrain());
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
