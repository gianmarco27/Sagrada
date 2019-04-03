package ingsw.Server.GameFlow.GameFlowStates;

import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Player;
import ingsw.Settings;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * GenericState, subclasse by all other specific states
 * Part of the StateMachine that handle the game flow and all the possible interactions
 */
public abstract class GenericState {
    private static final Logger log = Logger.getLogger( GenericState.class.getName());
    protected GameFlowHandler reference;
    protected GameFlowState state;
    protected List<Player> currentlyConnected;
    CountDownLatch playerAck;
    List<Player> playerUpToDate;


    /**
     * Function that all state of the state machine implements
     * Contains the basic logic of how a certain state operate and is called with polymorphism
     */
    public abstract void execute();

    /**
     * @return GameFlowState of the current active state
     */
    public GameFlowState getState() {
        return state;
    }

    /**
     * @param state
     * Setter used for transitioning between states
     */
    public void setState(GameFlowState state) {
        this.state = state;
    }

    public void waitForPlayers() {
        try {
            if (!playerAck.await(Settings.TURN_TIMEOUT, TimeUnit.SECONDS)) {
                // Functional Java to check which players have yet to be acknowledged
                List<Player> notAcked = reference.getGameBoard().getPlayers().stream().filter(p -> !playerUpToDate.contains(p)).collect(Collectors.toList());
                for (Player p : notAcked){
                    log.info("Player " + p.getName()+" timed out during an ack, disconnecting.");
                    reference.disconnectPlayer(p,true);
                }
            }
        } catch (InterruptedException e) {
            log.warning("Error waiting for players in " + this.state + " : " + e.toString());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Helper function to handle the acknowledge that all players are up-to-date
     */
    protected void setupPlayerAck() {
        this.currentlyConnected = reference.getGameBoard().getActivePlayers();
        this.playerAck = new CountDownLatch(currentlyConnected.size());
        this.playerUpToDate = new CopyOnWriteArrayList<>();
    }

    /**
     * @param p
     * Update the list containing the players which are up to date with the current changes
     */
    public void ack(Player p) {
        if (playerUpToDate == null || playerAck == null) {
            return;
        }
        if (!playerUpToDate.contains(p) && currentlyConnected.contains(p)) {
            this.playerUpToDate.add(p);
            playerAck.countDown();
        }
    }

    /**
     * endTurn is the method used for gracefully terminate a state if the user disconnect/timeout during it
     */
    public void endTurn() {
    }
}
