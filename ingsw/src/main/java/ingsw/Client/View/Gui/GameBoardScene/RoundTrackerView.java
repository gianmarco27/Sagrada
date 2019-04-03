package ingsw.Client.View.Gui.GameBoardScene;

import ingsw.Server.Controller.Controller;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Utility.Coord;
import ingsw.Server.Utility.RoundTracker;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

import static ingsw.Server.Dice.DiceColor.*;
import static javafx.geometry.Pos.CENTER;

/**
 * This Class is used to render ethe roundTracker in its section in the mainScene.
 * it uses nested Vbox inside a main Hbox for each round, placing them in the Grid of the prototype
 */
public class RoundTrackerView extends Application {

    private static Controller controller;
    GridPane rtGrid;
    Pane opPane;
    Parent root;

    public RoundTrackerView() {
        try {
            root = FXMLLoader.load(getClass().getResource("/View/Fxml/gameBoardLayout.fxml"));
        } catch (IOException e) {
            System.out.println("error loading main frame");
        }
    }

    public RoundTrackerView(Parent root) {
        rtGrid = ((GridPane) root.lookup("#RoundTrackerGrid"));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setController(Controller c) {
        RoundTrackerView.controller = c;
    }

    /*
     * Start-method used for displaying the view as a standalone window for debugging feature
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        RoundTracker rtMock = new RoundTracker();
        rtMock.placeDice(0, new Dice(1, RED));
        rtMock.placeDice(1, new Dice(2, BLUE));
        rtMock.placeDice(2, new Dice(3, YELLOW));
        rtMock.placeDice(2, new Dice(4, PURPLE));
        rtMock.placeDice(2, new Dice(3, YELLOW));
        rtMock.placeDice(2, new Dice(3, YELLOW));
        rtMock.placeDice(3, new Dice(4, PURPLE));
        rtMock.placeDice(4, new Dice(5, GREEN));
        rtMock.placeDice(5, new Dice(4, GREEN));
        rtMock.placeDice(5, new Dice(2, YELLOW));
        rtMock.placeDice(5, new Dice(6, RED));
        rtMock.placeDice(5, new Dice(6, RED));
        rtMock.placeDice(5, new Dice(6, RED));
        rtMock.placeDice(6, new Dice(1, BLUE));
        rtMock.placeDice(7, new Dice(2, YELLOW));
        rtMock.placeDice(7, new Dice(2, BLUE));
        rtMock.placeDice(7, new Dice(2, YELLOW));
        rtMock.placeDice(7, new Dice(2, GREEN));
        rtMock.placeDice(7, new Dice(2, YELLOW));
        rtMock.placeDice(7, new Dice(2, PURPLE));
        rtMock.placeDice(7, new Dice(2, RED));
        rtMock.placeDice(7, new Dice(2, YELLOW));
        rtMock.placeDice(7, new Dice(2, YELLOW));
        rtMock.placeDice(8, new Dice(3, PURPLE));
        rtMock.placeDice(9, new Dice(4, GREEN));
        rtMock.placeDice(9, new Dice(1, BLUE));
        primaryStage.setTitle("RoundTrackerExemple");
        root = FXMLLoader.load(getClass().getResource("/View/Fxml/gameBoardLayout.fxml"));
        rtGrid = ((GridPane) root.lookup("#RoundTrackerGrid"));
        renderRoundTracker(rtMock);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void renderRoundTracker(RoundTracker rt) {
        VBox currentCell;
        HBox splitCell;
        ArrayList<StackPane>[] groupDice = (ArrayList<StackPane>[])new ArrayList[RoundTracker.ROUNDMAX];
        for (int round = 0; round < RoundTracker.ROUNDMAX; round++) {
            groupDice[round] = new ArrayList<>();
            int roundSize = rt.peekPosition(round).size();
            for (int idx = 0; idx < roundSize; idx++) {
                Dice toRender = rt.peekPosition(round, idx);
                StackPane renderedDice = new DiceView().renderDice(toRender, (roundSize < 4) ? 40 : (30));
                PlayerInteractionGameBoard.setRoundTrackerHandler(renderedDice, new Coord(round, idx));
                groupDice[round].add(renderedDice);
            }
        }
        for (int round = 0; round < RoundTracker.ROUNDMAX; round++) {
            int roundSize = rt.peekPosition(round).size();
            int placed = 0;
            currentCell = new VBox(4);
            rtGrid.add(currentCell, round, 0);
            for (int idx = 0; idx < roundSize; idx++) {
                splitCell = new HBox(4);
                splitCell.setAlignment(CENTER);
                currentCell.getChildren().add(splitCell);
                splitCell.getChildren().add(groupDice[round].get(idx));
                placed ++;
                if (roundSize > 5 && placed < roundSize) {
                    idx ++;
                    placed ++;
                    splitCell.getChildren().add(groupDice[round].get(idx));
                }
            }
        }
    }

    public void updateRoundTracker(RoundTracker rt) {
        rtGrid.getChildren().clear();
        renderRoundTracker(rt);
    }

}
