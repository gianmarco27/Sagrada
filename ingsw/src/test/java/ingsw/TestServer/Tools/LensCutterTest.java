package ingsw.TestServer.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.GameBoard;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

import static ingsw.Server.Dice.DiceColor.BLUE;
import static ingsw.Server.Dice.DiceColor.YELLOW;

public class LensCutterTest {

    GameBoardMocker gbMocker = new GameBoardMocker();
    GameBoard gbTest0 = gbMocker.getGameBoard(0, 4);
    GameBoard gbTest1 = gbMocker.getGameBoard(1, 4);
    GameBoard gbTest2 = gbMocker.getGameBoard(2, 4);
    ToolActionParameter tap = new ToolActionParameter();
    DicePool dicePoolTest = new DicePool();
    Dice testDice1 = new Dice(1, BLUE);
    Dice testDice2 = new Dice(3, YELLOW);

    @Test
    public void testValidLensCutter() {

        while (gbTest0.getCurrentPlayer().getToken() < 3) {
            gbTest0.nextPlayer();
        }
        tap.setRoundTrackerTargetRound(0);
        tap.setRoundTrackerTargetOption(0);
        tap.setDicePoolTarget(0);
        tap.setWorkOnDicePool(true);
        tap.setWorkOnRoundTracker(true);
        tap.setPickedTool(5);
        dicePoolTest = gbTest0.getDicePool();
        dicePoolTest.addDice(testDice1);
        dicePoolTest.addDice(testDice2);
        Dice tempDice = gbTest0.getRoundTracker().peekPosition(0, 0);

        Assert.assertEquals(0, gbTest0.getTools().get(0).use(tap, gbTest0));
        Assert.assertEquals(tempDice, gbTest0.getDicePool().getAllDice().get(0));
        Assert.assertEquals(testDice1, gbTest0.getRoundTracker().peekPosition(0, 0));
    }
}
