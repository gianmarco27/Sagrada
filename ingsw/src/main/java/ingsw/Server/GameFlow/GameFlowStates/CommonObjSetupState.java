package ingsw.Server.GameFlow.GameFlowStates;

import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Objective.PublicObjective;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This State is responsible for extracting and setting up the PublicObjective
 */
public class CommonObjSetupState extends GenericState {

    private static final Logger log = Logger.getLogger(CommonObjSetupState.class.getName());

    public CommonObjSetupState(GameFlowHandler gfh) {
        this.reference = gfh;
        this.state = GameFlowState.COMMONOBJSETUP;
    }

    @Override
    public void execute() {
        List<PublicObjective> objToAdd = new CopyOnWriteArrayList<>();
        for (int i = 0; i < reference.gameType.getPublicObjNumber(); i++) {
            try {
                PublicObjective extracted = reference.objectiveBag.getPublicObj();
                objToAdd.add(extracted);
            } catch (NoMoreBagElementException e) {
                log.warning("Error extracting PublicObjective : "+e.toString());
                System.exit(-1);
            }
        }
        reference.gameBoard.setPublicObjectives(objToAdd);

        setupPlayerAck();
        reference.gameBoard.pushUpdate();
        waitForPlayers();

        reference.setState(reference.turnHandlerState);
    }
}
