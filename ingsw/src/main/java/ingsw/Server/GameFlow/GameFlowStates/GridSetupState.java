package ingsw.Server.GameFlow.GameFlowStates;

import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Server.Grid.Grid;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Player;
import ingsw.Server.Utility.GameException;
import ingsw.Settings;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This State is responsible for handling the initial grid extraction and player related choice
 *
 */
public class GridSetupState extends GenericState {
    private static final Logger log = Logger.getLogger( GridSetupState.class.getName() );
    CountDownLatch waitPlayers;
    private boolean chooseOpen;

    public GridSetupState(GameFlowHandler gfh) {
        this.reference = gfh;
        this.state = GameFlowState.GRIDSETUP;
    }

    @Override
    public void execute() {
        assignPossibleGrids();
        this.chooseOpen = true;
        reference.gameBoard.pushUpdate();
        waitPlayers = new CountDownLatch(reference.gameBoard.getActivePlayers().size());
        try {
            boolean playerAnswered = !waitPlayers.await(Settings.TURN_TIMEOUT,TimeUnit.SECONDS);
            this.chooseOpen = false;
            if (!playerAnswered) {
                List<Player> notChosenGrid = reference.getGameBoard().getPlayers().stream().filter(p -> p.getGrid() == null).collect(Collectors.toList());
                for(Player p : notChosenGrid) {
                    log.info("Player "+p.getName()+" timed out, disconnecting.");
                    reference.disconnectPlayer(p,true);
                }
            }
        } catch (InterruptedException e) {
            log.warning("Error waiting for players to choose the grid\n");
            Thread.currentThread().interrupt();
            System.exit(-1);
        }

        setupPlayerAck();
        reference.gameBoard.pushUpdate();
        waitForPlayers();

        reference.setState(reference.toolsSetupState);
    }

    /**
     * @param p - player who selected a choice
     * @param g - grid selected by the player
     * @return success of the operation
     *
     * Check if the grid selected by the player is actually usable and, if so, assign it to the player
     */
    public boolean chooseGrid(Player p, Grid g) {
        if (!chooseOpen)
            throw new GameException("Cannot register in this state of the game!");
        Player localPlayer;
        try {
            localPlayer = reference.gameBoard.getPlayers().stream().filter( player -> player.equals(p)).collect(Collectors.toList()).get(0);
        } catch(Exception e) {
            log.warning("Cannot find the supplied player\n");
            return false;
        }
        if (localPlayer == null) {
            log.warning("Player is still null\n");
            return false;
        }
        log.info("Received valid grid choice from player " + p.getName());
        localPlayer.setGrid(g);
        waitPlayers.countDown();
        return true;
    }

    /**
     * Assign to each player the grid from which he should chose
     */
    private void assignPossibleGrids() {
        for (Player p : reference.gameBoard.getPlayers()) {
            Grid[] choice = new Grid[4];
            try {
                Grid[] temp = reference.gridsBag.extractPair();
                choice[0] = temp[0];
                choice[1] = temp[1];
                temp = reference.gridsBag.extractPair();
                choice[2] = temp[0];
                choice[3] = temp[1];
                p.setPossibleGrids(choice);
            } catch (NoMoreBagElementException e) {
                log.warning("No more grids to play with, cya artists\n");
                log.warning(Arrays.toString(e.getStackTrace()));
                System.exit(-1);
            }
        }
    }
}
