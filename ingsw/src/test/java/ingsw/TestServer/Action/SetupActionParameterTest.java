package ingsw.TestServer.Action;

import ingsw.Server.Actions.SetupActionParameter;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Grid.GridLoader;
import ingsw.Server.Grid.InvalidJsonException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SetupActionParameterTest {

    @Test
    public void testAddGridCouple() throws IOException, InvalidJsonException {
        Grid[] testCouple1 = new Grid[3];
        Grid[] testCouple2 = new Grid[2];
        SetupActionParameter testTap = new SetupActionParameter();

        testCouple1[0] = GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testGrid.json");
        testCouple1[1] = GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testKaleidoscope.json");
        testCouple1[2] = GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testVirtus.json");
        Assert.assertEquals(false, testTap.addGridCouple(testCouple1));
        testCouple2[0] = GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testGrid.json");
        testCouple2[1] = GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testVirtus.json");
        Assert.assertEquals(true, testTap.addGridCouple(testCouple2));
        Assert.assertEquals(4, testTap.getGridChoice().length);
        Assert.assertEquals(true, testTap.addGridCouple(testCouple2));
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testBoundaries() throws IOException, InvalidJsonException {
        Grid[] testCouple1 = new Grid[3];
        SetupActionParameter testTap = new SetupActionParameter();
        testCouple1[0] = GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testGrid.json");
        testCouple1[1] = GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testKaleidoscope.json");
        Assert.assertEquals(true, testTap.addGridCouple(testCouple1));
        Assert.assertEquals(true, testTap.addGridCouple(testCouple1));
        testTap.addGridCouple(testCouple1);
    }
}
