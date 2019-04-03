package ingsw.TestServer.Tools;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceBag;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.GameBoard;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Grid.GridLoader;
import ingsw.Server.Grid.InvalidJsonException;
import ingsw.Server.Tools.FluxRemover;
import ingsw.Server.Utility.GameException;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static ingsw.Server.Dice.DiceColor.BLUE;
import static ingsw.Server.Dice.DiceColor.YELLOW;
import static ingsw.Server.Utility.CopyObjects.deepCopy;

public class FluxRemoverTest {

    GameBoardMocker gbMocker = new GameBoardMocker();
    GameBoard gbTest0 = gbMocker.getGameBoard(0, 4);
    GameBoard gbTest1 = gbMocker.getGameBoard(1, 4);
    GameBoard gbTest2 = gbMocker.getGameBoard(2, 4);
    ToolActionParameter tap = new ToolActionParameter();
    DicePool dicePoolTest = new DicePool();
    Dice testDice1 = new Dice(1, BLUE);
    Dice testDice2 = new Dice(3, YELLOW);
    int diceLeft;

    @Test
    public void testValidFluxRemover() throws IOException, InvalidJsonException {
        gbTest2.getTools().set(0, new FluxRemover());
        tap.setPickedTool(11);
        tap.setWorkOnDicePool(true);
        tap.setDicePoolTarget(0);
        gbTest2.getDicePool().addDice(testDice1);
        gbTest2.getDicePool().addDice(testDice2);
        gbTest2.getDicePool().addDice(testDice1);
        gbTest2.getCurrentPlayer().setGrid(GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testGrid.json"));
        gbTest2.getCurrentPlayer().setToken(3);
        diceLeft = gbTest2.getDiceBag().diceLeft();
        try {
            gbTest2.getTools().get(0).use(tap, gbTest2);
            Assert.assertTrue(false);
        } catch (GameException e) {
            if (e.isCallBack()) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        }
        tap.setCallBack(true);
        tap.addCoords(0, 0);
        tap.setValueChangeTo(5);
        Assert.assertEquals(0, gbTest2.getTools().get(0).use(tap, gbTest2));
        Assert.assertEquals(2, gbTest2.getCurrentPlayer().getToken());
        Assert.assertEquals(5, gbTest2.getCurrentPlayer().getGrid().getDie(0, 0).getValue());
        Assert.assertEquals(testDice2, gbTest2.getDicePool().getAllDice().get(0));
        Assert.assertEquals(diceLeft, gbTest2.getDiceBag().diceLeft());
    }

    @Test
    public void unplaceableDice() {
        while (gbTest0.getCurrentPlayer() != gbTest0.getPlayers().get(0)) {
            gbTest0.nextPlayer();
        }
        gbTest0.getTools().set(0, new FluxRemover());
        gbTest0.setDiceBag(new DiceBag());
        tap.setPickedTool(11);
        tap.setWorkOnDicePool(true);
        tap.setDicePoolTarget(0);
        gbTest0.getDicePool().addDice(testDice1);
        gbTest0.getDicePool().addDice(testDice2);
        gbTest0.getDicePool().addDice(testDice1);
        gbTest0.getCurrentPlayer().setToken(3);
        Grid oldGrid = ((Grid) deepCopy(gbTest0.getCurrentPlayer().getGrid()));
        diceLeft = gbTest0.getDiceBag().diceLeft();
        System.out.println(oldGrid);
        try {
            gbTest0.getTools().get(0).use(tap, gbTest0);
            Assert.assertTrue(false);
        } catch (GameException e) {
            if (e.isCallBack()) {
                Assert.assertTrue(false);
            } else {
                System.out.println(e);
                Assert.assertTrue(true);
            }
        }

        Assert.assertEquals(2, gbTest0.getCurrentPlayer().getToken());

        for (int idx_col = 0; idx_col < 5; idx_col++) {
            for (int idx_row = 0; idx_row < 4; idx_row++) {
                Assert.assertEquals(oldGrid.getDie(idx_col, idx_row).getValue(), gbTest0.getCurrentPlayer().getGrid().getDie(idx_col, idx_row).getValue());
                Assert.assertEquals(oldGrid.getDie(idx_col, idx_row).getColor(), gbTest0.getCurrentPlayer().getGrid().getDie(idx_col, idx_row).getColor());
            }
        }
        Assert.assertEquals(testDice2, gbTest0.getDicePool().getAllDice().get(1));
        Assert.assertNotEquals(null, gbTest0.getDicePool().getAllDice().get(2));
        Assert.assertEquals(diceLeft, gbTest0.getDiceBag().diceLeft());
    }
}
