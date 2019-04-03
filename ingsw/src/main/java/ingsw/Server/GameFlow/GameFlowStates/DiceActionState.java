package ingsw.Server.GameFlow.GameFlowStates;

import ingsw.Server.Actions.DiceActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Utility.GameException;
import ingsw.Settings;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * This State is responsible for handling a user action related to placing a die in the grid
 * It works with the DiceActionParameter and handle the placements accordingly
 */
public class DiceActionState extends GenericState {
    private static final Logger log = Logger.getLogger( DiceActionState.class.getName() );
    private CountDownLatch waitChoice;
    private boolean needToEnd;

    public DiceActionState(GameFlowHandler gfh) {
        this.reference = gfh;
        this.state = GameFlowState.DICEACTION;
    }

    @Override
    public void execute() {
        waitChoice = new CountDownLatch(1);
        reference.getGameBoard().pushUpdate();
        try {
            boolean playerAnswered = waitChoice.await(Settings.TURN_TIMEOUT,TimeUnit.SECONDS);
            if (!playerAnswered) {
                reference.disconnectPlayer(reference.getGameBoard().getCurrentPlayer(),true);
                this.needToEnd = true;
            }
        } catch (InterruptedException e) {
            log.warning("Error waiting for the player to choose what to do\n");
            Thread.currentThread().interrupt();
            System.exit(-1);
        }

        if (needToEnd) {
            reference.setState(reference.playerTurnEndState);
            needToEnd = false;
            return;
        }
        reference.setState(reference.playerTurnBeginState);
        return;
    }

    /**
     * @param p DiceActionParameter holding all the information used to make the placements
     * @return if the operation was successful
     * @throws GameException explaining the reasoning behind a potential invalid move
     */
    public boolean setChoice(DiceActionParameter p) throws GameException {
        if (p.getPicked_idx() < 0 || p.getPicked_idx() > reference.gameBoard.getDicePool().getAllDice().size()) {
            throw new GameException("The chosen dice does not exists.");
        }
        try {
            Dice d = reference.gameBoard.getDicePool().peekDice(p.getPicked_idx());
            boolean success = reference.getGameBoard().getCurrentPlayer().getGrid().placeDie(d, p.getTarget_x(), p.getTarget_y());
            if (success) {
                reference.gameBoard.getCurrentPlayer().setDicePlayed(true);
                reference.gameBoard.getDicePool().getSingleDice(p.getPicked_idx()); // Actually remove the dice from the dicePool
                waitChoice.countDown();
            }
            return success;
        } catch (NoMoreBagElementException e) {
            System.out.println("This was not supposed to happen");
            log.warning(Arrays.toString(e.getStackTrace()));
            System.exit(-1);
        }
        return false;
    }

    public boolean back() {
        waitChoice.countDown();
        return true;
    }

    public void endTurn() {
        this.needToEnd = true;
        waitChoice.countDown();
    }
}