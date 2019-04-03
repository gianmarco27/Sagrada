package ingsw.Client.View.Gui;

import ingsw.Client.View.Gui.GameBoardScene.GameExceptionPopUp;
import ingsw.Client.View.Gui.GameBoardScene.GridView;
import ingsw.Client.View.Gui.GameBoardScene.NotificationDisplay;
import ingsw.Client.View.Gui.GameBoardScene.ObjectiveView;
import ingsw.Server.Controller.Controller;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Objective.PrivateObjective;
import ingsw.Server.Utility.GameException;
import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static javafx.geometry.Pos.CENTER;

/**
 * This class is responsible for the GridPick window displaying to the user the available Grids to select and his own private Objective
 */
public class GridPickView extends Application {
    private static final Logger log = Logger.getLogger( GridPickView.class.getName() );
    private static Controller controller;
    GridView gridView = new GridView();
    Boolean gridSelected = false;

    private static List<Grid> grids;
    private boolean chosen = false;

    private static final String CLICKED_KEY = "clicked";

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("GridPick");
        primaryStage.setScene(SelectionStageRender(grids, new ArrayList<>()));
        primaryStage.show();
    }

    public Scene SelectionStageRender(List<Grid> grids, List<PrivateObjective> privObj) throws Exception {
        Pane root = FXMLLoader.load(getClass().getResource("/View/Fxml/gridSelectLayout.fxml"));
        GridPane gridHolder = (GridPane) root.lookup("#Grids");
        VBox objHolder = ((VBox) root.lookup("#privateObjective"));
        NotificationDisplay.notificationSetRoot(root);
        Double SCALE = 0.7;
        Pane gridChoice;
        HBox centeredBox;
        Button confirmBtn = (Button) root.lookup("#ConfirmButton");
        confirmBtn.setOnMouseClicked(e -> validateAndChoose(confirmBtn));
        confirmBtn.setDisable(true);
        for(int idx_x = 0; idx_x < 2; idx_x ++) {
            for (int idx_y = 0; idx_y < 2; idx_y ++) {
                gridChoice = gridView.renderGrid(grids.get(idx_x + 2*idx_y), 80, false);
                gridChoice.setScaleX(SCALE);
                gridChoice.setScaleY(SCALE);
                gridChoice.setTranslateY(-(gridChoice.getPrefHeight()*SCALE)/7);
                centeredBox = new HBox();
                centeredBox.getChildren().add(gridChoice);
                centeredBox.setAlignment(CENTER);
                gridHolder.add(centeredBox, idx_x, idx_y);
                setGridHandler(gridChoice, idx_x + 2*idx_y + 1,confirmBtn);
            }
        }

        objHolder.setSpacing(10);
        for (PrivateObjective o : privObj) {
            objHolder.getChildren().add(new ObjectiveView().renderObjective(o));
        }

         return new Scene(root, 1280, 720);
    }

    private void validateAndChoose(Button conf) {
        if (chosen) {
            return;
        }
        try {
            if (controller.chooseGrid(controller.getPlayer().getPossibleGrids()[(Integer) conf.getUserData()-1])) {
                chosen = true;
                conf.setDisable(true);
            }
        } catch (GameException e) {
            GameExceptionPopUp.show(e);
        } catch (IOException e) {
            log.warning(Arrays.toString(e.getStackTrace()));
        }
    }

    private void setGridHandler(Node toSet, int choice,Button conf) {
        toSet.setUserData(choice);
        toSet.getProperties().put(CLICKED_KEY, false);

        toSet.setOnMouseClicked(e -> {
            if (chosen) {
                return;
            }
            // Check if there is no grid selected or the clicked one was the selected one, else do nothing
            if (!gridSelected || ((Boolean) toSet.getProperties().get(CLICKED_KEY))) {
                Integer picked = (Integer) toSet.getUserData();
                // On click revert the CLICKED_KEY status
                gridSelected = !gridSelected;
                toSet.getProperties().compute(CLICKED_KEY, (k, v) -> !(Boolean) v);
                toSet.setStyle("-fx-effect:dropshadow(three-pass-box," +
                        (((Boolean) toSet.getProperties().get(CLICKED_KEY)) ? "coral" : "rgba(0, 174, 255, 1)") +
                        ", 6, 5, 0, 0);");
                if (gridSelected) {
                    conf.setUserData(picked);
                    conf.setDisable(false);
                } else {
                    conf.setDisable(true);
                }
            }
        });

        toSet.setOnMouseEntered(e -> {
            toSet.setStyle("-fx-effect: dropshadow(three-pass-box," +
                    (((Boolean) toSet.getProperties().get(CLICKED_KEY)) ? "coral" : "rgba(0, 174, 255, 1)") +
                    ", 6, 5, 0, 0);\n");
            // Checks if the CLICKED_KEY property is true and changes how the highlight colors and size are displayed
            toSet.toFront();
        });

        toSet.setOnMouseExited(e ->
                toSet.setStyle("-fx-effect: dropshadow(three-pass-box," +
                        (((Boolean) toSet.getProperties().get(CLICKED_KEY)) ? "coral, 6" : "rgba(0, 174, 255, 1), 0") +
                        ", 5, 0, 0);\n"));
        // Necessary to prevent that de-hovering alters how CLICKED_KEY is displayed

    }

    public static void setGrids(List<Grid> g) {
        grids = g;
    }

    public static void setController(Controller c) {
        controller = c;
    }
}
