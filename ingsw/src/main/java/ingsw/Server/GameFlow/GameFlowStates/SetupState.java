package ingsw.Server.GameFlow.GameFlowStates;

import ingsw.Server.Dice.DiceBag;
import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Grid.GridBag;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Objective.ObjectiveBag;
import ingsw.Server.Objective.PrivateObjective;
import ingsw.Server.Objective.PublicObjective;
import ingsw.Server.Player;
import ingsw.Server.Tools.Tool;
import ingsw.Server.Tools.ToolsBag;
import ingsw.Settings;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * This State is responsible for setting up the server-side game
 * It instantiate the bags that will be used during the game.
 */
public class SetupState extends GenericState {

    private static final Logger log = Logger.getLogger( SetupState.class.getName() );

    public SetupState(GameFlowHandler gfh) {
        this.reference = gfh;
        this.state = GameFlowState.SETUP;
    }

    @Override
    public void execute() {
        reference.diceBag = new DiceBag();
        reference.objectiveBag = new ObjectiveBag();
        reference.toolsBag = new ToolsBag(reference.gameType);
        reference.gridsBag = new GridBag();
        reference.getGameBoard().setDiceBag(reference.diceBag);
        setupPlayerAck();
        reference.getGameBoard().pushUpdate();
        waitForPlayers();
        reference.setState(reference.privateObjeSetupState);
    }


}
