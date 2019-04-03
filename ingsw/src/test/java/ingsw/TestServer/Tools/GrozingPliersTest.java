package ingsw.TestServer.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.GameBoard;
import ingsw.Server.Tools.GrozingPliers;
import ingsw.Server.Utility.GameException;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

import static ingsw.Server.Dice.DiceColor.BLUE;
import static ingsw.Server.Dice.DiceColor.YELLOW;

public class GrozingPliersTest {

    GameBoardMocker gbMocker = new GameBoardMocker();
    GameBoard gbTest0 = gbMocker.getGameBoard(0, 4);
    GameBoard gbTest1 = gbMocker.getGameBoard(1, 4);
    GameBoard gbTest2 = gbMocker.getGameBoard(2, 4);
    ToolActionParameter tap = new ToolActionParameter();
    Dice testDice1 = new Dice(1, BLUE);
    Dice testDice2 = new Dice(3, YELLOW);

    @Test
    public  void testValidGrozingpliers() {

        gbTest2.getDicePool().addDice(testDice1);
        gbTest2.getDicePool().addDice(testDice2);

        while (gbTest2.getCurrentPlayer() != gbTest2.getPlayers().get(1)) {
            gbTest2.nextPlayer();
        }

        tap.setPickedTool(1);
        tap.setWorkOnDicePool(true);
        tap.setDicePoolTarget(1);
        tap.setValueChangeTo(4);
        tap.addCoords(2, 1);
        gbTest2.getTools().set(0, new GrozingPliers());
        gbTest2.getCurrentPlayer().setToken(3);

        Assert.assertEquals(0, gbTest2.getTools().get(0).use(tap, gbTest2));
        Assert.assertEquals(2, gbTest2.getCurrentPlayer().getToken());
        Assert.assertEquals(0, gbTest2.getCurrentPlayer().turnPlayed);
        Assert.assertEquals(false, gbTest2.getCurrentPlayer().isDicePlayed());
    }

    @Test (expected = GameException.class)
    public void testWrongValue() {
        gbTest2.getDicePool().addDice(testDice1);
        gbTest2.getDicePool().addDice(testDice2);

        while (gbTest2.getCurrentPlayer() != gbTest2.getPlayers().get(1)) {
            gbTest2.nextPlayer();
        }

        tap.setPickedTool(1);
        tap.setWorkOnDicePool(true);
        tap.setDicePoolTarget(1);
        tap.setValueChangeTo(6);
        tap.addCoords(2, 1);

        gbTest2.getTools().set(0, new GrozingPliers());
        gbTest2.getCurrentPlayer().setToken(3);

        gbTest2.getTools().get(0).use(tap, gbTest2);
    }

    @Test (expected = GameException.class)
    public void testWrongDice() {
        gbTest2.getDicePool().addDice(testDice1);
        gbTest2.getDicePool().addDice(testDice2);

        while (gbTest2.getCurrentPlayer() != gbTest2.getPlayers().get(1)) {
            gbTest2.nextPlayer();
        }

        tap.setPickedTool(1);
        tap.setWorkOnDicePool(true);
        tap.setDicePoolTarget(3);
        tap.setValueChangeTo(1);
        tap.addCoords(2, 1);

        gbTest2.getTools().set(0, new GrozingPliers());
        gbTest2.getCurrentPlayer().setToken(3);

        gbTest2.getTools().get(0).use(tap, gbTest2);
    }

}
