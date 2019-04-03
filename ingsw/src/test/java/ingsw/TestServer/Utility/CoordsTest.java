package ingsw.TestServer.Utility;

import ingsw.Server.Utility.Coord;
import org.junit.Assert;
import org.junit.Test;

public class CoordsTest {

    @Test
    public void testEquals() {
        Coord toCompare = new Coord(3,4);
        Coord other1 = new Coord(3,3);
        Coord uninitialized = null;
        Assert.assertEquals(false, toCompare.equals(other1));
        Assert.assertEquals(false, toCompare.equals(uninitialized));
        Coord other2 = new Coord(2,4);
        Assert.assertEquals(false, toCompare.equals(other2));
        Coord other3 = new Coord(3,4);
        Assert.assertEquals(true, toCompare.equals(other3));
    }
}
