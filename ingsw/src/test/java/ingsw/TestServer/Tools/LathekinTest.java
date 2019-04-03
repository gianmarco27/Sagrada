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

public class LathekinTest {

    GameBoardMocker gbMocker = new GameBoardMocker();
    GameBoard gbTest0 = gbMocker.getGameBoard(0, 4);
    GameBoard gbTest1 = gbMocker.getGameBoard(1, 4);
    GameBoard gbTest2 = gbMocker.getGameBoard(2, 4);
    ToolActionParameter tap = new ToolActionParameter();
    DicePool dicePoolTest = new DicePool();
    Dice testDice1 = new Dice(1, BLUE);
    Dice testDice2 = new Dice(3, YELLOW);
    Dice tempDice1;
    Dice tempDice2;

    @Test
    public void testValidLathekin() {
        while (gbTest2.getCurrentPlayer() != gbTest2.getPlayers().get(1)) {
            gbTest2.nextPlayer();
        }
        gbTest2.getCurrentPlayer().setToken(3);
        tap.addCoords(1, 0);
        tap.addCoords(2, 2);
        tap.addCoords(3, 0);
        tap.addCoords(3, 1);
        tap.setPickedTool(4);
        //test a correct case of placement
        tempDice1 = gbTest2.getCurrentPlayer().getGrid().getDie(1, 0);
        tempDice2 = gbTest2.getCurrentPlayer().getGrid().getDie(3, 0);
        Assert.assertEquals(0, gbTest2.getTools().get(1).use(tap, gbTest2));
        Assert.assertEquals(tempDice1, gbTest2.getCurrentPlayer().getGrid().getDie(2, 2));
        Assert.assertEquals(tempDice2, gbTest2.getCurrentPlayer().getGrid().getDie(3, 1));
        Assert.assertEquals(null, gbTest2.getCurrentPlayer().getGrid().getDie(1, 0));
        Assert.assertEquals(null, gbTest2.getCurrentPlayer().getGrid().getDie(3, 0));
        Assert.assertEquals(2, gbTest2.getCurrentPlayer().getToken());
    }

    @Test
    public void testOverlapping() {

        while (gbTest2.getCurrentPlayer() != gbTest2.getPlayers().get(1)) {
            gbTest2.nextPlayer();
        }
        gbTest2.getCurrentPlayer().setToken(3);

        tap.addCoords(1, 2);
        tap.addCoords(2, 3);
        tap.addCoords(3, 0);
        tap.addCoords(2, 3);
        tap.setPickedTool(4);

        tempDice1 = gbTest2.getCurrentPlayer().getGrid().getDie(1, 2);
        tempDice2 = gbTest2.getCurrentPlayer().getGrid().getDie(3, 0);
        //trying to overlap dices
        try {
            gbTest2.getTools().get(1).use(tap, gbTest2);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertTrue(true);
        }

        Assert.assertEquals(tempDice1, gbTest2.getCurrentPlayer().getGrid().getDie(1, 2));
        Assert.assertEquals(tempDice2, gbTest2.getCurrentPlayer().getGrid().getDie(3, 0));
        Assert.assertEquals(null, gbTest2.getCurrentPlayer().getGrid().getDie(2, 3));
        Assert.assertEquals(null, gbTest2.getCurrentPlayer().getGrid().getDie(2, 3));
        Assert.assertEquals(3, gbTest2.getCurrentPlayer().getToken());
    }

    @Test
    public void testNotAdjacentToAnother() {

        while (gbTest2.getCurrentPlayer() != gbTest2.getPlayers().get(1)) {
            gbTest2.nextPlayer();
        }
        gbTest2.getCurrentPlayer().setToken(3);

        tap.setPickedTool(4);
        tap.addCoords(2,0);
        tap.addCoords(2,3);
        tap.addCoords(0,3);
        tap.addCoords(4,2);
        System.out.println(gbTest2.getCurrentPlayer().getGrid());
        tempDice1 = gbTest2.getCurrentPlayer().getGrid().getDie(2,0);
        tempDice2 = gbTest2.getCurrentPlayer().getGrid().getDie(0,3);
        try {
            gbTest2.getTools().get(1).use(tap, gbTest2);
            Assert.assertTrue(false);
        } catch (GameException e) {
            Assert.assertTrue(true);
        }
        Assert.assertEquals(null, gbTest2.getCurrentPlayer().getGrid().getDie(2,3));
        Assert.assertEquals(null, gbTest2.getCurrentPlayer().getGrid().getDie(4,2));
        Assert.assertEquals(tempDice1, gbTest2.getCurrentPlayer().getGrid().getDie(2, 0));
        Assert.assertEquals(tempDice2, gbTest2.getCurrentPlayer().getGrid().getDie(0, 3));
        Assert.assertEquals(3, gbTest2.getCurrentPlayer().getToken());
    }

}
