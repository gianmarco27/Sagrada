package ingsw.Server.Utility;

import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Objective.PublicObjective;
import ingsw.Server.Player;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ingsw.Server.Utility.RoundTracker.ROUNDMAX;
import static java.lang.Math.max;
import static java.util.Collections.reverseOrder;
import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Object responsible to handle the player scores calculation. It provides a way to receive the players score and the ranking position!
 */
public class ScoreBoard implements Serializable {

    HashMap<Player, Integer> scoreMap;
    int roundTrackerScore;
    GameType gameType;

    /**
     * Constructor that initializes the HashMap that associates the Player to the score
     */
    public ScoreBoard() {
        this.scoreMap = new HashMap<>();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /**
     * @return the string representation of the scoreboard
     */
    public String toString(boolean colored) {
        if (this.scoreMap.size() == 0) {
            return "Empty Player ScoreBoard";
        }
        if (gameType == GameType.MULTIPLAYER) {
            return toStringMP(colored);
        } else if (gameType == GameType.SINGLEPLAYER) {
            return toStringSP(colored);
        }
        return ""; // Cover edge case, which should never happen...
    }

    public String toStringMP(boolean colored) {
        Ansi.Color[] scoreBoardColor = new Ansi.Color[] { YELLOW, BLUE, RED, GREEN };
        if (colored) {
            System.setProperty("jansi.passthrough", "true");
            AnsiConsole.systemInstall();
        }
        HashMap<Player, Integer> sortedMap = getPlayerOrder();
        StringBuilder rep = new StringBuilder();
        int position = 0;
        rep.append("###  F I N A L    POSITION  ###\n");
        for (Player p : sortedMap.keySet()) {
            if(p.isConnected()) {
                position++;
                rep.append(
                        ((colored) ?
                                ansi().bg(scoreBoardColor[position - 1]).fg(BLACK).a("| N°" + (position) + " |").reset().toString() :
                                "N°" + (position)) +
                                " : " + p.getName() + " SCORE: " + sortedMap.get(p) + "\n");
            }
        }
        for (Player p : sortedMap.keySet()) {
            if(!p.isConnected()) {
                position++;
                rep.append(
                        ((colored) ?
                                ansi().bg(scoreBoardColor[position - 1]).fg(BLACK).a("| N°" + (position) + " |").reset().toString() :
                                "N°" + (position)) +
                                " : " + p.getName() + " - disconnected SCORE: " + sortedMap.get(p) + "\n");
            }
        }
        return rep.toString();
    }

    public String toStringSP(boolean colored) {
        Player singlePlayer = (Player) scoreMap.keySet().toArray()[0];
        int playerScore = scoreMap.get(singlePlayer);
        if (playerScore >= roundTrackerScore) {
            return "# YOU WON! #\n\nYou scored an impressive " + playerScore + "\n\tVS\n" + "An insignificant " + roundTrackerScore;
        } else {
            return "# YOU LOST! #\n\nYou scored a measly " + playerScore + "\n\tVS\n" + "Score to beat " + roundTrackerScore;
        }
    }
    /**
     * Calculates the score of all the players from a given gameBoard state and saves it in a Map
     * @param gameBoard
     */
    public void calculateScore(GameBoard gameBoard, GameType gameType) {
        this.gameType = gameType;
        for (Player p : gameBoard.getPlayers()) {
            int currentScore = 0;
            Grid grid = p.getGrid();
            for (PublicObjective publicObjective : gameBoard.getPublicObjectives()) {
                currentScore += publicObjective.getScore(grid);
            }
            currentScore += calculatePrivateObjective(gameBoard, p);
            currentScore += (gameType == gameType.SINGLEPLAYER) ? 0 : p.getToken();
            currentScore += -20 * ((gameType == GameType.SINGLEPLAYER) ? 3 : 1) + grid.getDiceNum() * ((gameType == GameType.SINGLEPLAYER) ? 3 : 1);
            scoreMap.put(p, currentScore);
            roundTrackerScore = calculateScoreTracker(gameBoard.getRoundTracker());
        }
    }

    /**
     * Helper function that take care of calculating the portion of score from the private Objective
     * */
    private int calculatePrivateObjective(GameBoard gb, Player p) {
        Grid pGrid = p.getGrid();
        if (gameType == GameType.MULTIPLAYER) {
            return gb.getPrivateObjective(p).getScore(pGrid);
        } else if (gameType == GameType.SINGLEPLAYER) {
            int firstObj = gb.getAllPrivateObjective(p).get(0).getScore(pGrid);
            int seconObj = gb.getAllPrivateObjective(p).get(1).getScore(pGrid);
            // We take care of just returning the most influential portion of the score since the player has to pick one!
            return max(firstObj, seconObj);
        }
        return 0; // Edge case to cover if we intend to develop other gameType
    }

    /**
     * Calculates the score in a singlePlayer game from a given gameBoard state and a roundTracker
     * @param roundTracker
     */
    public int calculateScoreTracker(RoundTracker roundTracker) {
        int currentScore = 0;
        for (int round = 0; round < ROUNDMAX; round++) {
            int roundSize = roundTracker.peekPosition(round).size();
            for (int idx = 0; idx < roundSize; idx++) {
                currentScore += roundTracker.peekPosition(round, idx).getValue();
            }
        }
        return currentScore;
    }
    /**
     * @return an HashMap where the keys are the player sorted by their score.
     */
    public HashMap<Player, Integer> getPlayerOrder() {
        if (this.scoreMap.isEmpty()) {
            return null;
        } else {
            return this.scoreMap.entrySet().stream()
                    .sorted(reverseOrder(Map.Entry.comparingByValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (e1, e2) -> e1, LinkedHashMap::new));
        }
    }

    /**
     * @return the sum of the value of the dice in the scoreBoard
     */
    public int getRoundTrackerScore() {
        return roundTrackerScore;
    }
}
