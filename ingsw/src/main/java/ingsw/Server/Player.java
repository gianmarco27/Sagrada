package ingsw.Server;

import ingsw.Server.Grid.Grid;
import ingsw.Server.Utility.GameException;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class containing the information related to a given Player
 */
public class Player implements Serializable {

    private String name;
    private int favorToken;
    private boolean connected;
    public int turnPlayed;
    private boolean dicePlayed;
    private boolean toolPlayed;
    private Grid grid;
    private Grid[] possibleGrids;

    public Player(String name) {
        this.name = name;
        this.connected = true;
    }

    @Override
    public String toString() {
        return "P: " + name.toUpperCase() + " | " + favorToken;
    }


    public String getName() {
        return name;
    }

    public void setToken(int toSet) {
        this.favorToken = toSet;
    }

    public int getToken() {
        return favorToken;
    }

    public void setGrid(Grid toSet) {
        this.grid = toSet;
        setToken(this.grid.getFavorPoints());
    }

    /**
     * @return the number of turn a player has played this round
     */
    public int getTurnPlayed() {
        return turnPlayed;
    }

    /**
     * Setter to specify the number of turn a player has played this round
     */
    public void setTurnPlayed(int turnPlayed) {
        this.turnPlayed = turnPlayed;
    }

    /**
     * @return if the player used any dice this turn
     */
    public boolean isDicePlayed() {
        return dicePlayed;
    }

    /**
     * Setter to specify if a player has used a die this turn
     */
    public void setDicePlayed(boolean dicePlayed) {
        this.dicePlayed = dicePlayed;
    }

    /**
     * @return if the player used any tool this turn
     */
    public boolean isToolPlayed() {
        return toolPlayed;
    }

    /**
     * Setter to specify if a player has used a tool this turn
     */
    public void setToolPlayed(boolean toSet) {
        this.toolPlayed = toSet;
    }

    /**
     * @return the player grid
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * @param toSpend number of token to remove from the current players
     */
    public void spendToken(int toSpend) {
        if (toSpend > favorToken) {
            throw new GameException("Not enough token"); // Unlikely since we check beforehand
        } else {
            favorToken -= toSpend;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Player)) {
            return false;
        }
        Player other = (Player) o;
        return this.name.toUpperCase().equals(other.getName().toUpperCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * @return the possibile grids from which the user can pick in the starting-up phase
     */
    public Grid[] getPossibleGrids() {
        return possibleGrids;
    }

    /**
     * @param possibleGrids, set up the possibile grids from which the user can pick in the starting-up phase
     */
    public void setPossibleGrids(Grid[] possibleGrids) {
        this.possibleGrids = possibleGrids;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
