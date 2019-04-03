package ingsw.TestServer.GameFlow;

import ingsw.Server.Actions.DiceActionParameter;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceBag;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowStates.DiceActionState;
import ingsw.Server.GameFlow.GameFlowStates.PlayerTurnBeginState;
import ingsw.Server.GameFlow.GameFlowStates.TurnHandlerState;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Player;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

import static ingsw.Server.Dice.DiceColor.BLUE;
import static ingsw.Server.Dice.DiceColor.RED;

public class DiceActionStateTest {
    GameFlowHandler gameFH = new GameFlowHandler();
    GameBoardMocker gbMock = new GameBoardMocker();
    GameBoard gb = gbMock.getGameBoard(1, 4);

    public DiceActionStateTest() {
        gameFH.gameBoard = gb;
        gameFH.setState(gameFH.diceActionState);
        gameFH.setGameType(GameType.MULTIPLAYER);
    }

    @Test
    public void testDiceActionSelection() {
        gameFH.diceBag = new DiceBag();
        gb.getDicePool().addDice(new Dice(RED));
        gb.getDicePool().addDice(new Dice(BLUE));
        DiceActionParameter dap = new DiceActionParameter();
        dap.chooseDie(0);
        dap.choseTarget(3, 0);

        Thread execution = new Thread(() -> gameFH.getCurrent().execute());
        execution.start();

        Thread playerChoiche = new Thread(() -> {
            Boolean executed = false;
            while (execution.isAlive() && !executed) {
                if (execution.getState() == Thread.State.TIMED_WAITING) {
                    ((DiceActionState) gameFH.getCurrent()).setChoice(dap);
                    executed = true;
                }
            }
        });
        playerChoiche.start();

        try {
            execution.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(gameFH.playerTurnBeginState, gameFH.getCurrent());

    }

    @Test
    public void testDiceActionBack() {
        gameFH.diceBag = new DiceBag();

        Thread execution = new Thread(() -> gameFH.getCurrent().execute());
        execution.start();

        Thread back = new Thread(() -> {
            Boolean executed = false;
            while (execution.isAlive() && !executed) {
                if (execution.getState() == Thread.State.TIMED_WAITING) {
                    ((DiceActionState) gameFH.getCurrent()).back();
                    executed = true;
                }
            }
        });
        back.start();

        try {
            execution.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(gameFH.playerTurnBeginState, gameFH.getCurrent());
    }

    @Test
    public void testDiceActionEndTurn() {
        gameFH.diceBag = new DiceBag();

        Thread execution = new Thread(() -> gameFH.getCurrent().execute());
        execution.start();

        Thread needToEnd = new Thread(() -> {
            Boolean executed = false;
            while (execution.isAlive() && !executed) {
                if (execution.getState() == Thread.State.TIMED_WAITING) {
                    ((DiceActionState) gameFH.getCurrent()).endTurn();
                    executed = true;
                }
            }
        });
        needToEnd.start();

        try {
            execution.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(gameFH.playerTurnEndState, gameFH.getCurrent());
    }
}
