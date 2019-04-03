package ingsw.TestServer.Objective;

import ingsw.Server.Objective.Objective;
import ingsw.Server.Objective.ObjectiveBag;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Objective.PrivateObjective;
import ingsw.Server.Objective.PublicObjective;
import org.junit.Assert;
import org.junit.Test;

public class ObjectiveBagTest {

    @Test(expected = NoMoreBagElementException.class)
    public void testPublicObjectiveBag() throws NoMoreBagElementException{
        ObjectiveBag testBag = new ObjectiveBag();
        for (int i = 0; i < 11; i++)
            testBag.getPublicObj();
    }
    @Test(expected = NoMoreBagElementException.class)
    public void testPrivateObjectiveBag() throws NoMoreBagElementException{
        ObjectiveBag testBag = new ObjectiveBag();
        for (int i = 0; i < 6; i++)
            testBag.getPrivateObj();
    }
   @Test
    public void testUniquePublicObjective() throws NoMoreBagElementException {
        boolean duplicate = false;
        ObjectiveBag testBag = new ObjectiveBag();
        boolean[] seen = new boolean[10+1];
        for (int i = 0; i < 10; i++) {
            PublicObjective extractedTool = testBag.getPublicObj();
            int extractedId = extractedTool.getCardId();
            if (seen[extractedId]) {
                duplicate = true;
            }
            seen[extractedId] = true;
        }
        Assert.assertFalse(duplicate);
    }

    @Test
    public void testUniquePrivateObjective() throws NoMoreBagElementException {
        boolean duplicate = false;
        ObjectiveBag testBag = new ObjectiveBag();
        boolean[] seen = new boolean[5+1];
        for (int i = 0; i < 5; i++) {
            PrivateObjective extractedTool = testBag.getPrivateObj();
            int extractedId = extractedTool.getCardId();
            if (seen[extractedId - 11]) {
                duplicate = true;
            }
            seen[extractedId - 11] = true;
        }
        Assert.assertFalse(duplicate);
    }
}
