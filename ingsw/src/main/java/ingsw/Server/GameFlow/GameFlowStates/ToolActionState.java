package ingsw.Server.GameFlow.GameFlowStates;

import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Server.Tools.Tool;
import ingsw.Server.Utility.GameException;
import ingsw.Settings;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * This State is responsible for using a Tool, it handle the ToolActionParameter
 * It provide feedback to the client if the action has been successful or the next step for tool which require it
 */
public class ToolActionState extends GenericState {
    private static final Logger log = Logger.getLogger( ToolActionState.class.getName() );

    CountDownLatch waitChoice;
    private boolean needToEnd;

    public ToolActionState(GameFlowHandler gfh) {
        this.reference = gfh;
        this.state = GameFlowState.TOOLACTION;
    }

    @Override
    public void execute() {
        reference.gameBoard.pushUpdate();
        waitChoice = new CountDownLatch(1);
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

        if(needToEnd){
            reference.setState(reference.playerTurnEndState);
            needToEnd = false;
            return;
        }

        /*setupPlayerAck();
        reference.gameBoard.pushUpdate();
        waitForPlayers();
        */

        reference.setState(reference.playerTurnBeginState);
    }

    /**
     * @param tap ToolActionParameter with all how the client would like to use the tool
     * @return if the operation was successful
     */
    public boolean useTap(ToolActionParameter tap) {

        Tool pickedTool = null;
        for (Tool t : reference.gameBoard.getTools()) {
            if (t.getToolNumber() == tap.getPickedTool()) {
                pickedTool = t;
            }
        }

        if (pickedTool == null) {
            throw new GameException("Cannot find the chosen tool.");
        }

        if (!pickedTool.isUsable(tap, reference.gameBoard)) {
            log.warning("Tool not usable");
            throw new GameException("The tool is not usable.");
        } else {
            GameException badLuck = null;
            try {
                pickedTool.use(tap, reference.gameBoard);
            } catch (GameException e) {
                if (!e.isCallBack()) {
                    badLuck = e;
                } else {
                    throw e;
                }
            }
            log.info("Tool used" + pickedTool);
            reference.gameBoard.getCurrentPlayer().setToolPlayed(true);
            waitChoice.countDown();
            log.info("Player correctly picked and used a tool");
            if(badLuck != null) {
                throw badLuck;
            } else
                return true;
        }
    }

    public boolean back() {
        waitChoice.countDown();
        return true;
    }

    public void endTurn(){
        this.needToEnd = true;
        waitChoice.countDown();
    }
}
