package ingsw.TestServer.Objective;

import ingsw.Server.Grid.Grid;
import ingsw.Server.Objective.*;
import ingsw.TestServer.Mockers.MockupGrid;
import org.junit.Assert;
import org.junit.Test;

public class PublicObjectiveTest {
    MockupGrid testMocker = new MockupGrid();

    @Test
    public void testColorDiagonals(){
        PublicObjective testObjective = new ColorDiagonals();
        Grid testGrid = testMocker.getGrid(1);
        Assert.assertEquals(18, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(3);
        Assert.assertEquals(9, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(5);
        Assert.assertEquals(15, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(0);
        Assert.assertEquals(0, testObjective.getScore(testGrid));

    }
    @Test
    public void testRowShadeVariety(){
        PublicObjective testObjective = new RowShadeVariety();
        Grid testGrid = testMocker.getGrid(1);
        Assert.assertEquals(20, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(3);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(0);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
    }
    @Test
    public void testColumnShadeVariety(){
        PublicObjective testObjective = new ColumnShadeVariety();
        Grid testGrid = testMocker.getGrid(1);
        Assert.assertEquals(20, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(3);
        Assert.assertEquals(4, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(0);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
    }
    @Test
    public void testRowColorVariety(){
        PublicObjective testObjective = new RowColorVariety();
        Grid testGrid = testMocker.getGrid(1);
        Assert.assertEquals(24, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(3);
        Assert.assertEquals(6, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(0);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
    }
    @Test
    public void testColumnColorVariety(){
        PublicObjective testObjective = new ColumnColorVariety();
        Grid testGrid = testMocker.getGrid(1);
        Assert.assertEquals(25, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(3);
        Assert.assertEquals(5, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(0);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
    }
    @Test
    public void testLightShades(){
        PublicObjective testObjective = new LightShades();
        Grid testGrid = testMocker.getGrid(1);
        Assert.assertEquals(8,testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(3);
        Assert.assertEquals(4,testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(0);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
    }
    @Test
    public void testMediumShades(){
        PublicObjective testObjective = new MediumShades();
        Grid testGrid = testMocker.getGrid(1);
        Assert.assertEquals(8, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(3);
        Assert.assertEquals(4, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(0);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
    }
    @Test
    public void testDeepShades(){
        PublicObjective testObjective = new DeepShades();
        Grid testGrid = testMocker.getGrid(1);
        Assert.assertEquals(0,testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(4);
        Assert.assertEquals(2,testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(0);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
    }
    @Test
    public void testColorVariety(){
        PublicObjective testObjective = new ColorVariety();
        Grid testGrid = testMocker.getGrid(1);
        Assert.assertEquals(16, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(3);
        Assert.assertEquals(8, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(0);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
    }
    @Test
    public void testShadeVariety(){
        PublicObjective testObjective = new ShadeVariety();
        Grid testGrid = testMocker.getGrid(1);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(4);
        Assert.assertEquals(5, testObjective.getScore(testGrid));
        testGrid = testMocker.getGrid(0);
        Assert.assertEquals(0, testObjective.getScore(testGrid));
    }
}
