package ingsw.Client.View.Gui.GameBoardScene;

import ingsw.Server.Grid.Grid;
import ingsw.Server.Player;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javafx.geometry.Pos.CENTER;

/**
 * This class takes care of rendering in their relative section the grids from the opponents, tracing an X on the disconnected players
 */
public class OpponentsView extends Application {

    Pane opPane;
    Parent root;
    private static final int diceSize = 80;
    private static final int WSpacing = 0;
    private static final Double scale = 0.4;

    public OpponentsView() {
        try {
            root = FXMLLoader.load(getClass().getResource("/View/Fxml/gameBoardLayout.fxml"));
        } catch (IOException e) {
            System.out.println("error loading main frame");
        }
    }

    public OpponentsView(Parent root) {
        opPane = ((Pane) root.lookup("#OpponentSection"));

    }

    /*
     * Start-method used for displaying the view as a standalone window for debugging feature
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Map<Player,Grid> Opponents = new HashMap();
        List<String> PlayerNames = Arrays.asList("Federico", "Giovanni", "Gianmarco", "Marco");
        for (int i = 0; i < 3; i++) {
            Player p = new Player(PlayerNames.get(i));
            p.setConnected(false);
            Opponents.put(p,p.getGrid());}

        primaryStage.setTitle("DicePoolExemple");
        root = FXMLLoader.load(getClass().getResource("/View/Fxml/gameBoardLayout.fxml"));
        opPane = ((Pane) root.lookup("#OpponentSection"));
        renderOpponents(Opponents);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void renderOpponents(Map<Player,Grid> opponents) throws Exception {
        int size = opponents.size();

        Pane renderedPane;
        HBox slots = new HBox(WSpacing);
        slots.setAlignment(CENTER);
        slots.setMaxWidth(opPane.getPrefWidth());
        slots.setMaxHeight(opPane.getPrefHeight());
        StackPane splitSlotContainer;
        VBox splitSlot;
        opPane.getChildren().add(slots);
        for (Player p : opponents.keySet()) {

            renderedPane = new GridView().renderGrid(opponents.get(p), diceSize, false);
            paneScale(renderedPane);

            splitSlotContainer = new StackPane();
            splitSlot = new VBox(0);
            slotScale(splitSlot, size);

            Label playerInfo = new Label(p.getName() + " tokens: " + p.getToken());

            playerInfo.setMinWidth(renderedPane.getMaxWidth());
            playerInfo.setAlignment(CENTER);

            splitSlot.getChildren().add(playerInfo);
            splitSlot.getChildren().add(renderedPane);
            splitSlotContainer.getChildren().add(splitSlot);

            if (!p.isConnected()) {
                disconnectionMark(splitSlot, splitSlotContainer);
                }

            slots.getChildren().add(splitSlotContainer);
        }
    }

    public void disconnectionMark(VBox splitSlot, StackPane splitSlotContainer){
        Line line1 = new Line();
        Line line2 = new Line();

        Double minX = splitSlot.getBoundsInParent().getMinX()-10;
        Double maxX = splitSlot.getBoundsInParent().getMaxX()+10;
        Double minY = splitSlot.getBoundsInParent().getMinY()-30;
        Double maxY = splitSlot.getBoundsInParent().getMaxY()+30;

        line1.setStartX(minX);
        line1.setStartY(minY);
        line1.setEndX(maxX);
        line1.setEndY(maxY);
        line1.setStrokeWidth(10);
        line1.setStroke(Color.RED);
        line1.setTranslateY(25);
        line1.setTranslateX(-5);

        line2.setStartX(maxX);
        line2.setStartY(minY);
        line2.setEndX(minX);
        line2.setEndY(maxY);
        line2.setStrokeWidth(10);
        line2.setStroke(Color.RED);
        line2.setTranslateY(25);
        line1.setTranslateX(-5);

        splitSlotContainer.getChildren().addAll(line1, line2);
    }

    public void paneScale(Pane renderedPane) {
        renderedPane.setScaleX(scale);
        renderedPane.setScaleY(scale);
        renderedPane.setTranslateX(-(renderedPane.getPrefWidth() * scale / 2) + diceSize * scale);
        renderedPane.setMaxHeight(renderedPane.getPrefHeight() * scale);
        renderedPane.setMaxWidth(renderedPane.getPrefWidth() * scale);
    }

    public void slotScale(VBox splitSlot, int size) {
        splitSlot.setMinHeight(opPane.getPrefHeight() - 50);
        splitSlot.setMinWidth(opPane.getPrefWidth() / size);
        splitSlot.setAlignment(CENTER);

    }

    public void updateOpponents(Map<Player,Grid> opponents) {
        opPane.getChildren().clear();
        try {
            renderOpponents(opponents);
        } catch (Exception e) {
            System.out.println("Error while updating Opponents");
        }
    }
}