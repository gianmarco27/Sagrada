package ingsw.Server.GameFlow.GameFlowStates;

import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Objective.PrivateObjective;
import ingsw.Server.Player;

import java.util.logging.Logger;

/**
 * This State is responsible for extracting and setting up the PrivateObjective
 */
public class PrivateObjSetupState extends GenericState {
    private static final Logger log = Logger.getLogger( PrivateObjSetupState.class.getName() );
    public PrivateObjSetupState(GameFlowHandler gfh) {
        this.reference = gfh;
        this.state = GameFlowState.PRIVATEOBJSETUP;
    }

    @Override
    public void execute() {
        //log.info("Executing PrivateObjectiveSetupState");

        for (Player p : reference.gameBoard.getPlayers()) {
            try {
                for (int idx = 0; idx < reference.gameType.getPrivateObjNumber(); idx ++) {
                    PrivateObjective extracted = reference.objectiveBag.getPrivateObj();
                    reference.gameBoard.setPrivateObjective(p, extracted);
                }
            } catch (NoMoreBagElementException e) {
                log.warning("No more private objective : " + e.toString());
                System.exit(-1);
            }
        }

        setupPlayerAck();
        reference.gameBoard.pushUpdate();
        waitForPlayers();

        reference.setState(reference.gridSetupState);
    }
}
