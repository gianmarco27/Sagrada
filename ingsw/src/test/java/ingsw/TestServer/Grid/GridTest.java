package ingsw.TestServer.Grid;
import ingsw.Server.Dice.*;
import ingsw.Server.Grid.*;
import ingsw.Server.Utility.Coord;
import ingsw.Server.Utility.GameException;
import ingsw.TestServer.Mockers.MockupGrid;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class GridTest {

    MockupGrid mocker = new MockupGrid();
    Dice testDice1 = new Dice(3, DiceColor.RED);
    Dice testDice2 = new Dice(4, DiceColor.RED);
    Dice testDice3 = new Dice(2, DiceColor.YELLOW);

    @Test
    public void testValidPlacing() {
        Grid testGrid = mocker.getGrid(6);
        Assert.assertTrue(testGrid.placeDie(testDice1, 4, 3));
        Assert.assertEquals(testDice1, testGrid.getDie(4, 3));
        Assert.assertTrue(testGrid.placeDie(testDice3, 4, 2));
        Assert.assertTrue(testGrid.placeDie(testDice2, 3, 2));
    }

    @Test
    public void testForbiddenPlacing() {
        Grid testGrid = mocker.getGrid(6);
        try {
            testGrid.placeDie(testDice3, 4, 0);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertEquals("The placement doesn't respect color constraints", e.getMessage());
        }

        Assert.assertEquals(null, testGrid.getDie(4, 0));
        testGrid.placeDie(testDice1, 4, 3);

        try {// Overlapping forbidden
            testGrid.placeDie(testDice2, 4, 3);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertEquals("The cell is not empty", e.getMessage());
        }
        try {// Not Adjacent forbidden
            testGrid.placeDie(testDice2, 1, 1);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertEquals("The die should be placed near another die!", e.getMessage());
        }
        try {// Same color adjacent forbidden
            testGrid.placeDie(testDice2, 4, 2);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertEquals("Placement would violate color restrictions", e.getMessage());
        }
        try {// Y out of bound forbidden
            testGrid.placeDie(testDice3, 3, 4);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertEquals("Invalid Coordinates", e.getMessage());
        }
    }

    @Test
    public void testPlacingNoRestrictions() {
        Grid testGrid = mocker.getGrid(3);
        Assert.assertTrue(testGrid.placeDieWithoutRestrictions(testDice1, 3, 2));
        Assert.assertEquals(testDice1, testGrid.getDie(3, 2));
    }

    @Test
    public void testRemove() throws IOException, InvalidJsonException {
        Grid testGrid = mocker.getGrid(3);
        testGrid.placeDieWithoutRestrictions(testDice1, 3, 2);
        Assert.assertEquals(testDice1, testGrid.removeDie(3, 2));
        Assert.assertEquals(null, testGrid.getDie(3, 2));
    }

    @Test
    public void testBorderPlacement() {
        Grid testGrid = mocker.getGrid(0);
        testGrid.placeDieWithoutRestrictions(testDice1, 3, 2);
        testGrid.placeDieWithoutRestrictions(testDice1, 3, 0);
        testGrid.placeDieWithoutRestrictions(testDice1, 2, 1);
        Assert.assertEquals(true, testGrid.isValidBorderPlacement(0, 0));
        Assert.assertEquals(false, testGrid.isValidBorderPlacement(1, 1));
        Assert.assertEquals(true, testGrid.isValidBorderPlacement(4, 2));
    }

    @Test
    public void testDiagonalPlacement() {
        Grid testGrid = mocker.getGrid(0);
        testGrid.placeDieWithoutRestrictions(testDice1, 3, 2);
        testGrid.placeDieWithoutRestrictions(testDice1, 3, 0);
        testGrid.placeDieWithoutRestrictions(testDice1, 2, 1);
        Assert.assertEquals(true, testGrid.isValidDiagonalPlacement(testDice3, 3, 2));
        Assert.assertEquals(false, testGrid.isValidDiagonalPlacement(testDice3, 0, 0));
        Assert.assertEquals(false, testGrid.isValidDiagonalPlacement(testDice1, 0, 0));
    }

    @Test
    public void testAdjacentPlacement() {
        Grid testGrid = mocker.getGrid(0);
        testGrid.placeDieWithoutRestrictions(testDice1, 3, 2);
        testGrid.placeDieWithoutRestrictions(testDice1, 3, 0);
        testGrid.placeDieWithoutRestrictions(testDice1, 2, 1);
        Assert.assertEquals(true, testGrid.isValidAdjacentPlacement(testDice2, 2, 2));
        Assert.assertEquals(false, testGrid.isValidAdjacentPlacement(testDice2, 4, 3));
        Assert.assertEquals(false, testGrid.isValidAdjacentPlacement(testDice2, 0, 0));
        Assert.assertEquals(false, testGrid.isValidAdjacentPlacement(testDice2, 0, 3));

    }

    @Test
    public void testColorPlacement() {
        Grid testGrid = mocker.getGrid(0);
        testGrid.placeDieWithoutRestrictions(testDice1, 3, 2);
        testGrid.placeDieWithoutRestrictions(testDice1, 3, 0);
        testGrid.placeDieWithoutRestrictions(testDice1, 2, 1);
        Assert.assertEquals(false, testGrid.isValidNearColor(testDice2, 2, 2));
        Assert.assertEquals(true, testGrid.isValidNearColor(testDice3, 2, 2));
        }

    @Test
    public void testShadePlacement() {
        Grid testGrid = mocker.getGrid(0);
        testGrid.placeDieWithoutRestrictions(testDice1, 3, 2);
        testGrid.placeDieWithoutRestrictions(testDice1, 3, 0);
        testGrid.placeDieWithoutRestrictions(testDice1, 2, 1);
        Assert.assertEquals(false, testGrid.isValidNearShade(testDice1, 2, 2));
        Assert.assertEquals(true, testGrid.isValidNearShade(testDice3, 2, 2));
    }

    @Test(expected = InvalidJsonException.class)
    public void testBrokenGrid() throws IOException, InvalidJsonException {
        GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testBrokenGrid.json");
    }

    @Test
    public void testPlacerSuggestion() {
        // Empty grid with no restriction need to suggest all the border
        Grid testGrid1 = mocker.getGrid(0);
        Dice toPlace1 = new Dice(DiceColor.BLUE);
        String expectedSolution1 = "[0-0, 0-1, 0-2, 0-3, 1-0, 1-3, 2-0, 2-3, 3-0, 3-3, 4-0, 4-1, 4-2, 4-3]";
        Assert.assertEquals(expectedSolution1, testGrid1.suggestPlacements(toPlace1).toString());
        // Now that we have just a blue dice we should just be able to place another one diagonally
        testGrid1.placeDie(toPlace1, 0, 0);
        Dice toPlace2 = new Dice(DiceColor.BLUE);
        String expectedSolution2 = "[1-1]";
        Assert.assertEquals(expectedSolution2, testGrid1.suggestPlacements(toPlace2).toString());
        testGrid1.removeDie(0, 0);
        Dice toPlace3 = new Dice(DiceColor.RED);
        testGrid1.placeDieWithoutRestrictions(toPlace3, 3, 2);
        testGrid1.placeDieWithoutRestrictions(toPlace3, 3, 0);
        testGrid1.placeDieWithoutRestrictions(toPlace3, 2, 1);
        String expectedSolution3 = "[1-0, 1-2, 2-3, 4-1, 4-3]";
        Assert.assertEquals(expectedSolution3, testGrid1.suggestPlacements(toPlace3).toString());
        // Full grid should have 0 suggestion on where to place
        Grid testGrid2 = mocker.getGrid(1);
        Dice toPlace4 = new Dice(DiceColor.RED);
        Assert.assertEquals(new CopyOnWriteArrayList<Coord>(), testGrid2.suggestPlacements(toPlace4));
    }
}
