package ingsw.Client.View.Gui.GameBoardScene;

import ingsw.Server.Controller.Controller;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.NoMoreBagElementException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ingsw.Server.Dice.DiceColor.BLUE;
import static ingsw.Server.Dice.DiceColor.GREEN;
import static ingsw.Server.Dice.DiceColor.PURPLE;
import static javafx.geometry.Pos.CENTER;

/**
 * This Class takes care of rendering the DicePool in the main GameView
 */
public class DicePoolView extends Application {

    private static Controller controller;
    Pane dpPane;
    Parent root;

    public DicePoolView() {
        try {
            root = FXMLLoader.load(getClass().getResource("/View/Fxml/gameBoardLayout.fxml"));
        } catch (IOException e) {
            System.out.println("error loading main frame");
        }
    }

    public DicePoolView(Parent root) {
        dpPane = ((Pane) root.lookup("#DicePool"));
    }

    public static void setController(Controller c) {
        DicePoolView.controller = c;
    }

    /*
     * Start-method used for displaying the view as a standalone window for debugging feature
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        DicePool dpMock = new DicePool();
        dpMock.addDice(new Dice(3, PURPLE));
        dpMock.addDice(new Dice(1, BLUE));
        dpMock.addDice(new Dice(4, GREEN));
        dpMock.addDice(new Dice(3, PURPLE));
        dpMock.addDice(new Dice(1, BLUE));
        dpMock.addDice(new Dice(4, GREEN));
        dpMock.addDice(new Dice(1, BLUE));
        dpMock.addDice(new Dice(3, PURPLE));
        dpMock.addDice(new Dice(3, PURPLE));
        primaryStage.setTitle("DicePoolExemple");
        root = FXMLLoader.load(getClass().getResource("/View/Fxml/gameBoardLayout.fxml"));
        dpPane = ((Pane) root.lookup("#DicePool"));
        renderDicePool(dpMock);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     *This method renders all the Dice in the dicepool in a list and then appends them one by one in the Dicepool, if the Dice are more than 5 it adds 2 of them in a HBOX
     * after that the HBOX gets appended to the VBOX
     * @param dicePool the dicepool to be rendered
     */
    public void renderDicePool(DicePool dicePool) throws NoMoreBagElementException {
        int size = dicePool.getAllDice().size();
        int placed = 0;
        List<StackPane> rd = new ArrayList<>();
        for (int idx = 0; idx < size; idx++) {
            Dice toRender = dicePool.peekDice(idx);
            StackPane renderedDice = new DiceView().renderDice(toRender, 40);
            PlayerInteractionGameBoard.setPoolDiceHandler(renderedDice, idx, toRender);
            rd.add(renderedDice);
        }
        VBox currentSlot = new VBox(15);
        currentSlot.setAlignment(CENTER);
        currentSlot.setMinWidth(dpPane.getPrefWidth());
        HBox splitSlot;
        dpPane.getChildren().add(currentSlot);
        for (int idx = 0; idx < size; idx++) {
            splitSlot = new HBox(10);
            splitSlot.setAlignment(CENTER);
            currentSlot.getChildren().add(splitSlot);
            splitSlot.getChildren().addAll(new Label(Integer.toString(idx + 1)), rd.get(idx));
            placed ++;
            if (size > 5 && placed < size) {
                idx ++;
                placed ++;
                splitSlot.getChildren().addAll(new Label(Integer.toString(idx + 1)), rd.get(idx));
            }
        }
    }

    public void updateDicePool(DicePool dicePool) {
        PlayerInteractionGameBoard.resetSelectedDice();
        dpPane.getChildren().clear();
        try {
            renderDicePool(dicePool);
        } catch (NoMoreBagElementException e) {
            System.out.println("Error while updating dicePool");
        }
    }

}