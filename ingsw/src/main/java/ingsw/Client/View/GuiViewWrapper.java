package ingsw.Client.View;

import ingsw.Client.View.Gui.GameBoardScene.FinalResultsView;
import ingsw.Client.View.Gui.GameBoardScene.GameExceptionPopUp;
import ingsw.Client.View.Gui.GameBoardScene.NotificationDisplay;
import ingsw.Client.View.Gui.*;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Utility.GameException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * Extends the View and is responsible for launching and managing the GUI
 */
public class GuiViewWrapper extends View {

    private static final Logger log = Logger.getLogger( GuiViewWrapper.class.getName() );
    private boolean mainSceneSet = false;

    public GuiViewWrapper() throws RemoteException {
        super();
        this.conn = new GameConnection(this);
    }


    @Override
    public void run() {
        controllerSet = new CountDownLatch(1);
        initGui();
        try {
            controllerSet.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } do {
            update();
        } while (conn.isConnected());
        if (gameBoard.getCurrentState() != GameFlowState.GAMEFINISH) {
            GuiManager.close();
            Platform.runLater(() -> GameExceptionPopUp.show(new GameException("You have lost connection to the game server, please check your connection, than reconnect.")));
        }
    }

    /**
     * @param info display the DisconnectionInfo box in the lower left side of the GUI
     */
    @Override
    protected void showDisconnectionNotification(String info) {
        Platform.runLater(() -> NotificationDisplay.notification(info));
    }

    /**
     * @param info display the TimeoutInfo box in the lower left side of the GUI
     */
    @Override
    protected void showTimeoutNotification(String info) {
        Platform.runLater(() -> NotificationDisplay.notification(info));
    }

    private void updateGameBoard() {
        try {
            GameBoardView.setGameBoard(this.gameBoard);
        } catch(Exception e) {
            log.warning(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    protected void gameFinish() {
        conn.setGameFinished(true);
        if (gameBoard.getGameType() == GameType.MULTIPLAYER) {
            Platform.runLater(() -> FinalResultsView.MultiScoreboardRender(gameBoard.getScoreBoard().getPlayerOrder()));
        } else if (gameBoard.getGameType() == GameType.SINGLEPLAYER) {
            Platform.runLater(() -> FinalResultsView.SingleScoreboardRender(
                    gameBoard.getScoreBoard().getPlayerOrder(),
                    gameBoard.getScoreBoard().getRoundTrackerScore()));
        }
    }

    @Override
    protected void playerTurnEnd() {
        showMainScene();
        updateGameBoard();
    }

    @Override
    protected void toolAction() {
        showMainScene();
        updateGameBoard();
    }

    @Override
    protected void diceAction() {
        showMainScene();
        updateGameBoard();
    }

    @Override
    protected void playerTurnBegin() {
        showMainScene();
        updateGameBoard();
    }

    @Override
    protected void turnHandler() {
        showMainScene();
        updateGameBoard();
    }

    @Override
    protected void commonObjSetup() {
        //Empty methods since the client doesn't have to take any action here
    }

    @Override
    protected void toolSetup() {
        //Empty methods since the client doesn't have to take any action here
    }

    private void showMainScene() {
        if (this.mainSceneSet) {
            return;
        }
        GameBoardView.setController(c);
        GridPickView.setController(c);
        try {
            GuiManager.SceneSwitcher(new GameBoardView().mainScene(gameBoard));
            mainSceneSet = true;
        } catch (Exception e) {
            log.warning(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    protected void gridSetup() throws IOException {
        if (c.getPlayer().getPossibleGrids() == null) {
            return;
        }
        GameBoardView.setController(c);
        GridPickView.setController(c);
        List<Grid> possibilities = Arrays.asList(c.getPlayer().getPossibleGrids());
        GridPickView.setGrids(possibilities);
        try {
            GuiManager.SceneSwitcher(new GridPickView().SelectionStageRender(possibilities, c.getAllPrivateObjective()));
        } catch (Exception e) {
            log.warning(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    protected void privateObjSetup() {

    }

    @Override
    protected void setup() {

    }

    @Override
    protected void playerRegistration() {
        try {
            GuiManager.SceneSwitcher(new LobbyView().lobbyViewRender(gameBoard.getPlayers()));
        } catch (Exception e) {
            log.warning(Arrays.toString(e.getStackTrace()));
        }
    }

    private void initGui() {
        PlayerRegisterView.setGc(conn);
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel(); // initializes JavaFX environment
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        new Thread(() -> {
            try {
                new GuiManager().launchApplication();
            } catch (Exception e) {
                log.warning(Arrays.toString(e.getStackTrace()));
            }
        }).start();
    }


}
