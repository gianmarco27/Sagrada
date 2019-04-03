package ingsw.Client.View.Gui;

import ingsw.Client.View.Gui.GameBoardScene.*;
import ingsw.Server.Controller.Controller;
import ingsw.Server.GameBoard;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * This Class is used to manage the main Game Window containing the whole Gameboard responsible of binding
 * the controller to every subsection, and keeping track of their instance call their update
 */
public class GameBoardView extends Application {
    private static final Logger log = Logger.getLogger( GameBoardView.class.getName() );
    private static Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    private static OwnGridView ownGrid;
    private static RoundTrackerView roundTrackerView;
    private static DicePoolView dicePoolView;
    private static OpponentsView opponentsView;
    private static ToolsView toolsView;
    private static ObjectivesView objectivesView;
    private static GameBoard gameBoard;
    private static Parent root;

    private static Label wishedValueView;
    private static Button wishedValue;

    private static Label statusLabel;
    private static Label diceActionLabel;
    private static Label toolActionLabel;
    private static Label yourTurnLabel;
    private static final Object lock = new Object();

    public GameBoardView() {
        try {
            root = FXMLLoader.load(getClass().getResource("/View/Fxml/gameBoardLayout.fxml"));
        } catch (IOException e) {
            log.warning("Error while loading the main frame");
        }
        ownGrid = new OwnGridView(root);
        PlayerInteractionGameBoard.setGrid(ownGrid);
        roundTrackerView = new RoundTrackerView(root);
        dicePoolView = new DicePoolView(root);
        opponentsView = new OpponentsView(root);
        toolsView = new ToolsView(root);
        objectivesView = new ObjectivesView(root);
        NotificationDisplay.notificationSetRoot(root);
        // This label is used to display the wishedValue that the user selected
        wishedValueView = (Label) root.lookup("#wishedValueLabel");
        wishedValue = (Button) root.lookup("#wishedValue");
        wishedValueView.setText("");
        PlayerInteractionGameBoard.setWishedValueHandler(wishedValue, wishedValueView);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ownGrid = new OwnGridView(root);
        PlayerInteractionGameBoard.setGrid(ownGrid);
        roundTrackerView = new RoundTrackerView(root);
        dicePoolView = new DicePoolView(root);
        opponentsView = new OpponentsView(root);
        toolsView = new ToolsView(root);
        objectivesView = new ObjectivesView(root);
        Label label = ((Label) root.lookup("#statusInfo"));
        label.setText("Round: " + (gameBoard.getCurrentTurn() + 1) + " Step " + (gameBoard.getCurrentPlayer().getTurnPlayed() + 1) + "\tTurn of " + gameBoard.getCurrentPlayer().getName() + " | T:" + gameBoard.getCurrentPlayer().getToken() + "\t State: " + gameBoard.getCurrentState());

        ownGrid.renderGrid(gameBoard.getPlayers().get(3).getGrid(), 80);
        roundTrackerView.renderRoundTracker(gameBoard.getRoundTracker());
        dicePoolView.renderDicePool(gameBoard.getDicePool());
        opponentsView.renderOpponents(gameBoard.getPlayers().subList(0, 3).stream().collect(Collectors.toMap(Function.identity(),p -> p.getGrid())));
        toolsView.renderTools(gameBoard.getTools());
        objectivesView.renderAllObj(gameBoard.getPublicObjectives(), gameBoard.getAllPrivateObjective(gameBoard.getPlayers().get(3)));

        primaryStage.setTitle("Sagrada Poggi-Schiavon-Scloza");
        primaryStage.setScene(new Scene(root, 1280, 720));
        runUpdateConsumer();
        primaryStage.show();
    }

    public Scene mainScene(GameBoard gameBoard) throws Exception {
        statusLabel = (Label) root.lookup("#statusInfo");
        yourTurnLabel = (Label) root.lookup("#yourTurnLabel");
        diceActionLabel = (Label) root.lookup("#diceActionLabel");
        toolActionLabel = (Label) root.lookup("#toolActionLabel");
        Button confirmButton = (Button) root.lookup("#confirmButton");
        Button endTurnButton = (Button)root.lookup("#endTurnButton");

        ownGrid.renderGrid(controller.getPlayer().getGrid(), 80);
        roundTrackerView.renderRoundTracker(gameBoard.getRoundTracker());
        dicePoolView.renderDicePool(gameBoard.getDicePool());
        opponentsView.renderOpponents(controller.getOthersGrids());
        toolsView.renderTools(gameBoard.getTools());
        objectivesView.renderAllObj(gameBoard.getPublicObjectives(), controller.getAllPrivateObjective());
        updateLabels();

        PlayerInteractionGameBoard.setEndTurnButtonHandler(endTurnButton);
        PlayerInteractionGameBoard.setConfirmButtonHandler(confirmButton);
        confirmButton.setOnAction(event -> {
            try {
                updateView();
            } catch (IOException e) {
                log.warning(Arrays.toString(e.getStackTrace()));
            }
        });

        runUpdateConsumer();
        return new Scene(root, 1280, 720);
    }

    private static void updateView() throws IOException {
        if (controller != null) {
            gameBoard = controller.getGameBoard();
            ownGrid.updateGrid(controller.getPlayer().getGrid(), 80);
            roundTrackerView.updateRoundTracker(gameBoard.getRoundTracker());
            dicePoolView.updateDicePool(gameBoard.getDicePool());
            opponentsView.updateOpponents(controller.getOthersGrids());
            toolsView.updateTools(gameBoard.getTools());
            objectivesView.updateObj(gameBoard.getPublicObjectives(), controller.getAllPrivateObjective());
            updateLabels();
        }
    }

    private static void updateLabels() throws IOException {
        if (gameBoard == null) {
            gameBoard = controller.getGameBoard();
        }
        statusLabel.setText("Round: " + (gameBoard.getCurrentTurn() + 1) + " STEP " + (gameBoard.getCurrentPlayer().turnPlayed + 1) +
              "\tTurn of " + gameBoard.getCurrentPlayer().getName() + " | T:" + gameBoard.getCurrentPlayer().getToken() + "\t State: " + gameBoard.getCurrentState());
        if (controller.getPlayer().equals(gameBoard.getCurrentPlayer())) {
            yourTurnLabel.setText("Available Actions:");
            diceActionLabel.setText(controller.getPlayer().isDicePlayed() ? "" : "Place a Die");
            toolActionLabel.setText(controller.getPlayer().isToolPlayed() ? "" : "Use a Tool");
        } else {
            yourTurnLabel.setText("Not Your Turn");
            diceActionLabel.setText("");
            toolActionLabel.setText("");
        }
    }

    private void runUpdateConsumer() {
        new Thread(() -> {
            boolean running = true;
            synchronized (lock) {
                while (running) {
                    try {
                        lock.wait();
                        Platform.runLater(() -> {
                            try {
                                updateView();
                            } catch (Exception e) {
                                log.info("Exception : " + e.getMessage());
                            }
                        });
                    } catch (Exception e) {
                        log.info("Exception : " + e.getMessage());
                    }
                }
           }
        }).start();
    }

    public static void setGameBoard(GameBoard gb) {
        synchronized (lock) {
            gameBoard = gb;
            lock.notifyAll();
        }
    }

    public static void setController(Controller c) {
        controller = c;
        //set controller on all sub-scene
        PlayerInteractionGameBoard.setController(c);
        OwnGridView.setController(c);
        RoundTrackerView.setController(c);
        DicePoolView.setController(c);
        ToolsView.setController(c);
        ObjectivesView.setController(c);
    }
}
