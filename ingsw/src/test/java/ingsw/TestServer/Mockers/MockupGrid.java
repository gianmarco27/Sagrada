package ingsw.TestServer.Mockers;

import ingsw.Server.Grid.*;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;

import java.io.IOException;

public class MockupGrid {

    public Grid getGrid(int idx_grid) {
        if (idx_grid == 0) {
            return Grid0(); // EmptyTest
        }
        if (idx_grid == 1) {
            return Grid1(); // Diagonals full
        }
        if (idx_grid == 2) {
            return Grid2(); // Diagonals full 2
        }
        if (idx_grid == 3) {
            return Grid3(); // Kaleidoscope full
        }
        if (idx_grid == 4) {
            return Grid4(); // Test partially filled to cover most objectives
        }
        if (idx_grid == 5) {
            return Grid5(); // Diagonals missing 4th one
        }
        if (idx_grid == 6) {
            return Grid6(); // Virtus empty
        }
        else {
            return null;
        }
    }

    private Grid Grid0() {
        Grid grid0 = null;
        try {
            grid0 = GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testGrid.json");
        } catch (IOException | InvalidJsonException e) {
            e.printStackTrace();
        }
        return grid0;
    }
    private Grid Grid1() {
        Grid grid1 = null;
        try {
            grid1 = GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testGrid.json");
        } catch (IOException | InvalidJsonException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                if (i + j == 0 || i + j == 5)
                    grid1.placeDie(new Dice(1, DiceColor.RED), i, j);
                if (i + j == 1 || i + j == 6)
                    grid1.placeDie(new Dice(2, DiceColor.BLUE), i, j);
                if (i + j == 2 || i + j == 7)
                    grid1.placeDie(new Dice(3, DiceColor.GREEN), i, j);
                if (i + j == 3)
                    grid1.placeDie(new Dice(4, DiceColor.YELLOW), i, j);
                if (i + j == 4)
                    grid1.placeDie(new Dice(5, DiceColor.PURPLE), i, j);
            }
        }
        return grid1;
    }
    private Grid Grid2() {
        Grid grid2 = null;
        try {
            grid2 = GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testGrid.json");
        } catch (IOException | InvalidJsonException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                if (i + j == 0 || i + j == 7)
                    grid2.placeDie(new Dice(1, DiceColor.RED), i, j);
                if (i + j == 1 || i + j == 6)
                    grid2.placeDie(new Dice(2, DiceColor.BLUE), i, j);
                if (i + j == 2 || i + j == 5)
                    grid2.placeDie(new Dice(3, DiceColor.GREEN), i, j);
                if (i + j == 3)
                    grid2.placeDie(new Dice(4, DiceColor.YELLOW), i, j);
                if (i + j == 4)
                    grid2.placeDie(new Dice(5, DiceColor.PURPLE), i, j);
            }
        }

        return grid2;
    }
    private Grid Grid3(){
        Grid grid3 = null;
        try {
            grid3 = GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testKaleidoscope.json");
        } catch (IOException | InvalidJsonException e) {
            e.printStackTrace();
        }
        grid3.placeDie(new Dice(5, DiceColor.YELLOW),0,0);
        grid3.placeDie(new Dice(4, DiceColor.BLUE),1,0);
        grid3.placeDie(new Dice(5, DiceColor.YELLOW),2,1);
        grid3.placeDie(new Dice(5, DiceColor.YELLOW),3,2);
        grid3.placeDie(new Dice(5, DiceColor.YELLOW),1,2);
        grid3.placeDie(new Dice(5, DiceColor.YELLOW),4,3);
        grid3.placeDie(new Dice(2, DiceColor.GREEN),0,1);
        grid3.placeDie(new Dice(2, DiceColor.GREEN),2,0);
        grid3.placeDie(new Dice(2, DiceColor.GREEN),4,2);
        grid3.placeDie(new Dice(2, DiceColor.GREEN),2,3);
        grid3.placeDie(new Dice(3, DiceColor.RED),0,2);
        grid3.placeDie(new Dice(3, DiceColor.RED),2,2);
        grid3.placeDie(new Dice(4, DiceColor.BLUE),3,0);
        grid3.placeDie(new Dice(4, DiceColor.BLUE),4,1);
        grid3.placeDie(new Dice(4, DiceColor.BLUE),1,3);
        grid3.placeDie(new Dice(4, DiceColor.BLUE),3,3);
        grid3.placeDie(new Dice(1, DiceColor.RED),1,1);
        grid3.placeDie(new Dice(2, DiceColor.PURPLE),3,1);
        grid3.placeDie(new Dice(2, DiceColor.PURPLE),0,3);
        grid3.placeDie(new Dice(1, DiceColor.YELLOW),4,0);
        return grid3;
    }

    private Grid Grid4(){
        Grid grid4 = null;
        try {
            grid4 = GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testGrid.json");
        } catch (IOException | InvalidJsonException e) {
            e.printStackTrace();
        }
        // Column shade and color variety
        grid4.placeDieWithoutRestrictions(new Dice(1, DiceColor.RED),0,0);
        grid4.placeDieWithoutRestrictions(new Dice(2, DiceColor.BLUE),0,1);
        grid4.placeDieWithoutRestrictions(new Dice(3, DiceColor.GREEN),0,2);
        grid4.placeDieWithoutRestrictions(new Dice(4, DiceColor.YELLOW),0,3);
        // Row shade and color variety
        grid4.placeDieWithoutRestrictions(new Dice(2, DiceColor.BLUE),1,0);
        grid4.placeDieWithoutRestrictions(new Dice(3, DiceColor.GREEN),2,0);
        grid4.placeDieWithoutRestrictions(new Dice(4, DiceColor.YELLOW),3,0);
        grid4.placeDieWithoutRestrictions(new Dice(5, DiceColor.PURPLE),4,0);
        // Deep shades
        grid4.placeDieWithoutRestrictions(new Dice(1, DiceColor.RED),1,1);
        grid4.placeDieWithoutRestrictions(new Dice(5, DiceColor.YELLOW),1,2);
        grid4.placeDieWithoutRestrictions(new Dice(6, DiceColor.PURPLE),1,3);
        return grid4;
    }
    private Grid Grid5() {
        Grid grid5 = null;
        try {
            grid5 = GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testGrid.json");
        } catch (IOException | InvalidJsonException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                if (i + j == 0)
                    grid5.placeDie(new Dice(1, DiceColor.RED), i, j);
                if (i + j == 1 || i + j == 7)
                    grid5.placeDie(new Dice(2, DiceColor.BLUE), i, j);
                if (i + j == 2 || i + j == 6)
                    grid5.placeDie(new Dice(3, DiceColor.GREEN), i, j);
                if (i + j == 3)
                    grid5.placeDie(new Dice(4, DiceColor.YELLOW), i, j);
                if (i + j == 4)
                    grid5.placeDie(new Dice(5, DiceColor.PURPLE), i, j);
            }
        }
        return grid5;
    }
    private Grid Grid6(){
        Grid grid6 = null;
        try {
            grid6 = GridLoader.loadFromFile("./src/test/java/ingsw/TestResources/testVirtus.json");
        } catch (IOException | InvalidJsonException e) {
            e.printStackTrace();
        }
        return grid6;
    }
}
