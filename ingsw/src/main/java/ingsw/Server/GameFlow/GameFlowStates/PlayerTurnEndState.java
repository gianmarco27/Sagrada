package ingsw.Server.GameFlow.GameFlowStates;

import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Server.Player;


/**
 * Tear-Down state for the current player.
 * Reset the available action so that he can use them again next turn
 */
public class PlayerTurnEndState extends GenericState{

    public PlayerTurnEndState(GameFlowHandler gfh) {
        this.reference = gfh;
        this.state = GameFlowState.PLAYERTURNEND;
    }

    @Override
    public void execute() {
//        System.out.println("End of turn for " + reference.gameBoard.getCurrentPlayer() + "\n-\n");
        Player currentPlayer = reference.gameBoard.getCurrentPlayer();

        int alreadyPlayed = currentPlayer.getTurnPlayed();
        currentPlayer.setTurnPlayed(alreadyPlayed+1);
        currentPlayer.setDicePlayed(false);
        currentPlayer.setToolPlayed(false);

        setupPlayerAck();
        reference.getGameBoard().pushUpdate();
        waitForPlayers();

        reference.setState(reference.turnHandlerState);
    }
}
