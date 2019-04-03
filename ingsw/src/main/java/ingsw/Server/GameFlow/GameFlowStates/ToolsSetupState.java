package ingsw.Server.GameFlow.GameFlowStates;

import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Tools.Tool;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This State is responsible for extracting and setting up the Tools
 */
public class ToolsSetupState extends GenericState {

    private static final Logger log = Logger.getLogger(ToolsSetupState.class.getName());
    public ToolsSetupState(GameFlowHandler gfh) {
        this.reference = gfh;
        this.state = GameFlowState.TOOLSETUP;
    }

    @Override
    public void execute() {

        List<Tool> toolsToAdd = new CopyOnWriteArrayList<>();
        for (int i = 0; i < reference.gameType.getDifficulty(); i++) {
            try {
                Tool extracted = reference.toolsBag.getTools();
                toolsToAdd.add(extracted);
            } catch (NoMoreBagElementException e) {
                log.warning("Cannot extract enough tools from bag : " + e.toString());
                System.exit(-1);
            }
        }
        reference.getGameBoard().setTools(toolsToAdd);

        setupPlayerAck();
        reference.gameBoard.pushUpdate();
        waitForPlayers();

        reference.setState(reference.commonObjSetupState);
    }
}
