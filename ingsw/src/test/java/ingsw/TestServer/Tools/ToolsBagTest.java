package ingsw.TestServer.Tools;

import ingsw.Server.Tools.EglomiseBrush;
import ingsw.Server.Tools.FluxRemover;
import ingsw.Server.Tools.Tool;
import ingsw.Server.Tools.ToolsBag;
import ingsw.Server.NoMoreBagElementException;
import org.junit.Assert;
import org.junit.Test;

public class ToolsBagTest {

    @Test(expected = NoMoreBagElementException.class)
    public void testToolsBagElements() throws NoMoreBagElementException{
        ToolsBag testBag = new ToolsBag();
        int nTools = testBag.getToolsLeft();
        for (int i = 0; i < nTools; i++)
            Assert.assertNotNull(testBag.getTools());
        testBag.getTools();
    }

    @Test
    public void testUniqueItem() throws NoMoreBagElementException {
        boolean duplicate = false;
        ToolsBag testBag = new ToolsBag();
        int nTools = testBag.getToolsLeft();
        boolean[] seen = new boolean[nTools+1];
        for (int i = 0; i < nTools; i++) {
            Tool extractedTool = testBag.getTools();
            int extractedId = extractedTool.getToolNumber();
            if (seen[extractedId]) {
                duplicate = true;
            }
            seen[extractedId] = true;
        }
        Assert.assertFalse(duplicate);
    }
}
