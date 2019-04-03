package ingsw.Server.GameFlow.GameFlowStates;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Player;
import ingsw.Server.Utility.RoundTracker;
import ingsw.Settings;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This State is the state that wrap around the whole turn-manager logic.
 * It makes the game switch from a player to next one and terminate the match when we gat to the turn_limi
 */
public class TurnHandlerState extends GenericState {
    private static final Logger log = Logger.getLogger( TurnHandlerState.class.getName() );

    public TurnHandlerState(GameFlowHandler gfh) {
        this.reference = gfh;
        this.state = GameFlowState.TURNHANDLER;
        reference.startOfTurn = true;
    }

    @Override
    public void execute() {
        int currentTurn = reference.gameBoard.getCurrentTurn();
        if (reference.gameBoard.getActivePlayers().size()==1 && reference.gameBoard.getGameType() == GameType.MULTIPLAYER){ //last player connected need to win
            reference.setState(reference.gameFinishState);
            return;
        }
        if (currentTurn == Settings.TURN_NUMBER) {  // If this is the last turn...
            reference.setState(reference.gameFinishState);
            return;
        } else if (reference.startOfTurn) {
            reference.startOfTurn = false;
            diceSetup();
            reference.setState(reference.playerTurnBeginState);
            return;
        }
        if (reference.gameBoard.nextPlayer()) {
            reference.setState(reference.playerTurnBeginState);
            return;
        } else { // End of turn
            for (Player p : reference.gameBoard.getPlayers()) { // Clear turn played by each person
                p.turnPlayed = 0;
            }
            reference.startOfTurn = true;

            DicePool dp = reference.gameBoard.getDicePool();
            RoundTracker rt = reference.gameBoard.getRoundTracker();

            for (Dice d : dp.getAllDice()) {
                // Put all remaining dice in the roundTracker
                rt.placeDice(currentTurn, d);
            }

            dp.emptyDicePool();

            reference.setState(reference.turnHandlerState);
            reference.gameBoard.nextTurn();
            return;
        }
    }

    /**
     * Private helper function which fill the dicePool back at the start of a turn
     */
    private void diceSetup() {
        List<Dice> ext = new CopyOnWriteArrayList<>();
        try {
            int nPlayers = reference.gameBoard.getPlayers().size();
            int diceNumber = reference.gameType.getDiceNumber();
            ext = reference.diceBag.extractDice(nPlayers * diceNumber + 1);
        } catch (NoMoreBagElementException e) {
            System.exit(-1);
        }
        for (Dice d : ext) {
            reference.gameBoard.getDicePool().addDice(d);
        }
        setupPlayerAck();
        reference.gameBoard.pushUpdate();
        waitForPlayers();
    }
}

