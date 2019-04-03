package ingsw.Server.GameFlow.GameFlowStates;

import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Player;
import ingsw.Server.Utility.GameException;
import ingsw.Server.Utility.RegistrationLatch;
import ingsw.Settings;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * State that handle the lobby and the player registration
 * Implements the game logic rules related to how the pre-match should behave
 */
public class PlayerRegistrationState extends GenericState {
    private static final Logger log = Logger.getLogger( PlayerRegistrationState.class.getName() );

    CountDownLatch otherPlayersLatch = new CountDownLatch(3);
    CountDownLatch firstPlayerLatch = new CountDownLatch(1);
    RegistrationLatch lobbyLatch = new RegistrationLatch(Settings.LOBBY_TIMEOUT);

    public PlayerRegistrationState(GameFlowHandler gfh) {
        this.reference = gfh;
        this.state = GameFlowState.PLAYERREGISTRATION;
    }

    /**
     * @param p player to remove from the lobby
     */
    public synchronized void removePlayer(Player p) {
        if (lobbyLatch.getCount() > 0) {
            if (reference.getGameBoard().removePlayer(p)) {
                lobbyLatch.removePlayer();
            }
        }
    }

    /**
     * @param name which the player would like to use for register
     * @throws GameException explaining the reason behind an invalid requests
     */
    public synchronized Player registerPlayer(String name,GameType gameType) throws GameException {
        log.log(Level.INFO, "Received registration request with name : " + name);
        Player newPlayer = new Player(name);
        if (gameType == GameType.SINGLEPLAYER) {
            if (lobbyLatch.getCount() > 0) {
                throw new GameException("Cannot play single player, someone is already in the lobby.");
            } else {
                reference.gameBoard.addPlayer(newPlayer);
                lobbyLatch.addSinglePlayer();
            }
        } else {
            if (lobbyLatch.getCount() == 4) {
                throw new GameException("The game is currently full!");
            }
            reference.gameBoard.addPlayer(newPlayer);
            lobbyLatch.addPlayer();
        }
        return newPlayer;
    }


    @Override
    public void execute() {
        try {
            lobbyLatch.waitForStarting();
            if (lobbyLatch.getCount() ==1 ) {
                reference.setGameType(GameType.SINGLEPLAYER);
                log.log(Level.INFO, "SINGLEPLAYER MODE SELECTED");
            } else {
                reference.setGameType(GameType.MULTIPLAYER);
                log.log(Level.INFO, "MULTIPLAYER MODE SELECTED");
            }
        } catch (InterruptedException e) {
            log.log(Level.WARNING,"Something is not working properly with the latches from the players");
            Thread.currentThread().interrupt();
            }
        reference.getGameBoard().setPlayers();
        setupPlayerAck();
        reference.getGameBoard().pushUpdate();
        waitForPlayers();

        reference.setState(reference.setupState);
    }

}
