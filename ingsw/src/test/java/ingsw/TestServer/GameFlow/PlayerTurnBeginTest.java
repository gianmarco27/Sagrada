package ingsw.TestServer.GameFlow;

import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowStates.PlayerTurnBeginState;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

public class PlayerTurnBeginTest {
    GameFlowHandler gameFH = new GameFlowHandler();
    GameBoardMocker gbMock = new GameBoardMocker();
    GameBoard gb = gbMock.getGameBoard(1, 4);

    public PlayerTurnBeginTest() {
        gameFH.gameBoard = gb;
        gameFH.setState(gameFH.playerTurnBeginState);
    }

    @Test
    public void testPlayerLimitTurnFlow() {
        gb.getCurrentPlayer().turnPlayed = 2;

        Thread execution = new Thread(() -> gameFH.getCurrent().execute());
        execution.start();

        Thread stateExecution = new Thread(() -> {
            while (execution.isAlive()) {
                if (execution.getState() == Thread.State.TIMED_WAITING) {
                    ((PlayerTurnBeginState) gameFH.getCurrent()).setChoice(1);
                }
            }
        });
        stateExecution.start();

        try {
            execution.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(gameFH.playerTurnEndState, gameFH.getCurrent());
    }

    @Test
    public void testPlayerDisconnected() {
        gb.getCurrentPlayer().setConnected(false);

        gameFH.getCurrent().execute();

        Assert.assertEquals(gameFH.playerTurnEndState, gameFH.getCurrent());
    }

    @Test
    public void testPlayerDiceTurnFlow() {

        Thread execution = new Thread(() -> gameFH.getCurrent().execute());
        execution.start();

        Thread stateExecution = new Thread(() -> {
            while (execution.isAlive()) {
                if (execution.getState() == Thread.State.TIMED_WAITING) {
                    ((PlayerTurnBeginState) gameFH.getCurrent()).setChoice(1);
                }
            }
        });
        stateExecution.start();

        try {
            execution.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(gameFH.diceActionState, gameFH.getCurrent());
    }

    @Test
    public void testPlayerToolTurnFlow() {

        Thread execution = new Thread(() -> gameFH.getCurrent().execute());
        execution.start();

        Thread stateExecution = new Thread(() -> {
            while (execution.isAlive()) {
                if (execution.getState() == Thread.State.TIMED_WAITING) {
                    ((PlayerTurnBeginState) gameFH.getCurrent()).setChoice(2);
                }
            }
        });
        stateExecution.start();

        try {
            execution.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(gameFH.toolActionState, gameFH.getCurrent());
    }

    @Test
    public void testPlayerEndTurnFlow() {

        Thread execution = new Thread(() -> gameFH.getCurrent().execute());
        execution.start();

        Thread stateExecution = new Thread(() -> {
            while (execution.isAlive()) {
                if (execution.getState() == Thread.State.TIMED_WAITING) {
                    ((PlayerTurnBeginState) gameFH.getCurrent()).setChoice(0);
                }
            }
        });
        stateExecution.start();

        try {
            execution.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(gameFH.playerTurnEndState, gameFH.getCurrent());
    }

    @Test
    public void testForceEndTurnFlow() {

        Thread execution = new Thread(() -> gameFH.getCurrent().execute());
        execution.start();

        Thread stateExecution = new Thread(() -> {
            while (execution.isAlive()) {
                if (execution.getState() == Thread.State.TIMED_WAITING) {
                    gameFH.getCurrent().endTurn();
                }
            }
        });
        stateExecution.start();

        try {
            execution.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(gameFH.playerTurnEndState, gameFH.getCurrent());
    }
}
