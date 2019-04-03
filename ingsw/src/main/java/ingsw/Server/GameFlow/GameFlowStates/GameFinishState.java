package ingsw.Server.GameFlow.GameFlowStates;

import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowState;

import java.util.logging.Logger;

/**
 * This State is the final game state that calculate the player score and gracefully turn of the game
 */
public class GameFinishState extends GenericState {
    private static final Logger log = Logger.getLogger( GameFinishState.class.getName() );

    public GameFinishState(GameFlowHandler gfh) {
        this.reference = gfh;
        this.state = GameFlowState.GAMEFINISH;
    }

    @Override
    public void execute() {
        log.warning("Finished Game, calculating score");
        reference.gameBoard.calculateScore();
        setupPlayerAck();
        reference.getGameBoard().pushUpdate();
        waitForPlayers();
        log.warning("Ending game...");
        reference.setGameStatus(true); // TA-DA!
    }
}
