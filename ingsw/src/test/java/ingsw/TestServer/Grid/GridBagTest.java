package ingsw.TestServer.Grid;

import ingsw.Server.Grid.Grid;
import ingsw.Server.Grid.GridBag;
import ingsw.Server.NoMoreBagElementException;
import org.junit.Assert;
import org.junit.Test;

public class GridBagTest {

    GridBag testBag = new GridBag();

    @Test
    public void testLeftGrids() throws NoMoreBagElementException {
        for (int pairs = 12; pairs > 0; pairs--) {
            Assert.assertEquals(pairs,testBag.getPairsLeft());
            testBag.extractPair();
        }
    }

    @Test (expected = NoMoreBagElementException.class)
    public void testLeftGridsException() throws NoMoreBagElementException {
        for (int pairs = 13; pairs > 0; pairs--) {
            testBag.extractPair();
        }
    }

    @Test
    public void testExtractPair() throws NoMoreBagElementException {
        Assert.assertEquals(2, testBag.extractPair().length);
        Assert.assertEquals(11, testBag.getPairsLeft());
    }

    @Test
    public void testExtractPairs() throws NoMoreBagElementException {
        Assert.assertEquals(7, testBag.extractPairs(7).size());
        Assert.assertEquals(5, testBag.getPairsLeft());
        Assert.assertEquals(5, testBag.extractPairs(5).size());
        Assert.assertEquals(0, testBag.getPairsLeft());
    }

    @Test
    public void testExtractedAreCouples() throws NoMoreBagElementException {
        Grid[] pair;
        pair = testBag.extractPair();
        Assert.assertSame(pair[0].getMatchId(), pair[1].getMatchId());
    }

    @Test
    public void testAddGridPair() throws NoMoreBagElementException {
        Grid[] pair;
        pair = testBag.extractPair();
        Assert.assertEquals(11, testBag.getPairsLeft());
        testBag.addGridPair(pair);
        Assert.assertEquals(12, testBag.getPairsLeft());
    }
}
