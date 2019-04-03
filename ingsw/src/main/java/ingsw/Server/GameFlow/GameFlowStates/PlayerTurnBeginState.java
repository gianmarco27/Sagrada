package ingsw.Server.GameFlow.GameFlowStates;

import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Settings;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Initial state for a single player turn.
 * Handle the current turn possibility, like playing with a Tool or using a Die
 */
public class PlayerTurnBeginState extends GenericState {
    private static final Logger log = Logger.getLogger( PlayerTurnBeginState.class.getName() );
    private CountDownLatch waitChoice;
    private int playerChoice;
    private boolean needToEnd;

    private static final int TURN_LIMIT = 2;

    public PlayerTurnBeginState(GameFlowHandler gfh) {
        this.reference = gfh;
        this.state = GameFlowState.PLAYERTURNBEGIN;
    }

    @Override
    public void execute() {
        waitChoice = new CountDownLatch(1);

        if (reference.gameBoard.getCurrentPlayer().turnPlayed == TURN_LIMIT) {
            //log.info("You played quite some times already!");
            reference.setState(reference.playerTurnEndState);
            return;
        }
        if (!reference.gameBoard.getCurrentPlayer().isConnected()) {
            reference.setState(reference.playerTurnEndState);
            return;
        }
        reference.getGameBoard().pushUpdate();
        try {
            boolean playerAnswered = waitChoice.await(Settings.TURN_TIMEOUT,TimeUnit.SECONDS);
            if (!playerAnswered) {
                reference.disconnectPlayer(reference.getGameBoard().getCurrentPlayer(),true);
                log.info("Player '"+reference.getGameBoard().getCurrentPlayer().getName()+"' timed out, finishing the turn");
                this.needToEnd = true;
            }
        } catch (InterruptedException e) {
            log.warning("Error waiting for the player to choose what to do\n");
            Thread.currentThread().interrupt();
            System.exit(-1);
        }

        if (needToEnd || playerChoice == 0){
            reference.setState(reference.playerTurnEndState);
            needToEnd = false;
            return;
        }

        if (!reference.gameBoard.getCurrentPlayer().isDicePlayed() && playerChoice == 1) {
            reference.setState(reference.diceActionState);
            return;
        }
        if (!reference.gameBoard.getCurrentPlayer().isToolPlayed() && playerChoice == 2) {
            reference.setState(reference.toolActionState);
            return;
        }
        reference.setState(reference.playerTurnBeginState);
        //loop until user stops the round
        return;
    }

    /**
     * @param c value the client selected client-side
     */
    public void setChoice(int c){
        playerChoice = c;
        waitChoice.countDown();
    }

    public void endTurn(){
        this.needToEnd = true;
        waitChoice.countDown();
    }
}

