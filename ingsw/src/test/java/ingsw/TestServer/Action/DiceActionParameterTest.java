package ingsw.TestServer.Action;

import ingsw.Server.Actions.DiceActionParameter;
import org.junit.Assert;
import org.junit.Test;

public class DiceActionParameterTest {
    @Test
    public void TestDiceSelector() {
        DiceActionParameter dap = new DiceActionParameter();
        dap.chooseDie(2);
        Assert.assertEquals(2, dap.getPicked_idx());
        dap.chooseDie(3);
        Assert.assertEquals(3, dap.getPicked_idx());
    }

    @Test
    public void TestCoordSelector() {
        DiceActionParameter dap = new DiceActionParameter();
        dap.choseTarget(3, 2);
        Assert.assertEquals(3, dap.getTarget_x());
        Assert.assertEquals(2, dap.getTarget_y());
        dap.choseTarget(4, 0);
        Assert.assertEquals(4, dap.getTarget_x());
        Assert.assertEquals(0, dap.getTarget_y());
    }
}
