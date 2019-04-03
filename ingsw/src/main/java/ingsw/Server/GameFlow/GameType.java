package ingsw.Server.GameFlow;


import ingsw.Settings;

import java.io.Serializable;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Enum containing all the available GameType
 * It store the custom setting for each gameMode in the GameType enum attributes so that
 * the most of the difference in number of cards and dice are stored in the corresponding diceType
 */
public enum GameType implements Serializable {
    SINGLEPLAYER, MULTIPLAYER;

    private int toolCards;
    private int publicObjective;
    private int privateObjective;
    private int diceEachPlayer;

    private static int MIN_SP_D = 1;
    private static int MAX_SP_D = 5;

    static {
        // Singleplayer settings
        SINGLEPLAYER.publicObjective = 2;
        SINGLEPLAYER.privateObjective = 2;
        SINGLEPLAYER.toolCards = min(max(MIN_SP_D, Settings.SINGLE_PLAYER_DIFFICULTY), MAX_SP_D); // N° of usable tools in sp
        SINGLEPLAYER.diceEachPlayer = 3;

        // Multiplayer settings
        MULTIPLAYER.publicObjective = 3;
        MULTIPLAYER.privateObjective = 1;
        MULTIPLAYER.toolCards = 3;
        MULTIPLAYER.diceEachPlayer = 2;
    }

    public int getDifficulty() {
        return toolCards;
    }

    public int getPublicObjNumber() {
        return publicObjective;
    }

    public int getPrivateObjNumber() {
        return privateObjective;
    }

    public int getDiceNumber() {
        return diceEachPlayer;
    }

}
