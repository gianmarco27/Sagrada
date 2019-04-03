package ingsw.TestServer.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.GameBoard;
import ingsw.Server.Grid.GridLoader;
import ingsw.Server.Grid.InvalidJsonException;
import ingsw.Server.Tools.GrindingStone;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static ingsw.Server.Dice.DiceColor.*;

public class GrindingStoneTest {

    GameBoardMocker gbMocker = new GameBoardMocker();
    GameBoard gbTest0 = gbMocker.getGameBoard(0, 4);
    GameBoard gbTest1 = gbMocker.getGameBoard(1, 4);
    GameBoard gbTest2 = gbMocker.getGameBoard(2, 4);
    ToolActionParameter tap = new ToolActionParameter();
    DicePool dicePoolTest = new DicePool();
    Dice testDice1 = new Dice(1, BLUE);
    Dice testDice2 = new Dice(3, YELLOW);

    @Test
    public void testGrindingStone() throws IOException, InvalidJsonException {

        gbTest2.getTools().set(0, new GrindingStone());
        tap.setPickedTool(10);
        gbTest2.getCurrentPlayer().setGrid(GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testGrid.json"));
        gbTest2.getCurrentPlayer().setToken(5);
        tap.setWorkOnDicePool(true);
        gbTest2.getDicePool().addDice(new Dice(1, RED));
        gbTest2.getDicePool().addDice(new Dice(2, BLUE));
        gbTest2.getDicePool().addDice(new Dice(3, GREEN));
        tap.setWorkOnDicePool(true);
        tap.setDicePoolTarget(0);

        for (int idx = 0 ; idx < 3; idx ++) {
            tap.resetCoordinates();
            tap.addCoords(idx, idx);
            gbTest2.getCurrentPlayer().setDicePlayed(false);
            Assert.assertEquals(0, gbTest2.getTools().get(0).use(tap, gbTest2));
            System.out.println(gbTest2.getCurrentPlayer().getGrid());
            Assert.assertEquals(7-(idx+1), gbTest2.getCurrentPlayer().getGrid().getDie(idx, idx).getValue());
        }

        Assert.assertEquals(0, gbTest2.getCurrentPlayer().getToken());
    }
}
