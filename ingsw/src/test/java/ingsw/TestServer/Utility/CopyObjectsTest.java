package ingsw.TestServer.Utility;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static ingsw.Server.Utility.CopyObjects.deepCopy;
import static ingsw.Server.Utility.CopyObjects.deserializeFromB64;
import static ingsw.Server.Utility.CopyObjects.serializeToB64;

public class CopyObjectsTest {
    @Test
    public void testDeepCopy() {
        Dice toCopy = new Dice(5, DiceColor.BLUE);
        Dice newCopy = ((Dice) deepCopy(toCopy));

        Assert.assertNotEquals(toCopy, newCopy);
        Assert.assertEquals(toCopy.getColor(), newCopy.getColor());
        Assert.assertEquals(toCopy.getValue(), newCopy.getValue());
    }

    @Test
    public void testSerializeDeserialize() {
        Dice toSerialize = new Dice(5, DiceColor.BLUE);
        String serializedObject;
        Dice deserializedDice = new Dice(1, DiceColor.GREEN);

        try {
            serializedObject = serializeToB64(toSerialize);
            deserializedDice = ((Dice) deserializeFromB64(serializedObject));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(toSerialize.getValue(), deserializedDice.getValue());
        Assert.assertEquals(toSerialize.getColor(), deserializedDice.getColor());
    }

}
