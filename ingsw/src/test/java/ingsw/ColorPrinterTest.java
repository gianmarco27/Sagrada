package ingsw;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Grid.InvalidJsonException;
import ingsw.Server.Tools.*;
import ingsw.Server.Utility.RoundTracker;
import ingsw.TestServer.Mockers.GameBoardMocker;
import ingsw.TestServer.Mockers.MockupGrid;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ColorPrinterTest {

    Dice blue = new Dice(DiceColor.BLUE);
    Dice red = new Dice(DiceColor.RED);
    Dice green = new Dice(DiceColor.GREEN);
    Dice yellow = new Dice(DiceColor.YELLOW);
    Dice purple = new Dice(DiceColor.PURPLE);

    @Test
    public void testColoredDice() {
        System.out.println("DICE TEST");
        List<Dice> coloredDice = new ArrayList<>();
        coloredDice.add(blue);
        coloredDice.add(red);
        coloredDice.add(green);
        coloredDice.add(yellow);
        coloredDice.add(purple);

        for (Dice d : coloredDice) {
            System.out.println(d.toString(true));
        }
        System.out.println("\n");
        Assert.assertTrue(true);
    }

    @Test
    public void testColoredDicePool() {
        DicePool dp = new DicePool();
        dp.addDice(red);
        dp.addDice(green);
        dp.addDice(yellow);
        dp.addDice(green);
        System.out.println(dp.toString(true));
        Assert.assertTrue(true);
    }

    @Test
    public void testColoredGrid() {
        MockupGrid mocker = new MockupGrid();
        Grid testGrid = mocker.getGrid(6);
        Dice testDice1 = new Dice(3, DiceColor.RED);
        Dice testDice2 = new Dice(4, DiceColor.RED);
        Dice testDice3 = new Dice(1, DiceColor.BLUE);
        testGrid.placeDie(testDice1,4,3);
        testGrid.placeDie(testDice3,4,2);
        testGrid.placeDie(testDice2,3,2);
        System.out.println(testGrid.toString(true));
        Assert.assertTrue(true);
    }

    @Test
    public void testColoredRoundTracker() {
        RoundTracker rt = new RoundTracker();
        rt.placeDice(1, red);
        rt.placeDice(2, blue);
        rt.placeDice(5, red);
        rt.placeDice(5, blue);
        rt.placeDice(5, purple);
        rt.placeDice(5, blue);
        rt.placeDice(9, red);
        rt.placeDice(9, purple);
        rt.placeDice(8, yellow);
        rt.placeDice(7, green);
        rt.placeDice(7, green);
        System.out.println("\n" + rt.toString(true));
        Assert.assertTrue(true);

    }

    @Test
    public void printAllTools() {
        System.out.println("\nTOOL TEST\n");
        List<Tool> toolsPool = new ArrayList<>();
        // Instantiates a card for each tools
        toolsPool.add(new GlazingHammer());
        toolsPool.add(new EglomiseBrush());
        toolsPool.add(new Lathekin());
        toolsPool.add(new GrozingPliers());
        toolsPool.add(new CopperFoilBurnishes());
        toolsPool.add(new LensCutter());
        toolsPool.add(new FluxBrush());
        toolsPool.add(new RunningPliers());
        toolsPool.add(new CorkBackedStraightedge());
        toolsPool.add(new GrindingStone());
        toolsPool.add(new FluxRemover());
        toolsPool.add(new TapWheel());
        for (Tool t : toolsPool) {
            System.out.println(t);
        }
    }

    @Test
    public void testMultiPlayerScoreboardColor() {
        GameBoardMocker gbMocker = new GameBoardMocker();
        GameBoard gb = gbMocker.getGameBoard(0, 4);
        gb.calculateScore();
        System.out.println(gb.getScoreBoardStanding());
        Assert.assertTrue(true);
    }

    @Test
    public void testSinglePlayerScoreboardColor() {
        GameBoardMocker gbMocker = new GameBoardMocker();
        GameBoard gb = gbMocker.getGameBoard(5, 1);
        gb.calculateScore();
        System.out.println(gb.getScoreBoardStanding());
        Assert.assertTrue(true);
    }
}
