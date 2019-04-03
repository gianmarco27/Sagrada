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

public class TapWheelTest {

    GameBoardMocker gbMocker = new GameBoardMocker();
    GameBoard gbTest0 = gbMocker.getGameBoard(0, 4);
    GameBoard gbTest1 = gbMocker.getGameBoard(1, 4);
    GameBoard gbTest2 = gbMocker.getGameBoard(2, 4);
    ToolActionParameter tap = new ToolActionParameter();
    DicePool dicePoolTest = new DicePool();
    Dice testDice1 = new Dice(1, BLUE);
    Dice testDice2 = new Dice(3, YELLOW);
    Dice sampleDice;
    Dice tempDice1;
    Dice tempDice2;

    @Test
    public void testValidTapWheel() {

        while (gbTest2.getCurrentPlayer() != gbTest2.getPlayers().get(1)) {
            gbTest2.nextPlayer();
        }

        gbTest2.getCurrentPlayer().setToken(3);
        tap.setRoundTrackerTargetRound(2);
        tap.setRoundTrackerTargetOption(0);
        tap.addCoords(3, 0);
        tap.addCoords(2, 2);
        tap.addCoords(1, 2);
        tap.addCoords(3, 1);
        tap.setWorkOnRoundTracker(true);
        tap.setPickedTool(12);
        sampleDice = gbTest2.getRoundTracker().peekPosition(2, 0);
        tempDice1 = gbTest2.getCurrentPlayer().getGrid().getDie(0, 3);
        tempDice2 = gbTest2.getCurrentPlayer().getGrid().getDie(1, 2);

        Assert.assertEquals(sampleDice.getColor(), gbTest2.getCurrentPlayer().getGrid().getDie(0, 3).getColor());
        Assert.assertEquals(sampleDice.getColor(), gbTest2.getCurrentPlayer().getGrid().getDie(1, 2).getColor());
        Assert.assertEquals(0, gbTest2.getTools().get(2).use(tap, gbTest2));
        Assert.assertEquals(tempDice1.toString(), gbTest2.getCurrentPlayer().getGrid().getDie(2, 2).toString());
        Assert.assertEquals(tempDice2.toString(), gbTest2.getCurrentPlayer().getGrid().getDie(3, 1).toString());
        Assert.assertEquals(2, gbTest2.getCurrentPlayer().getToken());
    }

    @Test
    public void testInvalidTapWheel() {

        while (gbTest2.getCurrentPlayer() != gbTest2.getPlayers().get(1)) {
            gbTest2.nextPlayer();
        }

        gbTest2.getCurrentPlayer().setToken(3);
        tap.setRoundTrackerTargetRound(2);
        tap.setRoundTrackerTargetOption(0);
        tap.setWorkOnRoundTracker(true);
        tap.setPickedTool(12);
        sampleDice = gbTest2.getRoundTracker().peekPosition(2, 0);
        tempDice1 = gbTest2.getCurrentPlayer().getGrid().getDie(0, 3);
        tempDice2 = gbTest2.getCurrentPlayer().getGrid().getDie(1, 2);
        tap.addCoords(3, 0);
        tap.addCoords(0, 3);
        tap.addCoords(0, 3);
        tap.addCoords(4, 1);
        // Second die owerwriting First forbidden due to simultaneous check
        try {
            gbTest2.getTools().get(2).use(tap, gbTest2);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertTrue(true);
        }
        Assert.assertEquals(3, gbTest2.getCurrentPlayer().getToken());
    }
}
