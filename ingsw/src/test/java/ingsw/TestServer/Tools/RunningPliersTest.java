package ingsw.TestServer.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.GameBoard;
import ingsw.Server.Utility.GameException;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

import static ingsw.Server.Dice.DiceColor.BLUE;
import static ingsw.Server.Dice.DiceColor.YELLOW;

public class RunningPliersTest {
    GameBoardMocker gbMocker = new GameBoardMocker();
    GameBoard gbTest0 = gbMocker.getGameBoard(0, 4);
    GameBoard gbTest1 = gbMocker.getGameBoard(1, 4);
    GameBoard gbTest2 = gbMocker.getGameBoard(2, 4);
    ToolActionParameter tap = new ToolActionParameter();
    DicePool dicePoolTest = new DicePool();
    Dice testDice1 = new Dice(1, BLUE);
    Dice testDice2 = new Dice(3, YELLOW);

    @Test
    public  void testValidRunningPliers() {

        while (gbTest2.getCurrentPlayer() != gbTest2.getPlayers().get(1)) {
            gbTest2.nextPlayer();
        }
        tap.setPickedTool(8);
        gbTest2.getCurrentPlayer().turnPlayed = 0;
        gbTest2.getCurrentPlayer().setDicePlayed(true);
        gbTest2.getCurrentPlayer().setToken(3);
        Assert.assertEquals(0, gbTest2.getTools().get(0).use(tap, gbTest2));
        Assert.assertEquals(2, gbTest2.getCurrentPlayer().getToken());
        Assert.assertEquals(1, gbTest2.getCurrentPlayer().turnPlayed);
        Assert.assertEquals(false, gbTest2.getCurrentPlayer().isDicePlayed());
    }

    @Test
    public void testDicePlacedLimit() {
        while (gbTest2.getCurrentPlayer() != gbTest2.getPlayers().get(1)) {
            gbTest2.nextPlayer();
        }
        tap.setPickedTool(8);
        gbTest2.getCurrentPlayer().turnPlayed = 1;
        gbTest2.getCurrentPlayer().setDicePlayed(true);
        gbTest2.getCurrentPlayer().setToken(3);

        try {
            gbTest2.getTools().get(0).use(tap, gbTest2);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertTrue(true);
        }
        Assert.assertEquals(3, gbTest2.getCurrentPlayer().getToken());

        gbTest2.getCurrentPlayer().setDicePlayed(false);
        try {
            gbTest2.getTools().get(0).use(tap, gbTest2);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertTrue(true);
        }
        Assert.assertEquals(3, gbTest2.getCurrentPlayer().getToken());
    }
}
