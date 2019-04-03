package ingsw.TestServer.GameFlow;

import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameFlowHandler;
import ingsw.Server.GameFlow.GameFlowStates.PlayerRegistrationState;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Player;
import ingsw.Server.Utility.GameException;
import ingsw.TestServer.Mockers.GameBoardMocker;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

import static ingsw.Server.Utility.CopyObjects.deepCopy;

public class PlayerRegistrationStateTest {
    GameFlowHandler gameFH = new GameFlowHandler();
    GameBoardMocker gbMock = new GameBoardMocker();
    GameBoard gb = gbMock.getGameBoard(2, 4);

    public PlayerRegistrationStateTest() {
        gameFH.gameBoard = gb;
        gameFH.setState(gameFH.playerRegistrationState);
    }

    @Test
    public void testMultiPlayerRegistration() {

        List<Player> players = ((CopyOnWriteArrayList) deepCopy(gameFH.gameBoard.getPlayers()));
        for (Player p : players) {
            gameFH.gameBoard.removePlayer(p);
        }

        Thread execution = new Thread(() -> gameFH.getCurrent().execute());
        execution.start();

        Thread registration = new Thread(() -> {
            while (execution.isAlive()) {
                if (execution.getState() == Thread.State.WAITING) {
                    if (gameFH.gameBoard.getPlayers().isEmpty()) {
                        for (Player p : players) {
                            ((PlayerRegistrationState) gameFH.getCurrent()).registerPlayer(p.getName(), GameType.MULTIPLAYER);
                        }
                        try {
                            ((PlayerRegistrationState) gameFH.getCurrent()).registerPlayer(players.get(0).getName(), GameType.MULTIPLAYER);
                            Assert.assertTrue(false);
                        } catch (GameException e) {
                            Assert.assertTrue(true);
                        }
                    }
                }
            }
        });
        registration.start();

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

        for (Player p : players) {
            Assert.assertEquals(true, gameFH.gameBoard.getPlayers().contains(p));
        }
        Assert.assertEquals(gameFH.setupState, gameFH.getCurrent());
    }

    @Test
    public void testSinglePlayerRegistration() {

        List<Player> players = ((CopyOnWriteArrayList) deepCopy(gameFH.gameBoard.getPlayers()));
        for (Player p : players) {
            gameFH.gameBoard.removePlayer(p);
        }

        Thread execution = new Thread(() -> gameFH.getCurrent().execute());
        execution.start();

        Thread registration = new Thread(() -> {
            while (execution.isAlive()) {
                if (execution.getState() == Thread.State.WAITING) {
                    if (gameFH.gameBoard.getPlayers().isEmpty()) {
                        ((PlayerRegistrationState) gameFH.getCurrent()).registerPlayer(players.get(0).getName(), GameType.SINGLEPLAYER);
                        try {
                            ((PlayerRegistrationState) gameFH.getCurrent()).registerPlayer(players.get(1).getName(), GameType.SINGLEPLAYER);
                            Assert.assertTrue(false);
                        } catch (GameException e) {
                            Assert.assertTrue(true);
                        }
                    }
                }
            }
        });
        registration.start();

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
        Assert.assertEquals(true, gameFH.gameBoard.getPlayers().contains(players.get(0)));
        Assert.assertEquals(gameFH.setupState, gameFH.getCurrent());
    }
}
