package ingsw.TestServer.Action;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Client.View.ToolParameterParserGUI;
import ingsw.Server.Utility.Coord;
import org.junit.Assert;
import org.junit.Test;

public class ToolParameterParserGUITest {

    ToolParameterParserGUI tap = ToolParameterParserGUI.getInstance();

    @Test
    public void testRTCommand() {
        tap.reset();
        tap.setRoundTracker(new Coord(2,3));
        ToolActionParameter actualTap = tap.createCommand();
        Assert.assertEquals(true, actualTap.isWorkOnRoundTracker());
        Assert.assertEquals(2, actualTap.getRoundTrackerTargetRound());
        Assert.assertEquals(3, actualTap.getRoundTrackerTargetOption());
    }

    @Test
    public void testDPCommand() {
        tap.reset();
        tap.setDicePool(2);
        tap.setPayDicePool(1);
        ToolActionParameter actualTap = tap.createCommand();
        Assert.assertEquals(true, actualTap.isWorkOnDicePool());
        Assert.assertEquals(2, actualTap.getDicePoolTarget());
        Assert.assertEquals(1, actualTap.getPayDicePool());
    }

    @Test
    public void testCoordCommand() {
        tap.reset();
        Coord validCoord = new Coord(4,1);
        tap.addCoord(new Coord(2,3));
        tap.addCoord(validCoord);
        tap.removeCoord(new Coord(2,3));
        ToolActionParameter actualTap = tap.createCommand();
        Assert.assertEquals(false, actualTap.isWorkOnDicePool());
        Assert.assertEquals(validCoord, actualTap.getCoords().get(0));
        Assert.assertEquals(1, actualTap.getCoords().size());
    }

    @Test
    public void testMixedCommand() {
        tap.reset();
        tap.setPickedTool(9);
        tap.setWishValue(2);
        ToolActionParameter actualTap = tap.createCommand();
        Assert.assertEquals(false, actualTap.isWorkOnDicePool());
        Assert.assertEquals(9, actualTap.getPickedTool());
        Assert.assertEquals(2, actualTap.getValueChangeTo());
    }
}
