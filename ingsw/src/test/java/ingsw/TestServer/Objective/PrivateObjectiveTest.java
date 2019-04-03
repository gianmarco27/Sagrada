package ingsw.TestServer.Objective;

import ingsw.Server.Grid.Grid;
import ingsw.Server.Objective.*;
import ingsw.TestServer.Mockers.MockupGrid;
import org.junit.Assert;
import org.junit.Test;

public class PrivateObjectiveTest {
    MockupGrid testMocker = new MockupGrid();

    @Test
    public void testShadesOfBlue() {
        PrivateObjective testObjective = new ShadesOfBlue();
        Grid testGrid = testMocker.getGrid(1);
        Assert.assertEquals(8, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(3);
        Assert.assertEquals(20, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(0);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
    }
    @Test
    public void testShadesOfRed(){
        PrivateObjective testObjective = new ShadesOfRed();
        Grid testGrid = testMocker.getGrid(1);
        Assert.assertEquals(4, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(3);
        //System.out.println(testGrid);
        Assert.assertEquals(7, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(0);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
    }
    @Test
    public void testShadesOfYellow(){
        PrivateObjective testObjective = new ShadesOfYellow();
        Grid testGrid = testMocker.getGrid(1);
        Assert.assertEquals(16, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(3);
        Assert.assertEquals(26, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(0);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
    }
    @Test
    public void testShadesOfGreen(){
        PrivateObjective testObjective = new ShadesOfGreen();
        Grid testGrid = testMocker.getGrid(1);
        Assert.assertEquals(12, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(3);
        Assert.assertEquals(8, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(0);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
    }
    @Test
    public void testShadesOfPurple(){
        PrivateObjective testObjective = new ShadesOfPurple();
        Grid testGrid = testMocker.getGrid(1);
        Assert.assertEquals(20, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(3);
        Assert.assertEquals(4, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(0);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
    }
}