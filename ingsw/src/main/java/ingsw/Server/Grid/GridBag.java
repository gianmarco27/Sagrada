package ingsw.Server.Grid;


import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.GameFlow.GameFlowStates.GridSetupState;
import ingsw.Server.NoMoreBagElementException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Bag of Grid that handles the correct instantiation and retrieval of the Grids available in Sagrada.
 */
public class GridBag implements Serializable {
    private static final Logger log = Logger.getLogger( GridBag.class.getName() );

    private List<Grid[]> gridPairs;

    public GridBag() {
        gridPairs = new CopyOnWriteArrayList<>();
        List<String> gridsFiles = GridLoader.listFiles("./Grids");
        if (gridsFiles== null || gridsFiles.size() == 0) {
            System.out.println("Couldn't load grids from ./Grids");
            System.exit(-1);
        }
        if ((gridsFiles.size() % 2) == 1) {
            System.out.println("Odd number of grid files not supported\n");
            System.exit(-1);
        }
        for (int i = 0; i < (gridsFiles.size() / 2); i++) {
            gridPairs.add(new Grid[2]);
        }
        for (String s : gridsFiles) {
            try {
                Grid g = GridLoader.loadFromFile(s);
                gridPairs.get(g.getMatchId() - 1)[g.getCardId() % 2] = g;
            } catch (IOException | InvalidJsonException e) {
                log.warning(Arrays.toString(e.getStackTrace()));
                System.exit(-1);
            }
        }
        this.shuffleBag(); // We rely on this behaviour to avoid non-deterministic approach
    }

    @Override
    public String toString() {
        if (gridPairs.size() == 0) {
            return "[ Empty DiceBag ] ";
        } else {
            StringBuilder rep = new StringBuilder();
            rep.append("GridBad:\n");
            for (Grid[] p : gridPairs) {
                rep.append("matchId : "+p[0].getMatchId()+ " | ");
                rep.append(p[0].getCardId() + " - "+ p[0].getCardName());
                rep.append(" | ");
                rep.append(p[1].getCardId() + " - "+ p[1].getCardName());
                rep.append("\n");
            }
            rep.append("--------------\n");
            return rep.toString();
        }
    }

    private void shuffleBag() {
        Collections.shuffle(gridPairs);
    }

    /**
     * @return the number of pairs left
     */
    public int getPairsLeft() {
        return gridPairs.size();
    }

    /**
     * @return a pair of Grid (Grid[]) extracted
     * @throws NoMoreBagElementException
     */
    public Grid[] extractPair() throws NoMoreBagElementException {
        if (gridPairs.isEmpty()) {
            throw new NoMoreBagElementException("Grid Pair");
        } else {
            Grid[] extracted = gridPairs.remove(gridPairs.size() - 1);
            return extracted;
        }
    }

    /**
     * @param n of pairs of Grids to extract
     * @return a List of pair of Grid (Grid[]) extracted
     * @throws NoMoreBagElementException
     */
    public List<Grid[]> extractPairs(int n) throws NoMoreBagElementException {
        List<Grid[]> extracted = new CopyOnWriteArrayList<Grid[]>();
        for (int i = 0; i < n; i++) {
            extracted.add(extractPair());
        }
        return extracted;
    }

    public void addGridPair(Grid [] p) {
        gridPairs.add(p);
        this.shuffleBag();
    }
}