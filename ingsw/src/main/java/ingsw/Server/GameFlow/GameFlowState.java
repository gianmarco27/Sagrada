package ingsw.Server.GameFlow;

/**
 * Enumerate that contains the name of all the State available in the State machine
 */
public enum GameFlowState {
    PLAYERREGISTRATION,
    SETUP,
    PRIVATEOBJSETUP,
    GRIDSETUP,
    TOOLSETUP,
    COMMONOBJSETUP,
    TURNHANDLER,
    PLAYERTURNBEGIN,
    DICEACTION,
    TOOLACTION,
    PLAYERTURNEND,
    GAMEFINISH
}
