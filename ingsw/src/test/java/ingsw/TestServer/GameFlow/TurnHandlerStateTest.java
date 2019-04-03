package ingsw.TestServer.GameFlow;

import ingsw.Server.Dice.DiceBag;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Player;
import ingsw.Settings;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Test;

public class TurnHandlerStateTest {

    GameFlowHandler gameFH = new GameFlowHandler();
    GameBoardMocker gbMock = new GameBoardMocker();
    GameBoard gb = gbMock.getGameBoard(1, 4);

    public TurnHandlerStateTest() {
        gameFH.gameBoard = gb;
        gameFH.setState(gameFH.turnHandlerState);
        gameFH.setGameType(GameType.MULTIPLAYER);
        gameFH.diceBag = new DiceBag();
    }

    @Test
    public void testGameFinish() {
        for (int round = 0; round < Settings.TURN_NUMBER; round ++) {
            gb.nextTurn();
        }
        gameFH.getCurrent().execute();

        Assert.assertEquals(gameFH.gameFinishState, gameFH.getCurrent());
    }

    @Test
    public void testGameEndOnDisconnection() {
        for (Player p : gameFH.gameBoard.getPlayers()) {
            if (p != gameFH.gameBoard.getCurrentPlayer())
                gameFH.disconnectPlayer(p, false);
        }
        gameFH.getCurrent().execute();

        Assert.assertEquals(gameFH.gameFinishState, gameFH.getCurrent());
    }

    @Test
    public void testRoundEnd() {
        gameFH.setState(gameFH.turnHandlerState);

        for (Player p : gameFH.gameBoard.getPlayers()) {
            gameFH.gameBoard.nextPlayer();
            gameFH.gameBoard.nextPlayer();
        }

        Thread execution = new Thread(() -> {
            gameFH.getCurrent().execute();
        });
        execution.start();

        Thread playersAck = new Thread(() -> {
            while (execution.isAlive()) {
                if (execution.getState() == Thread.State.TIMED_WAITING) {
                    for (Player p : gb.getActivePlayers()) {
                        p.setConnected(true);
                        gameFH.getCurrent().ack(p);
                    }
                }
            }
        });
        playersAck.start();

        try {
            execution.join();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(gameFH.playerTurnBeginState, gameFH.getCurrent());
    }


    @Test
    public void testTurnBegin() {

        gameFH.startOfTurn = true;

        Thread execution = new Thread(() -> {
            gameFH.getCurrent().execute();
        });
        execution.start();


        Thread playersAck = new Thread(() -> {
            while (execution.isAlive()) {
                if (execution.getState() == Thread.State.TIMED_WAITING) {
                    for (Player p : gb.getActivePlayers()) {
                        p.setConnected(true);
                        gameFH.getCurrent().ack(p);
                    }
                }
            }
        });
        playersAck.start();

        try {
            execution.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(9, gb.getDicePool().getAllDice().size());
        Assert.assertEquals(gameFH.playerTurnBeginState, gameFH.getCurrent());
    }

    @Test
    public void testPlayerSwitch() {
        gameFH.startOfTurn = false;

        gameFH.diceBag = new DiceBag();

        gameFH.getCurrent().execute();

        Assert.assertEquals(gameFH.playerTurnBeginState, gameFH.getCurrent());
    }
}
